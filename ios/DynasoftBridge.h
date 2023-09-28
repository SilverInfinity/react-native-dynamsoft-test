
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNDynasoftBridgeSpec.h"

@interface DynasoftBridge : NSObject <NativeDynasoftBridgeSpec>
#else
#import <React/RCTBridgeModule.h>

@interface DynasoftBridge : NSObject <RCTBridgeModule>
#endif

@end
