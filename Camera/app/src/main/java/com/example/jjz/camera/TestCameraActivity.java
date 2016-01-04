package com.example.jjz.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class TestCameraActivity extends Activity implements OnClickListener, SurfaceHolder.Callback {
    private static final int update_time = 100;

    private Camera camera;
    private Bundle bundle = null;
    private Button btnCamera;
    private Button btnReCamera;
    private Button btnOk;
    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private TextView tvLon;
    private TextView tvLat;

    private AlertDialog alertDialog;
    private String path;

    private ProgressDialog progressDialog;

    private boolean hasLocation = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent dataIntent = getIntent();

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 拍照过程屏幕一直处于高亮
        setContentView(R.layout.custom_camera);
        initView();
        initListenrer();


    }

    private void initView() {
        btnCamera = (Button) findViewById(R.id.camera_photo);
        btnCamera.setOnClickListener(this);


        btnOk = (Button) findViewById(R.id.btn_ok);
        btnReCamera = (Button) findViewById(R.id.btn_re_camera);
        tvLat = (TextView) findViewById(R.id.tv_lat);
        tvLon = (TextView) findViewById(R.id.tv_lon);


        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        holder = surfaceView.getHolder();// 获得句柄

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this);// 为SurfaceView的句柄添加一个回调函数

    }

    private void initListenrer() {
        btnReCamera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.startPreview();
                btnReCamera.setVisibility(View.GONE);
                btnOk.setVisibility(View.GONE);
                btnCamera.setVisibility(View.VISIBLE);

            }
        });
        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private final class MyPictureCallback implements PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                bundle = new Bundle();
                bundle.putByteArray("bytes", data); // 将图片字节数据保存在bundle当中，实现数据交换
                new MyAsyncTask().execute(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
    public static int getPreviewDegree(Activity activity) {
        // 获得手机的方向
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0://竖屏
                degree = 90;
                break;
            case Surface.ROTATION_90://横屏
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_photo:
                // 快门
                camera.autoFocus(new AutoFocusCallback() {// 自动对焦
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            progressDialog = new ProgressDialog(TestCameraActivity.this);
                            progressDialog.setCancelable(false);
                            progressDialog.show();


                            // 设置参数，并拍照
                            Parameters params = camera.getParameters();
                            params.setPictureFormat(PixelFormat.JPEG);// 图片格式
                            List<Camera.Size> sizes = params.getSupportedPreviewSizes();
                            Camera.Size s = sizes.get(0);
                            params.setPictureSize(s.width, s.height);// 图片大小
                            camera.setParameters(params);// 将参数设置到我的camera
                            camera.takePicture(null, null, new MyPictureCallback());// 将拍摄到的照片给自定义的对象
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    public class MyAsyncTask extends AsyncTask<byte[], Void, String> {

        @Override
        protected String doInBackground(byte[]... params) {
            File file = null;

            try {
                Bitmap rotaBitmap = BitmapFactory.decodeByteArray(params[0], 0, params[0].length);
                file = new File(getExternalCacheDir(), "test" + System.currentTimeMillis() + ".jpg");
                rotaBitmap.compress(Bitmap.CompressFormat.JPEG, 90, new FileOutputStream(file));


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (file == null) {
                return "";
            }
            return file.getAbsolutePath();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            path = result;
            btnOk.setVisibility(View.VISIBLE);
            btnReCamera.setVisibility(View.VISIBLE);
            btnCamera.setVisibility(View.INVISIBLE);

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 当surfaceview创建时开启相机

        if (camera == null) {
            camera = Camera.open();
            try {
                Parameters p = camera.getParameters();
                p.setFlashMode(Parameters.FLASH_MODE_AUTO);
                camera.setParameters(p);
                camera.setPreviewDisplay(holder);// 通过surfaceview显示取景画面
                camera.setDisplayOrientation(getPreviewDegree(this));
                camera.startPreview();// 开始预览
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 当surfaceview关闭时，关闭预览并释放资源
        if (camera != null) {
            camera.stopPreview();
            camera.release(); // 释放照相机
            camera = null;
        }

        holder = null;
        surfaceView = null;
    }

    private void showDialog() {
        if (alertDialog == null) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("定位中...");
            alertDialog = dialog.create();
        }

        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }


}
