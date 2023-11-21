// @ts-nocheck
import {
  NativeModules,
  Platform,
  requireNativeComponent,
  UIManager,
} from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-dynasoft' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const DynasoftBridge = NativeModules.DynasoftBridge
  ? NativeModules.DynasoftBridge
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

function multiply(a, b) {
  return DynasoftBridge.multiply(a, b);
}

export default {
  multiply,
  setLicenceKey: DynasoftBridge.setLicenceKey,
  scanWithConfiguration: DynasoftBridge.scanWithConfiguration,
};
