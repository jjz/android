package com.example.jjz.camera;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class CustomCameraActivity extends Activity implements SurfaceHolder.Callback,
        CameraManager.CamOpenOverCallback {

    public static File file;

    private SurfaceView surfaceView;


    private SurfaceHolder surfaceHolder;
    private boolean isAvailable = true;


    private ImageView ivPreview;

    private ImageView ivLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera);

        CameraManager.init(getApplication());

        file = getExternalCacheDir();
        Toast.makeText(this,file.getAbsolutePath(),Toast.LENGTH_LONG).show();
        initUI();
        surfaceHolder = surfaceView.getHolder();

        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {

            CameraManager.get().openAndStartPreview(surfaceHolder);
        } catch (Exception e) {
            isAvailable = false;


        }

    }


    private void initUI() {
        ImageView mTakeBtn = (ImageView) findViewById(R.id.takepicture);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        ivPreview = (ImageView) findViewById(R.id.iv_preview);

        ivLeft = (ImageView) findViewById(R.id.left_image);
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivPreview.getVisibility() != View.GONE) {
                    ivPreview.setVisibility(View.GONE);
                }
                if (isAvailable) {
                    if (!CameraManager.get().isPreviewing()) {
                        CameraManager.get().rePreview();
                    } else {

                        CameraManager.get().doTakePicture();

                    }
                } else {

                }

            }
        });

    }

    private void initViewParams() {
        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;
        //  previewRate = DisplayUtil.getScreenRate(this); // 默认全屏的比例预览
        surfaceView.setLayoutParams(params);

    }

    @Override
    public void cameraHasOpened() {

    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        initCamera(holder);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        CameraManager.get().stopPreview();
        CameraManager.get().closeDriver();


    }

}
