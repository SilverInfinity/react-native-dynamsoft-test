
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNDynamsoftBridgeSpec.h"

@interface DynamsoftBridge : NSObject <NativeDynamsoftBridgeSpec>
#else
#import <React/RCTBridgeModule.h>

@interface DynamsoftBridge : NSObject <RCTBridgeModule>
#endif

@end
