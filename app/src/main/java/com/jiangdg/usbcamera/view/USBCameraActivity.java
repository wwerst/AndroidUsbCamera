package com.jiangdg.usbcamera.view;

import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;
import com.jiangdg.usbcamera.R;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jiangdg.usbcamera.UVCCameraHelper;
import com.jiangdg.usbcamera.application.MyApplication;
import com.jiangdg.usbcamera.utils.FileUtils;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.Size;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;
import com.serenegiant.usb.encoder.RecordParams;
import com.serenegiant.usb.widget.CameraViewInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * UVCCamera use demo
 * <p>
 * Created by jiangdongguo on 2017/9/30.
 */

public class USBCameraActivity extends AppCompatActivity implements CameraDialog.CameraDialogParent, CameraViewInterface.Callback {
    private static final String TAG = "Debug";
    @BindView(R.id.camera_view_ir)
    public View mTextureView_ir;
    @BindView(R.id.camera_view_vr)
    public View mTextureView_vr;
    @BindView(R.id.toolbar)
    public Toolbar mToolbar;
    @BindView(R.id.seekbar_brightness)
    public SeekBar mSeekBrightness;
    @BindView(R.id.seekbar_contrast)
    public SeekBar mSeekContrast;
    @BindView(R.id.switch_rec_voice)
    public Switch mSwitchVoice;

    private UVCCameraHelper mCameraHelper_ir;
    private CameraViewInterface mUVCCameraView_ir;
    private UVCCameraHelper mCameraHelper_vr;
    private CameraViewInterface mUVCCameraView_vr;
    private AlertDialog mDialog;

    private boolean isRequest;
    private boolean isPreview;

    public String camera_name_ir = "Camera VR";

    public String camera_name_vr = "Camera IR";

