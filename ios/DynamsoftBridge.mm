#import <UIKit/UIKit.h>
#import "DynamsoftBridge.h"
#import <DynamsoftCore/DynamsoftCore.h>
#import <DynamsoftLicense/DynamsoftLicense.h>
#import <DynamsoftCameraEnhancer/DynamsoftCameraEnhancer.h>
#import <DynamsoftCaptureVisionRouter/DynamsoftCaptureVisionRouter.h>
#import <DynamsoftImageProcessing/DynamsoftImageProcessing.h>
#import <DynamsoftDocumentNormalizer/DynamsoftDocumentNormalizer.h>

@interface DynamsoftBridge() <DSLicenseVerificationListener>

@end

@implementation DynamsoftBridge
{
    RCTPromiseResolveBlock licenseResolve;
    RCTPromiseRejectBlock licenseReject;
}
RCT_EXPORT_MODULE()

// Example method
// See // https://reactnative.dev/docs/native-modules-ios
RCT_EXPORT_METHOD(multiply:(double)a
                  b:(double)b
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    NSNumber *result = @(a * b);

    resolve(result);
}

RCT_EXPORT_METHOD(setLicenceKey:(NSString *)licenseKey
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    licenseResolve = resolve;
    licenseReject = reject;
    [DSLicenseManager initLicense:licenseKey verificationDelegate:self];
}
- (void)onLicenseVerified:(bool)isSuccess error:(NSError *)error{
    if(isSuccess){
        licenseResolve(@(YES));
    }else{
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:error.userInfo options:0 error:nil];
        NSString *msg = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        NSError *err = [NSError errorWithDomain:@"com.dynamsoft.license.error" code:error.code userInfo:nil];
        NSString *code = [NSString stringWithFormat:@"%ld",(long)error.code];
        licenseReject(code, msg, err);
    }
}

RCT_EXPORT_METHOD(scanWithConfiguration:(NSDictionary* )options
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    // dummy function. todo implement later
    resolve(@(NO));
}

// ios only function
RCT_EXPORT_METHOD(normalizeFromFile:(NSString *)uri
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    DSCaptureVisionRouter *router = [[DSCaptureVisionRouter alloc] init];
    NSError *error = nil;
    DSNormalizedImagesResult *result = ((DSNormalizedImagesResult*) [router captureFromFile:[NSURL fileURLWithPath:uri].path templateName:DSPresetTemplateDetectAndNormalizeDocument error:&error]);
    if (error != nil) return [self rejectWithError:error rejector:reject];
    else if(result.items.count == 0) {
        error = [NSError errorWithDomain:@"ERROR_NO_RESULTS" code: 0 userInfo:nil];
        return [self rejectWithError:error rejector:reject];
    }
    NSString *path = [self saveImageDataToFile: result.items[0].imageData error:&error];
    if (error != nil) {
        return [self rejectWithError:error rejector:reject];
    }
    resolve(path);
    return;
}
-(void)rejectWithError: (NSError *)error rejector:(RCTPromiseRejectBlock)reject {
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:error.userInfo options:0 error:nil];
    NSString *msg = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    NSString *code = [NSString stringWithFormat:@"%ld",(long)error.code];
    reject(code, msg, error);
}
-(NSString *)saveImageDataToFile: (DSImageData *)imageData error:(NSError **)error {
    UIImage *image = [imageData toUIImage:error];
    if (*error != nil) { return @""; }
    NSString *timeInMS = [NSString stringWithFormat:@"%lld", [@(floor([[NSDate date] timeIntervalSince1970] * 1000)) longLongValue]];
    NSString *fileName = [NSString stringWithFormat:@"ScanResult-%@.jpg", timeInMS];
    NSString *path = [[NSTemporaryDirectory() stringByStandardizingPath] stringByAppendingPathComponent:fileName];
    if (![UIImagePNGRepresentation(image) writeToFile:path atomically:YES]) {
        *error = [NSError errorWithDomain:@"ERROR_WRITING_TO_FILE" code: 0 userInfo:nil];
        return @"";
    }
    return path;
}

@end
