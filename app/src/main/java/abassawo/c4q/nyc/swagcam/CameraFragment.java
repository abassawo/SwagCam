package abassawo.c4q.nyc.swagcam;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import static android.hardware.Camera.*;

/**
 * Created by c4q-Abass on 2/1/16.
 */
public class CameraFragment extends Fragment {
    private Preview mPreview;
    Camera camera;
    int cameraCount;
    int mCameraLocked;
    //1ST rear-facing camera
    int mDefaultCameraId;
    int mFrontCameraId;

    public static Fragment getInstance() {
        return new CameraFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreview = new Preview(this.getActivity());
        cameraCount = getNumberOfCameras();
        setHasOptionsMenu(cameraCount > 1);

        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i <cameraCount ; i++) {
            Camera.getCameraInfo(i,cameraInfo);
            if(cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK)
                mDefaultCameraId = i;
            if(cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT ) mFrontCameraId = i;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return mPreview;
    }

    @Override
    public void onResume() {
        super.onResume();
        camera = Camera.open(mDefaultCameraId);
        mCameraLocked = mDefaultCameraId;
//        camera = Camera.open(mFrontCameraId);
//        mCameraLocked = mFrontCameraId;
        mPreview.setCamera(camera);
    }


    @Override
    public void onPause() {
        super.onPause();
        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (camera != null) {
            mPreview.setCamera(null);
            camera.release();
            camera = null;
        }
    }


//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        Activity activity = this.getActivity();
//        ActionBar actionBar = activity.getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (cameraCount > 1) {
            // Inflate our menu which can gather user input for switching camera
            inflater.inflate(R.menu.camera_menu, menu);
        } else {
            super.onCreateOptionsMenu(menu, inflater);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.switch_cam:
                // Release this camera -> mCameraCurrentlyLocked
                if (camera != null) {
                    camera.stopPreview();
                    mPreview.setCamera(null);
                    camera.release();
                    camera = null;
                }
                // Acquire the next camera and request Preview to reconfigure
                // parameters.
                camera = Camera
                        .open((cameraCount + 1) % cameraCount);
                mCameraLocked = (mCameraLocked + 1)
                        % cameraCount;
                mPreview.switchCamera(camera);
                // Start the preview
                camera.startPreview();
                return true;
            case android.R.id.home:
//                Intent intent = new Intent(this.getActivity(), MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
