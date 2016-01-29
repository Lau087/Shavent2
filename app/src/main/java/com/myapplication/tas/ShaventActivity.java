package com.myapplication.tas;

/**
 * Created by i7-3930 on 28/01/2016.
 */

import android.app.Dialog;
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
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

    int[] event_images={R.drawable.event_image,R.drawable.event_image,R.drawable.event_image,R.drawable.event_image,R.drawable.event_image};
    String [] event_names={"Craquage","Ski 2016","Fondue mémé","Carnaval","Annif Bette"};
    String [] event_times={"31/01/2016 - 10h","31/01/2016 - 10h","31/01/2016 - 10h","31/01/2016 - 10h","31/01/2016 - 10h"};
    Boolean [] event_active={false,false,true,false,false,};

    EventsAdapter adapter;

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

        //List view object
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

        int i=0;
        adapter= new EventsAdapter(getContext(),R.layout.listview_custom_layout);
        mListView.setAdapter(adapter);
        EventDatasProvider datasProvider;

        //Create an object for each row of the list view.
        for(String events:event_names){
            datasProvider= new EventDatasProvider(event_images[i],event_names[i], event_times[i], event_active[i]);
            adapter.add(datasProvider);
            i++;
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pop_event_name;
                TextView textView;

                //Change the item clicked background
                view.setSelected(true);
                //Find the event name clicked
                textView = (TextView) view.findViewById(R.id.event_name);
                pop_event_name = textView.getText() + "";

                //Only for debugging
                //Toast.makeText(getContext(),textView.getText(),Toast.LENGTH_LONG).show();

                //Sow the popup evenement infos
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.popup_event_informations);
                dialog.show();

                textView = (TextView) dialog.findViewById(R.id.pop_event_name);
                textView.setText(pop_event_name);

                Button activate = (Button) dialog.findViewById(R.id.set_active);
                activate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }

                });

                Button close = (Button) dialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }

                });

                Button delete = (Button) dialog.findViewById(R.id.delete_event);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }

                });
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

