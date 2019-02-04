package com.rapipay.android.agent.main_directory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.camerakit.CameraKitView;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.utils.BaseCompactActivity;

public class CameraKitActivity extends BaseCompactActivity {

    private CameraKitView cameraKitView;
    private Button photoButton;
    String ImageType;
    int requestType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camerakit_layout);
        cameraKitView = findViewById(R.id.camera);
        photoButton = findViewById(R.id.photoButton);
        photoButton.setOnClickListener(photoOnClickListener);
        ImageType = getIntent().getStringExtra("ImageType");
        requestType = getIntent().getIntExtra("REQUESTTYPE",0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private View.OnClickListener photoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                @Override
                public void onImage(CameraKitView cameraKitView, final byte[] photo) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
                    String pathScan = saveToInternalStorage(bitmap, ImageType);
                    Intent intent = new Intent();
                    intent.putExtra("ImagePath",pathScan);
                    intent.putExtra("ImageType",ImageType);
                    setResult(requestType,intent);
                    finish();
//                    File savedPhoto = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
//                    try {
//                        FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
//                        outputStream.write(photo);
//                        outputStream.close();
//                    } catch (java.io.IOException e) {
//                        e.printStackTrace();
//                        Log.e("CKDemo", "Exception in photo callback");
//                    }
                }
            });
        }
    };
}