    // Done IR
    private UVCCameraHelper.OnMyDevConnectListener listener_ir = new UVCCameraHelper.OnMyDevConnectListener() {

        @Override
        public void onAttachDev(UsbDevice device) {
            if(!device.getProductName().contains(camera_name_ir))
            {
                return;
            }
            // request open permission
            if (!isRequest) {
                isRequest = true;
                if (mCameraHelper_ir != null) {
                    int index = mCameraHelper_ir.getUsbDeviceList().indexOf(device);
                    mCameraHelper_ir.requestPermission(index);
                }
            }
        }

        @Override
        public void onDettachDev(UsbDevice device) {
            if(!device.getProductName().contains(camera_name_ir))
            {
                return;
            }
            // close camera
            if (isRequest) {
                isRequest = false;
                mCameraHelper_ir.closeCamera();
                showShortMsg(device.getDeviceName() + " is out");
            }
        }

        @Override
        public void onConnectDev(UsbDevice device, boolean isConnected) {
            if(!device.getProductName().contains(camera_name_ir))
            {
                return;
            }
            if (!isConnected) {
                showShortMsg("fail to connect,please check resolution params");
                isPreview = false;
            } else {
                isPreview = true;
                showShortMsg("connecting");
                // initialize seekbar
                // need to wait UVCCamera initialize over
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Looper.prepare();
                        if(mCameraHelper_ir != null && mCameraHelper_ir.isCameraOpened()) {
                            mSeekBrightness.setProgress(mCameraHelper_ir.getModelValue(UVCCameraHelper.MODE_BRIGHTNESS));
                            mSeekContrast.setProgress(mCameraHelper_ir.getModelValue(UVCCameraHelper.MODE_CONTRAST));
                        }
                        Looper.loop();
                    }
                }).start();
            }
        }

        @Override
        public void onDisConnectDev(UsbDevice device) {
            showShortMsg("disconnecting");
        }
    };

    private UVCCameraHelper.OnMyDevConnectListener listener_vr = new UVCCameraHelper.OnMyDevConnectListener() {

        @Override
        public void onAttachDev(UsbDevice device) {
            if(!device.getProductName().contains(camera_name_vr))
            {
                return;
            }
            // request open permission
            if (!isRequest) {
                isRequest = true;
                if (mCameraHelper_vr != null) {
                    int index = mCameraHelper_vr.getUsbDeviceList().indexOf(device);
                    mCameraHelper_vr.requestPermission(index);
                }
            }
        }

        @Override
        public void onDettachDev(UsbDevice device) {
            if(!device.getProductName().contains(camera_name_vr))
            {
                return;
            }
            // close camera
            if (isRequest) {
                isRequest = false;
                mCameraHelper_vr.closeCamera();
                showShortMsg(device.getDeviceName() + " is out");
            }
        }

        @Override
        public void onConnectDev(UsbDevice device, boolean isConnected) {
            if(!device.getProductName().contains(camera_name_vr))
            {
                return;
            }
            if (!isConnected) {
                showShortMsg("fail to connect,please check resolution params");
                isPreview = false;
            } else {
                isPreview = true;
                showShortMsg("connecting");
                // initialize seekbar
                // need to wait UVCCamera initialize over
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Looper.prepare();
                        if(mCameraHelper_vr != null && mCameraHelper_vr.isCameraOpened()) {
                            mSeekBrightness.setProgress(mCameraHelper_vr.getModelValue(UVCCameraHelper.MODE_BRIGHTNESS));
                            mSeekContrast.setProgress(mCameraHelper_vr.getModelValue(UVCCameraHelper.MODE_CONTRAST));
                        }
                        Looper.loop();
                    }
                }).start();
            }
        }

        @Override
        public void onDisConnectDev(UsbDevice device) {
            showShortMsg("disconnecting");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usbcamera);
        ButterKnife.bind(this);
        initView();

        // Initialize IR camera
        mUVCCameraView_ir = (CameraViewInterface) mTextureView_ir;
        mUVCCameraView_ir.setCallback(this);
        mCameraHelper_ir = UVCCameraHelper.getInstance();
        mCameraHelper_ir.setDefaultFrameFormat(UVCCameraHelper.FRAME_FORMAT_MJPEG);


        // Initialize VR camera
        mUVCCameraView_vr = (CameraViewInterface) mTextureView_vr;
        mUVCCameraView_vr.setCallback(this);
        mCameraHelper_vr = UVCCameraHelper.getInstance();
        mCameraHelper_vr.setDefaultFrameFormat(UVCCameraHelper.FRAME_FORMAT_MJPEG);
        mCameraHelper_vr.initUSBMonitor(this, mUVCCameraView_vr, listener_vr);
        mCameraHelper_ir.initUSBMonitor(this, mUVCCameraView_ir, listener_ir);

        mCameraHelper_ir.setOnPreviewFrameListener(new AbstractUVCCameraHandler.OnPreViewResultListener() {
            @Override
            public void onPreviewResult(byte[] nv21Yuv) {
                Log.d(TAG, "onPreviewResult IR: "+nv21Yuv.length);
            }
        });

        mCameraHelper_vr.setOnPreviewFrameListener(new AbstractUVCCameraHandler.OnPreViewResultListener() {
            @Override
            public void onPreviewResult(byte[] nv21Yuv) {
                Log.d(TAG, "onPreviewResult VR: "+nv21Yuv.length);
            }
        });
    }

    private void initView() {
        setSupportActionBar(mToolbar);

        mSeekBrightness.setMax(100);
        mSeekBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mCameraHelper_ir != null && mCameraHelper_ir.isCameraOpened()) {
                    mCameraHelper_ir.setModelValue(UVCCameraHelper.MODE_BRIGHTNESS,progress);
                }

                if(mCameraHelper_vr != null && mCameraHelper_vr.isCameraOpened()) {
                    mCameraHelper_vr.setModelValue(UVCCameraHelper.MODE_BRIGHTNESS,progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekContrast.setMax(100);
        mSeekContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mCameraHelper_ir != null && mCameraHelper_ir.isCameraOpened()) {
                    mCameraHelper_ir.setModelValue(UVCCameraHelper.MODE_CONTRAST,progress);
                }
                if(mCameraHelper_vr != null && mCameraHelper_vr.isCameraOpened()) {
                    mCameraHelper_vr.setModelValue(UVCCameraHelper.MODE_CONTRAST,progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // step.2 register USB event broadcast
        if (mCameraHelper_ir != null) {
            mCameraHelper_ir.registerUSB();
        }
        if (mCameraHelper_vr != null) {
            mCameraHelper_vr.registerUSB();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // step.3 unregister USB event broadcast
        if (mCameraHelper_ir != null) {
            mCameraHelper_ir.unregisterUSB();
        }
        if (mCameraHelper_vr != null) {
            mCameraHelper_vr.unregisterUSB();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toobar, menu);
        return true;
    }

    // Options only apply to VR camera
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_takepic:
                if (mCameraHelper_vr == null || !mCameraHelper_vr.isCameraOpened()) {
                    showShortMsg("sorry,camera open failed");
                    return super.onOptionsItemSelected(item);
                }
                String picPath = UVCCameraHelper.ROOT_PATH + MyApplication.DIRECTORY_NAME +"/images/"
                        + System.currentTimeMillis() + UVCCameraHelper.SUFFIX_JPEG;

                mCameraHelper_vr.capturePicture(picPath, new AbstractUVCCameraHandler.OnCaptureListener() {
                    @Override
                    public void onCaptureResult(String path) {
                        if(TextUtils.isEmpty(path)) {
                            return;
                        }
                        new Handler(getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(USBCameraActivity.this, "save path:"+path, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                break;
            case R.id.menu_recording:
                if (mCameraHelper_vr == null || !mCameraHelper_vr.isCameraOpened()) {
                    showShortMsg("sorry,camera open failed");
                    return super.onOptionsItemSelected(item);
                }
                if (!mCameraHelper_vr.isPushing()) {
                    String videoPath = UVCCameraHelper.ROOT_PATH + MyApplication.DIRECTORY_NAME +"/videos/" + System.currentTimeMillis()
                            + UVCCameraHelper.SUFFIX_MP4;

//                    FileUtils.createfile(FileUtils.ROOT_PATH + "test666.h264");
                    // if you want to record,please create RecordParams like this
                    RecordParams params = new RecordParams();
                    params.setRecordPath(videoPath);
                    params.setRecordDuration(0);                        // auto divide saved,default 0 means not divided
                    params.setVoiceClose(mSwitchVoice.isChecked());    // is close voice

                    params.setSupportOverlay(true); // overlay only support armeabi-v7a & arm64-v8a
                    mCameraHelper_vr.startPusher(params, new AbstractUVCCameraHandler.OnEncodeResultListener() {
                        @Override
                        public void onEncodeResult(byte[] data, int offset, int length, long timestamp, int type) {
                            // type = 1,h264 video stream
                            if (type == 1) {
                                FileUtils.putFileStream(data, offset, length);
                            }
                            // type = 0,aac audio stream
                            if(type == 0) {

                            }
                        }

                        @Override
                        public void onRecordResult(String videoPath) {
                            if(TextUtils.isEmpty(videoPath)) {
                                return;
                            }
                            new Handler(getMainLooper()).post(() -> Toast.makeText(USBCameraActivity.this, "save videoPath:"+videoPath, Toast.LENGTH_SHORT).show());
                        }
                    });
                    // if you only want to push stream,please call like this
                    // mCameraHelper.startPusher(listener);
                    showShortMsg("start record...");
                    mSwitchVoice.setEnabled(false);
                } else {
                    FileUtils.releaseFile();
                    mCameraHelper_vr.stopPusher();
                    showShortMsg("stop record...");
                    mSwitchVoice.setEnabled(true);
                }
                break;
            case R.id.menu_resolution:
                if (mCameraHelper_vr == null || !mCameraHelper_vr.isCameraOpened()) {
                    showShortMsg("sorry,camera open failed");
                    return super.onOptionsItemSelected(item);
                }
                showResolutionListDialog();
                break;
            case R.id.menu_focus:
                if (mCameraHelper_vr == null || !mCameraHelper_vr.isCameraOpened()) {
                    showShortMsg("sorry,camera open failed");
                    return super.onOptionsItemSelected(item);
                }
                mCameraHelper_vr.startCameraFoucs();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showResolutionListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(USBCameraActivity.this);
        View rootView = LayoutInflater.from(USBCameraActivity.this).inflate(R.layout.layout_dialog_list, null);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_dialog);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(USBCameraActivity.this, android.R.layout.simple_list_item_1, getResolutionList());
        if (adapter != null) {
            listView.setAdapter(adapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mCameraHelper_vr == null || !mCameraHelper_vr.isCameraOpened())
                    return;
                final String resolution = (String) adapterView.getItemAtPosition(position);
                String[] tmp = resolution.split("x");
                if (tmp != null && tmp.length >= 2) {
                    int widht = Integer.valueOf(tmp[0]);
                    int height = Integer.valueOf(tmp[1]);
                    mCameraHelper_vr.updateResolution(widht, height);
                }
                mDialog.dismiss();
            }
        });

        builder.setView(rootView);
        mDialog = builder.create();
        mDialog.show();
    }

    // example: {640x480,320x240,etc}
    private List<String> getResolutionList() {
        List<Size> list = mCameraHelper_vr.getSupportedPreviewSizes();
        List<String> resolutions = null;
        if (list != null && list.size() != 0) {
            resolutions = new ArrayList<>();
            for (Size size : list) {
                if (size != null) {
                    resolutions.add(size.width + "x" + size.height);
                }
            }
        }
        return resolutions;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtils.releaseFile();
        // step.4 release uvc camera resources
        if (mCameraHelper_ir != null) {
            mCameraHelper_ir.release();
        }
        if (mCameraHelper_vr != null) {
            mCameraHelper_vr.release();
        }
    }

    private void showShortMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public USBMonitor getUSBMonitor() {
        return mCameraHelper_vr.getUSBMonitor();
    }

    @Override
    public void onDialogResult(boolean canceled) {
        if (canceled) {
            showShortMsg("取消操作");
        }
    }

    public boolean isCameraOpened() {
        return mCameraHelper_vr.isCameraOpened();
    }

    @Override
    public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
        if (!isPreview && mCameraHelper_vr.isCameraOpened()) {
            mCameraHelper_vr.startPreview(mUVCCameraView_vr);
            isPreview = true;
        }
        if (!isPreview && mCameraHelper_ir.isCameraOpened()) {
            mCameraHelper_ir.startPreview(mUVCCameraView_ir);
            isPreview = true;
        }
    }

    @Override
    public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {

    }

    @Override
    public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
        if (isPreview && mCameraHelper_ir.isCameraOpened()) {
            mCameraHelper_ir.stopPreview();
            isPreview = false;
        }
        if (isPreview && mCameraHelper_vr.isCameraOpened()) {
            mCameraHelper_vr.stopPreview();
            isPreview = false;
        }
    }
}
