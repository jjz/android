/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jjz.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * This object wraps the Camera service object and expects to be the only one talking to it. The
 * implementation encapsulates the steps needed to take preview-sized images, which are used for
 * both preview and decoding.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CameraManager {

    private static final String TAG = CameraManager.class.getSimpleName();
    private Parameters mParams;
    private static final int MIN_FRAME_WIDTH = 240;
    private static final int MIN_FRAME_HEIGHT = 240;
    private static final int MAX_FRAME_WIDTH = 480;
    private static final int MAX_FRAME_HEIGHT = 360;

    private static CameraManager cameraManager;

    private boolean isPreviewing = false;

    static final int SDK_INT; // Later we can use Build.VERSION.SDK_INT

    static {
        int sdkInt;
        try {
            sdkInt = Integer.parseInt(Build.VERSION.SDK);
        } catch (NumberFormatException nfe) {
            // Just to be safe
            sdkInt = 10000;
        }
        SDK_INT = sdkInt;
    }

    private final Context context;
    private final CameraConfigurationManager configManager;
    private static Camera camera;
    private Rect framingRect;
    private Rect framingRectInPreview;
    private boolean initialized;
    //是否处于预览状态
    private boolean previewing;
    private final boolean useOneShotPreviewCallback;
    /**
     * Preview frames are delivered here, which we pass on to the registered handler. Make sure to
     * clear the handler so it will only receive one message.
     */
    private final PreviewCallback previewCallback;
    /**
     * Autofocus callbacks arrive here, and are dispatched to the Handler which requested them.
     */
    private final AutoFocusCallback autoFocusCallback;

    private static final File parentPath = Environment.getExternalStorageDirectory();
    private static String storagePath = "";
    private static final String DST_FOLDER_NAME = "ETCP";

    /**
     * Initializes this static object with the Context of the calling Activity.
     *
     * @param context The Activity which wants to use the camera.
     */
    public static void init(Context context) {
        if (cameraManager == null) {
            cameraManager = new CameraManager(context);
        }
    }

    /**
     * Gets the CameraManager singleton instance.
     *
     * @return A reference to the CameraManager singleton.
     */
    public static CameraManager get() {
        return cameraManager;
    }

    private CameraManager(Context context) {

        this.context = context;
        this.configManager = new CameraConfigurationManager(context);

        // Camera.setOneShotPreviewCallback() has a race condition in Cupcake, so we use the older
        // Camera.setPreviewCallback() on 1.5 and earlier. For Donut and later, we need to use
        // the more efficient one shot callback, as the older one can swamp the system and cause it
        // to run out of memory. We can't use SDK_INT because it was introduced in the Donut SDK.
        //useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) > Build.VERSION_CODES.CUPCAKE;
        useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) > 3; // 3 = Cupcake

        previewCallback = new PreviewCallback(configManager, useOneShotPreviewCallback);
        autoFocusCallback = new AutoFocusCallback();
    }

    /**
     * Opens the camera driver and initializes the hardware parameters.
     *
     * @param holder The surface object which the camera will draw preview frames into.
     * @throws IOException Indicates the camera driver failed to open.
     */
    public void openDriver(SurfaceHolder holder, CamOpenOverCallback callback) {
        try {
            camera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
        }

        if (!initialized) {
            initialized = true;
            configManager.initFromCameraParameters(camera);
        }
        configManager.setDesiredCameraParameters(camera);

        //     SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //�Ƿ�ʹ��ǰ��
        //      if (prefs.getBoolean(PreferencesActivity.KEY_FRONT_LIGHT, false)) {
        //        FlashlightManager.enableFlashlight();
        //      }
        //    FlashlightManager.enableFlashlight();
        //    callback.cameraHasOpened();

        //		}
    }

    /**
     * Closes the camera driver if still in use.
     */
    public void closeDriver() {
        if (camera != null) {
            //      FlashlightManager.disableFlashlight();
            camera.release();
            camera = null;
        }
    }

    /**
     * Asks the camera hardware to begin drawing preview frames to the screen.
     */
    public void startPreview() {
        //开始预览时 处于预览状态
        if (camera != null && !previewing) {
            camera.startPreview();
            previewing = true;
        }
    }

    /**
     * Tells the camera to stop drawing preview frames.
     */
    public void stopPreview() {
        if (camera != null && previewing) {
            if (!useOneShotPreviewCallback) {
                camera.setPreviewCallback(null);
            }
            camera.stopPreview();
            previewCallback.setHandler(null, 0);
            autoFocusCallback.setHandler(null, 0);
            previewing = false;
        }
    }

    /**
     * A single preview frame will be returned to the handler supplied. The data will arrive as byte[]
     * in the message.obj field, with width and height encoded as message.arg1 and message.arg2,
     * respectively.
     *
     * @param handler The handler to send the message to.
     * @param message The what field of the message to be sent.
     */
    public void requestPreviewFrame(Handler handler, int message) {
        if (camera != null && previewing) {
            previewCallback.setHandler(handler, message);
            if (useOneShotPreviewCallback) {
                camera.setOneShotPreviewCallback(previewCallback);
            } else {
                camera.setPreviewCallback(previewCallback);
            }
        }
    }

    /**
     * Asks the camera hardware to perform an autofocus.
     *
     * @param handler The Handler to notify when the autofocus completes.
     * @param message The message to deliver.
     */
    public void requestAutoFocus(Handler handler, int message) {
        if (camera != null && previewing) {
            autoFocusCallback.setHandler(handler, message);
            //Log.d(TAG, "Requesting auto-focus callback");
            camera.autoFocus(autoFocusCallback);
        }
    }

    /**
     * Calculates the framing rect which the UI should draw to show the user where to place the
     * barcode. This target helps with alignment as well as forces the user to hold the device
     * far enough away to ensure the image will be in focus.
     *
     * @return The rectangle to draw on screen in window coordinates.
     */
    public Rect getFramingRect() {
        if (camera != null && previewing) {
            Point screenResolution = configManager.getScreenResolution();
            if (framingRect == null) {
                if (camera == null) {
                    return null;
                }
                //			int width = screenResolution.x * 3 / 4;
                //			if (width < MIN_FRAME_WIDTH) {
                //				width = MIN_FRAME_WIDTH;
                //			} else if (width > MAX_FRAME_WIDTH) {
                //				width = MAX_FRAME_WIDTH;
                //			}
                //			int height = screenResolution.y * 3 / 4;
                //			if (height < MIN_FRAME_HEIGHT) {
                //				height = MIN_FRAME_HEIGHT;
                //			} else if (height > MAX_FRAME_HEIGHT) {
                //				height = MAX_FRAME_HEIGHT;
                //			}

                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 0.8);
                int height = (int) width;

                int leftOffset = (screenResolution.x - width) / 2;
                int topOffset = (screenResolution.y - height) / 2 - 200;
                if (topOffset <= 0)
                    topOffset = 20;

                framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
                Log.d(TAG, "Calculated framing rect: " + framingRect);
            }
        }
        return framingRect;
    }


    /**
     * Like {@link #getFramingRect} but coordinates are in terms of the preview frame,
     * not UI / screen.
     */
    public Rect getFramingRectInPreview() {
        if (framingRectInPreview == null) {
            Rect rect = new Rect(getFramingRect());
            Point cameraResolution = configManager.getCameraResolution();
            Point screenResolution = configManager.getScreenResolution();

            //      rect.left = rect.left * cameraResolution.x / screenResolution.x;
            //      rect.right = rect.right * cameraResolution.x / screenResolution.x;
            //      rect.top = rect.top * cameraResolution.y / screenResolution.y;
            //      rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;

            //�л�Ϊ����
            rect.left = rect.left * cameraResolution.y / screenResolution.x;
            rect.right = rect.right * cameraResolution.y / screenResolution.x;
            rect.top = rect.top * cameraResolution.x / screenResolution.y;
            rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y;
            framingRectInPreview = rect;
        }
        return framingRectInPreview;
    }

    /**
     * Converts the result points from still resolution coordinates to screen coordinates.
     *
     * @param points The points returned by the Reader subclass through Result.getResultPoints().
     * @return An array of Points scaled to the size of the framing rect and offset appropriately
     *         so they can be drawn in screen coordinates.
     */
  /*
  public Point[] convertResultPoints(ResultPoint[] points) {
    Rect frame = getFramingRectInPreview();
    int count = points.length;
    Point[] output = new Point[count];
    for (int x = 0; x < count; x++) {
      output[x] = new Point();
      output[x].x = frame.left + (int) (points[x].getX() + 0.5f);
      output[x].y = frame.top + (int) (points[x].getY() + 0.5f);
    }
    return output;
  }
   */

    /**
     * A factory method to build the appropriate LuminanceSource object based on the format
     * of the preview buffers, as described by Camera.Parameters.
     *
     * @param data   A preview frame.
     * @param width  The width of the image.
     * @param height The height of the image.
     * @return A PlanarYUVLuminanceSource instance.
     */
    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = getFramingRectInPreview();
        int previewFormat = configManager.getPreviewFormat();
        String previewFormatString = configManager.getPreviewFormatString();
        switch (previewFormat) {
            // This is the standard Android format which all devices are REQUIRED to support.
            // In theory, it's the only one we should ever care about.
            case PixelFormat.YCbCr_420_SP:
                // This format has never been seen in the wild, but is compatible as we only care
                // about the Y channel, so allow it.
            case PixelFormat.YCbCr_422_SP:
                return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                        rect.width(), rect.height());
            default:
                // The Samsung Moment incorrectly uses this variant instead of the 'sp' version.
                // Fortunately, it too has all the Y data up front, so we can read it.
                if ("yuv420p".equals(previewFormatString)) {
                    return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                            rect.width(), rect.height());
                }
        }
        throw new IllegalArgumentException("Unsupported picture format: " +
                previewFormat + '/' + previewFormatString);
    }

    public void doTakePicture() {
        if (isPreviewing && (camera != null)) {
            isPreviewing = false;

            new Thread(new Runnable() {

                @Override
                public void run() {
                    /***
                     *  没有设置setPictureSize 导致 预览图片和最后的结果不一致
                     */
                    Parameters params = camera.getParameters();

                    params.setPictureFormat(PixelFormat.JPEG);// 图片格式
                    List<Camera.Size> sizes = params.getSupportedPreviewSizes();
                    Camera.Size s = sizes.get(0);
                    params.setPictureSize(s.width, s.height);// 图片大小
                    camera.setParameters(params);// 将参数设置到我的camera
                    camera.takePicture(mShutterCallback, null, mJpegPictureCallback);
                }
            }).start();


        }
    }

    ShutterCallback mShutterCallback = new ShutterCallback() {
        public void onShutter() {
        }
    };
    PictureCallback mRawCallback = new PictureCallback()

    {

        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };
    PictureCallback mJpegPictureCallback = new PictureCallback() {
        public void onPictureTaken(final byte[] data, final Camera camera) {


            new Thread(new Runnable() {

                @Override
                public void run() {
                    Bitmap b = null;
                    if (null != data) {
                        b = BitmapFactory.decodeByteArray(data, 0, data.length);
                        camera.stopPreview();
                    }
                    if (null != b) {
                        Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
                        // ParkPhotoManager.getInstance().saveBitmap(rotaBitmap);
                        try {
                            File file = new File(CustomCameraActivity.file, System.currentTimeMillis() + ".jpg");

                            rotaBitmap.compress(Bitmap.CompressFormat.JPEG, 90, new FileOutputStream(file));

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }

                    //EventBus.getDefault().post(new EventBusLodeImage());

                }
            }).start();

        }
    };


    public boolean isPreviewing() {
        return isPreviewing;
    }

    public void rePreview() {
        camera.startPreview();//开启预览
        isPreviewing = true;
    }

    public void openAndStartPreview(SurfaceHolder holder) {

        try {
            camera = Camera.open();


            Parameters params    = camera.getParameters();
            params.setPictureFormat(PixelFormat.JPEG);// 图片格式
           // params.setFlashMode(Parameters.FLASH_MODE_AUTO);

            List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            Camera.Size s = sizes.get(0);
            params.setPictureSize(s.width, s.height);// 图片大小

            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(90);

            camera.setParameters(mParams);
            camera.startPreview();//开启预览

        } catch (Exception e) {
            e.printStackTrace();
        }


        mParams = camera.getParameters(); //重新get一次

        Log.i(TAG, "最终设置:PreviewSize--With = " + mParams.getPreviewSize().width
                + "Height = " + mParams.getPreviewSize().height);
        Log.i("haha", "最终设置:PictureSize--With = " + mParams.getPictureSize().width
                + "Height = " + mParams.getPictureSize().height);

        isPreviewing = true;


    }

    public void doStartPreview(SurfaceHolder holder, float previewRate) {

        if (camera != null) {

            mParams = camera.getParameters();
            mParams.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式
            CamParaUtil.getInstance().printSupportPictureSize(mParams);
            CamParaUtil.getInstance().printSupportPreviewSize(mParams);

            // LogUtil.d("camera", holder.getSurfaceFrame().toString());
            List<Size> sizeList = getSmaeSize(mParams.getSupportedPictureSizes(), mParams.getSupportedPreviewSizes());
            if (sizeList.size() == 0) {
                sizeList = mParams.getSupportedPictureSizes();
            }
            //设置PreviewSize和PictureSize
            Size sizePic = getOptimalPreviewSize(sizeList, context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels);
            mParams.setPictureSize(sizePic.width, sizePic.height);

            // Size size = getOptimalPreviewSize(mParams.getSupportedPreviewSizes(), context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels);
            mParams.setPreviewSize(sizePic.width, sizePic.height);

            camera.setDisplayOrientation(90);

            CamParaUtil.getInstance().printSupportFocusMode(mParams);
            List<String> focusModes = mParams.getSupportedFocusModes();
            if (focusModes.contains("continuous-video")) {
                mParams.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            camera.setParameters(mParams);

            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();//开启预览
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            isPreviewing = true;


            mParams = camera.getParameters(); //重新get一次
            Log.i(TAG, "最终设置:PreviewSize--With = " + mParams.getPreviewSize().width
                    + "Height = " + mParams.getPreviewSize().height);
            Log.i("haha", "最终设置:PictureSize--With = " + mParams.getPictureSize().width
                    + "Height = " + mParams.getPictureSize().height);
        }

    }

    public void doStopCamera() {
        try {
            if (null != camera) {
                camera.setPreviewCallback(null);
                camera.stopPreview();
                isPreviewing = false;

                camera.release();
                camera = null;
            }
        } catch (Exception e) {
        }

    }

    public interface CamOpenOverCallback {
        public void cameraHasOpened();
    }

    private List<Size> getSmaeSize(List<Size> sizes, List<Size> otherSizes) {
        List<Size> temp = new ArrayList<Size>();
        for (Size size : sizes) {
            if (otherSizes.contains(size)) {
                temp.add(size);
            }
        }
        return temp;

    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetWidth = w;
        for (Size size : sizes) {
            if (size.width % w == 0 && size.height % h == 0) {
                if (optimalSize == null) {
                    optimalSize = size;
                } else {
                    if (optimalSize.width > size.width) {
                        optimalSize = size;
                    }
                }

            }
        }
        if (optimalSize != null) {
            return optimalSize;
        }

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.width - targetWidth) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.width - targetWidth);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.width - targetWidth) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.width - targetWidth);
                }
            }
        }
        return optimalSize;
    }


    public static void turnLightOn(Camera mCamera) {
        if (mCamera == null) {
            return;
        }
        Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        // Check if camera flash exists
        if (flashModes == null) {
            // Use the screen as a flashlight (next best thing)
            return;
        }
        String flashMode = parameters.getFlashMode();

        if (flashMode.equals(Parameters.FLASH_MODE_TORCH)) {
            setFlashMode(mCamera, Parameters.FLASH_MODE_OFF);
        } else if (flashMode.equals(Parameters.FLASH_MODE_OFF)) {
            setFlashMode(mCamera, Parameters.FLASH_MODE_TORCH);
        }
    }

    public static void setFlashMode(Camera mCamera, String value) {
        if (mCamera != null) {
            Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(value);
            mCamera.setParameters(parameters);
        }
    }

    public static void lightSwitch() {
        turnLightOn(camera);
    }
}
