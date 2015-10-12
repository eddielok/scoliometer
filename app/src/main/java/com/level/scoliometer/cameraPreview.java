package com.level.scoliometer;

import java.io.IOException;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.Toast;



public class cameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;



    public cameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.setFixedSize(600, 600);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.

        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.set("orientation", "landscape");
            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
          //  mCamera.setDisplayOrientation(180);
        } catch (IOException e) {
        	
            Log.d("CameraView", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    	mCamera.release();
    	mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            Camera.Parameters parameters = mCamera.getParameters();// ?±o¬ÛÉó‰ë?
            Camera.Size s = parameters.getPictureSize();
            double nw = s.width;
            double nh = s.height;
            if (w > h)
            {
             //   surfaceView.setLayoutParams(new LinearLayout.LayoutParams( (int)(h*(nw/nh)), h);
            }
            parameters.setPreviewSize(w, h);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
          //  mCamera.setDisplayOrientation(180);
        } catch (Exception e){
            Log.d("CameraView", "Error starting camera preview: " + e.getMessage());
        }
    }

    public void onPause() {
    	mCamera.stopPreview();
    	mCamera.release();
    	mCamera = null;
    }
}