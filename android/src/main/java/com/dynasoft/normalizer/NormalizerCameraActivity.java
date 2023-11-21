package com.dynasoft.normalizer;

import com.dynasoft.R;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dynamsoft.dce.CameraView;
import com.dynamsoft.dce.CameraEnhancer;
import com.dynamsoft.dce.CameraEnhancerException;
import com.dynamsoft.dce.utils.PermissionUtil;
import com.dynamsoft.cvr.CaptureVisionRouter;
import com.dynamsoft.cvr.CaptureVisionRouterException;

import com.dynamsoft.core.basic_structures.CapturedResultReceiver;
import com.dynamsoft.core.basic_structures.ImageData;
// import com.dynamsoft.core.basic_structures.Quadrilateral;
import com.dynamsoft.core.basic_structures.EnumCapturedResultItemType;
import com.dynamsoft.cvr.EnumPresetTemplate;
import com.dynamsoft.ddn.NormalizedImagesResult;
import com.dynamsoft.utility.MultiFrameResultCrossFilter;

public class NormalizerCameraActivity extends AppCompatActivity {
  private CameraEnhancer mCamera;
  private CaptureVisionRouter mRouter;
  private boolean mJumpToOtherActivity = false;
  private String mCacheDirectory;
  
  public static ImageData mNormalizedImageData;
  public static ImageData mOriginalImageData;
//   public static Quadrilateral mNormalizedImageLocation;

  static final int PREVIEW_REQUEST = 2;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scanner);

    mCacheDirectory = getIntent().getStringExtra("CacheDirectory");

    // Initialize Camera Module
    CameraView cameraView = findViewById(R.id.camera_view);
    cameraView.setTorchButtonVisible(true);
    mCamera = new CameraEnhancer(cameraView, NormalizerCameraActivity.this);

    PermissionUtil.requestCameraPermission(NormalizerCameraActivity.this);

    // Initialize Capture Vision Router
    mRouter = new CaptureVisionRouter(NormalizerCameraActivity.this);
    try {
          mRouter.setInput(mCamera);
    } catch (CaptureVisionRouterException e) {
          e.printStackTrace();
    }
    
    // Add a Captured Result Receiver and Filter
    mRouter.addResultReceiver(new CapturedResultReceiver() {
      @Override
      public void onNormalizedImagesReceived(NormalizedImagesResult result) {
         if (mJumpToOtherActivity && result.getItems().length > 0) {
            mJumpToOtherActivity = false;

            //  You can use this ID to get the original image via IntermediateResultManager class.
            mOriginalImageData = mRouter.getIntermediateResultManager().getOriginalImage(result.getOriginalImageHashId());
            
            mNormalizedImageData = result.getItems()[0].getImageData(); // do I need this?

            // mNormalizedImageLocation = result.getItems()[0].getLocation();

            Intent intent = new Intent(NormalizerCameraActivity.this, ResultActivity.class);
            intent.putExtra("CacheDirectory", mCacheDirectory);
            startActivityForResult(intent, PREVIEW_REQUEST);
         }
      }
    });

    // Add a result cross filter to validate the normalized image result across multiple frames.
    MultiFrameResultCrossFilter filter = new MultiFrameResultCrossFilter();
    filter.enableResultCrossVerification(EnumCapturedResultItemType.CRIT_NORMALIZED_IMAGE, true);
    mRouter.addResultFilter(filter);
  }


  public void onCaptureBtnClick(View v) {
    // Add onCaptureBtnClick function to start the video document normalization. After start capturing, the SDK will process
    // the video frames from the Camera Enhancer, then send the normalized image results to the registered result receiver.
    mJumpToOtherActivity = true;
  }


  @Override
   public void onResume() {
      super.onResume();
      try {
            mCamera.open();
            mRouter.startCapturing(EnumPresetTemplate.PT_DETECT_AND_NORMALIZE_DOCUMENT);
      } catch (CameraEnhancerException | CaptureVisionRouterException e) {
            e.printStackTrace();
      }
   }

   @Override
   public void onPause() {
      super.onPause();
      try {
            mCamera.close();
      } catch (CameraEnhancerException e) {
            e.printStackTrace();
      }

      mRouter.stopCapturing();
   }

   protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
      if (requestCode == PREVIEW_REQUEST) {
            Intent returnIntent = new Intent();
            returnIntent.setData(intent.getData());
            setResult(resultCode, returnIntent);
            finish();
            // todo handle retake press result code
            // if (resultCode == Activity.RESULT_OK) {
            // } else if () {

            // }
        }
   }

}
