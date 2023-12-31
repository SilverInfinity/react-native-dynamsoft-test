// @ts-nocheck
import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-dynamsoft' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const DynamsoftBridge = NativeModules.DynamsoftBridge
  ? NativeModules.DynamsoftBridge
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

function multiply(a, b) {
  return DynamsoftBridge.multiply(a, b);
}

function setLicenceKey(licenseKey) {
  return DynamsoftBridge.setLicenceKey(licenseKey);
}

function scanWithConfiguration(options) {
  if (Platform.OS === 'ios') {
    throw new Error('scanWithConfiguration is not implemented for iOS');
  }
  return DynamsoftBridge.scanWithConfiguration(options);
}

function normalizeFromFile(uri) {
  if (Platform.OS === 'android') {
    throw new Error('scanWithConfiguration is not implemented for Android');
  }
  return DynamsoftBridge.normalizeFromFile(uri);
}

export default {
  multiply,
  setLicenceKey,
  scanWithConfiguration,
  normalizeFromFile,
};
