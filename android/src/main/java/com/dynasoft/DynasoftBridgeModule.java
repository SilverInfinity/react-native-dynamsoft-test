package com.dynasoft;

import com.dynamsoft.license.LicenseManager;

import com.dynasoft.normalizer.NormalizerCameraActivity;
import com.dynasoft.normalizer.ResultActivity;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.util.Log;
import android.app.Activity;
import android.net.Uri;

import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.*;

@ReactModule(name = DynasoftBridgeModule.NAME)
public class DynasoftBridgeModule extends ReactContextBaseJavaModule {
  public static final String NAME = "DynasoftBridge";
  public static final String TAG = "DynasoftBridge";
  private final ReactApplicationContext reactContext;
  
  private Promise mScanPromise;
  static final int SCAN_REQUEST = 1;  // The request code
  private static final String E_PICKER_CANCELLED = "E_PICKER_CANCELLED";
  private static final String E_FAILED_TO_SHOW_PICKER = "E_FAILED_TO_SHOW_PICKER";
  private static final String E_NO_IMAGE_DATA_FOUND = "E_NO_IMAGE_DATA_FOUND";
  private static final String E_FILE_SAVE_FAILED = "E_FILE_SAVE_FAILED";

  public DynasoftBridgeModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    reactContext.addActivityEventListener(mActivityEventListener);
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

  @ReactMethod
  public void scanWithConfiguration(ReadableMap scanOptions, Promise promise) {
    mScanPromise = promise;
    String cacheDirectory = this.getReactApplicationContext().getCacheDir().getAbsolutePath();

    Intent intent = new Intent(getCurrentActivity(), NormalizerCameraActivity.class);
    intent.putExtra("CacheDirectory", cacheDirectory);
    getCurrentActivity().startActivityForResult(intent, SCAN_REQUEST);
  }

  private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
    
    @Override
    public void onActivityResult(final Activity activity, final int requestCode, final int resultCode, final Intent intent) {
      if (requestCode == SCAN_REQUEST && mScanPromise != null) {
        Log.i(TAG, "got scan response");
        if (resultCode == Activity.RESULT_CANCELED) {
          mScanPromise.reject(E_PICKER_CANCELLED, "Image picker was cancelled");
        } else if (resultCode == Activity.RESULT_OK) {
          Uri uri = intent.getData();
          if (uri == null) {
            mScanPromise.reject(E_NO_IMAGE_DATA_FOUND, "No image data found");
          } else {
            mScanPromise.resolve(uri.toString());
          }
        } else if (resultCode == ResultActivity.FILE_SAVE_FAILED) {
          mScanPromise.reject(E_FILE_SAVE_FAILED, "Failed to save file");
        } else {
          Log.i(TAG, "Unhandled Result onActivityResult code: " + resultCode);
        }
      }
    }
  };

}
