package com.dynasoft;

import com.dynamsoft.license.LicenseManager;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

import android.util.Log;

@ReactModule(name = DynasoftBridgeModule.NAME)
public class DynasoftBridgeModule extends ReactContextBaseJavaModule {
  public static final String NAME = "DynasoftBridge";
  public static final String TAG = "DynasoftBridge";
  private final ReactApplicationContext reactContext;

  public DynasoftBridgeModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }


  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    promise.resolve(a * b);
  }

  /*
  public void scanWithConfiguration(ReadableMap scanOptions, Promise promise) { 

  }
  */

  @ReactMethod
  public void setLicenceKey(String licenseKey, Promise promise) {
    LicenseManager.initLicense(licenseKey, this.reactContext, (isSuccess, error) -> {
      if (!isSuccess) {
        Log.e(TAG, "InitLicense Error: " + error);
        promise.reject(error);
      }
      promise.resolve(isSuccess);
    });
  }
}
