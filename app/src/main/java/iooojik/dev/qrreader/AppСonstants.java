package iooojik.dev.qrreader;

import android.annotation.SuppressLint;
import android.hardware.Camera;

//класс с константами
public class AppСonstants {
    public static final int RC_HANDLE_GMS = 9001;
    // код разрешения
    public static final int RC_HANDLE_CAMERA_PERM = 2;
    //автофокус
    public static final String AutoFocus = "AutoFocus";
    //фонарик
    public static final String UseFlash = "UseFlash";
    @SuppressLint("InlinedApi")
    public static final int CAMERA_FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    @SuppressLint("InlinedApi")
    public static final int CAMERA_FACING_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;
    public static final String TAG = "OpenCameraSource";
    public static final int DUMMY_TEXTURE_NAME = 100;
    public static final float ASPECT_RATIO_TOLERANCE = 0.01f;

}
