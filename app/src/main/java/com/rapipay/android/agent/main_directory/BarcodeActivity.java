package com.rapipay.android.agent.main_directory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.rapipay.android.agent.Model.VersionPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.fragments.AgentKYCFragment;
import com.rapipay.android.agent.interfaces.VersionListener;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import java.io.IOException;
import java.util.ArrayList;

public class BarcodeActivity extends BaseCompactActivity implements VersionListener {

    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String intentData = "", TYPE;
    boolean isEmail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_activity);
        TYPE = getIntent().getStringExtra("type");
        init();
    }

    private void init() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
    }

    private void initialiseDetectorsAndSources() {
        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(BarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(BarcodeActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (barcodes.valueAt(0).email != null) {
                                    txtBarcodeValue.removeCallbacks(null);
                                    intentData = barcodes.valueAt(0).email.address;
                                    isEmail = true;
                                } else {
                                    isEmail = false;
                                    intentData = barcodes.valueAt(0).displayValue;
                                    final Intent intent = getIntent();
                                    intent.putExtra("Key", intentData);
                                    cameraSource.takePicture(new CameraSource.ShutterCallback() {
                                        @Override
                                        public void onShutter() {
                                        }
                                    }, new CameraSource.PictureCallback() {
                                        @Override
                                        public void onPictureTaken(byte[] bytes) {
                                            Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            if (TYPE.equalsIgnoreCase("outside"))
                                                AgentKYCFragment.bitmap_trans = getResizedBitmap(decodedByte, 600);
                                            else
                                                CustomerKYCActivity.bitmap_trans = getResizedBitmap(decodedByte, 600);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                    });
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void checkVersion(ArrayList<VersionPozo> list) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
}


