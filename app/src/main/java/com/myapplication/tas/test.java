package com.myapplication.tas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import java.io.File;
import it.carlom.stikkyheader.core.StikkyHeaderBuilder;
import android.app.Fragment;


/**
 * Created by i7-3930 on 25/01/2016.
 */


public class test extends AppCompatActivity {


    private Button scannerButton;
    private Button picturesButton;
    static final int CAM_REQUEST=1;

    static {
        System.loadLibrary("iconv");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shavent_activity);

        //QR code scanner button
        scannerButton = (Button) findViewById(R.id.scannerButton);
        scannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), BarcodeScanner.class);
                startActivity(intent);
            }
        });

        //Take picture button
        picturesButton = (Button) findViewById(R.id.picturesButton);
        picturesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent camera_intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file =getFile();
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(camera_intent, CAM_REQUEST);

            }
        });
    }

    private File getFile(){

        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "camera-app");
        if (!folder.exists()){
            folder.mkdir();
        }

        File image_file=new File(folder,"cam_image.jpg");

        return image_file;
    }


}
