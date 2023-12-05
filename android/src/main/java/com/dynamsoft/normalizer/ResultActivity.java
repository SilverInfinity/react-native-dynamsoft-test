package com.dynamsoft.normalizer;

import com.dynamsoft.R;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import com.dynamsoft.core.basic_structures.CoreException;
import com.dynamsoft.core.basic_structures.ImageData;

public class ResultActivity extends AppCompatActivity{
  private static String mCacheDirectory;
  public static int FILE_SAVE_FAILED = 2;
  private ImageData mImageData;
  private String mState = "normalized";
  private String mStateChangeButtonText = "Original";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_result);

     mCacheDirectory = getIntent().getStringExtra("CacheDirectory");
     mState = "normalized";
     mImageData = NormalizerCameraActivity.mNormalizedImageData;
     renderImageView();
  }

  public void renderImageView() {
     ImageView ivImagePreview = findViewById(R.id.iv_image_preview);
     Button btnChangeSource = findViewById(R.id.btn_changeSource);
     btnChangeSource.setText(mStateChangeButtonText);
     try {
           ivImagePreview.setImageBitmap(mImageData.toBitmap());
     } catch (CoreException e) {
           e.printStackTrace();
     }
  }

  public void onImageSourceChange(View v) {
      if (mState == "normalized") {
         mImageData = NormalizerCameraActivity.mOriginalImageData;
         mStateChangeButtonText = "Cropped";
         mState = "original";
      } else {
         mImageData = NormalizerCameraActivity.mNormalizedImageData;
         mStateChangeButtonText = "Original";
         mState = "normalized";
      }
      renderImageView();
  }

/*
Not implemented Yet
public void onRetakeBtnClick(View v) {
// Add onCaptureBtnClick function to start the video document normalization. After start capturing, the SDK will process
// the video frames from the Camera Enhancer, then send the normalized image results to the registered result receiver.
// mJumpToOtherActivity = true;
// go back
}
*/
  public void onSaveBtnClick(View v) {
        Intent returnIntent = new Intent(); 

      try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            mImageData.toBitmap().compress(Bitmap.CompressFormat.JPEG, 40, bytes);
            String fileName = "ScanResult-" + System.currentTimeMillis() + ".jpg";
            File normalizedImageFile = new File(mCacheDirectory + File.separator + fileName);
            normalizedImageFile.createNewFile();
            FileOutputStream fo = new FileOutputStream(normalizedImageFile);
            fo.write(bytes.toByteArray());
            fo.close();
      
            returnIntent.setData(Uri.fromFile(normalizedImageFile));
            setResult(Activity.RESULT_OK, returnIntent); 
            finish();
      } catch (IOException|CoreException e) {
            setResult(FILE_SAVE_FAILED, returnIntent); 
            finish();
      }
    }
}
