package com.myapplication.tas;

/**
 * Created by i7-3930 on 28/01/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import it.carlom.stikkyheader.core.StikkyHeaderBuilder;
import it.carlom.stikkyheader.core.animator.AnimatorBuilder;
import it.carlom.stikkyheader.core.animator.HeaderStikkyAnimator;

public class ShaventActivity extends Fragment {

    private ListView mListView;
    private Button scannerButton;
    private Button picturesButton;
    static final int CAM_REQUEST=1;

    ArrayAdapter<String> adapter;
    String[] listView_test={"Test1",
            "Test2",
            "Test3",
            "Test4",
            "Test5",
            "Test6",
            "Test7",
            "Test8",
            "Test9",
            "Test10"
    };

    static {
        System.loadLibrary("iconv");
    }

    public ShaventActivity() {
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.shavent_activity, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) view.findViewById(R.id.listview);

        //QR code scanner button
        scannerButton = (Button) view.findViewById(R.id.scannerButton);
        scannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), BarcodeScanner.class);
                startActivity(intent);
            }
        });

        //Take picture button
        picturesButton = (Button) view.findViewById(R.id.picturesButton);
        picturesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Start camera phone application
                Intent camera_intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file =getFile();
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(camera_intent, CAM_REQUEST);

            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        StikkyHeaderBuilder.stickTo(mListView)
                .setHeader(R.id.header, (ViewGroup) getView())
                .minHeightHeaderDim(R.dimen.min_height_header)
                .animator(new ParallaxStikkyAnimator())
                .build();

        Utils.populateListView(mListView);

        adapter=new ArrayAdapter<String>(getContext(),R.layout.listview_custom_layout,R.id.list_item,listView_test);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(),parent.getItemAtPosition(position)+" is selected",Toast.LENGTH_LONG).show();
                view.setSelected(true);
            }
        });
    }

    private class ParallaxStikkyAnimator extends HeaderStikkyAnimator {
        @Override
        public AnimatorBuilder getAnimatorBuilder() {
            View mHeader_image = getHeader().findViewById(R.id.header_image);
            return AnimatorBuilder.create().applyVerticalParallax(mHeader_image);
        }
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

