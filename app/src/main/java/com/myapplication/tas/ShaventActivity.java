package com.myapplication.tas;

/**
 * Created by i7-3930 on 28/01/2016.
 */

import android.app.Dialog;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.carlom.stikkyheader.core.StikkyHeaderBuilder;
import it.carlom.stikkyheader.core.animator.AnimatorBuilder;
import it.carlom.stikkyheader.core.animator.HeaderStikkyAnimator;



public class ShaventActivity extends Fragment {


    private ListView mListView;
    private Button scannerButton;
    private Button picturesButton;
    static final int CAM_REQUEST=1;

    UserDbHelper userDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

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
        //Take picture button
        picturesButton = (Button) view.findViewById(R.id.picturesButton);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Top picture list view animation setup
        StikkyHeaderBuilder.stickTo(mListView)
                .setHeader(R.id.header, (ViewGroup) getView())
                .minHeightHeaderDim(R.dimen.min_height_header)
                .animator(new ParallaxStikkyAnimator())
                .build();
        Utils.populateListView(mListView);

        //Define adapter based on the row custom layout for the list view with the help of the EventsAdapter class and set it to the list view.
        //Create an object for each row of the list view and add it to the list view adapter.
        adapter= new EventsAdapter(getContext(),R.layout.listview_custom_layout);
        mListView.setAdapter(adapter);
        EventDatasProvider datasProvider;

        //Read database to create the list view
        userDbHelper = new UserDbHelper(getContext());
        sqLiteDatabase = userDbHelper.getReadableDatabase();
        cursor=userDbHelper.getInformations(sqLiteDatabase);
        Boolean event_active=false;
        if(cursor.moveToFirst()){
            do{
                if(cursor.getInt(6)==1){
                    event_active=true;
                }
                datasProvider= new EventDatasProvider(R.drawable.event_image,cursor.getString(0), cursor.getString(1), event_active);
                adapter.add(datasProvider);
                event_active=false;
            }while (cursor.moveToNext());
        }
        userDbHelper.close();

        //OnClickListener on all the list view items
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {
                String pop_event_name;
                TextView textView;
                final int pos=position;

                //Change the item clicked background
                view.setSelected(true);
                //Find the event name clicked
                textView = (TextView) view.findViewById(R.id.event_name);
                pop_event_name = textView.getText() + "";

                final String current_event_name=pop_event_name;

                //Only for debugging
                //Toast.makeText(getContext(),textView.getText(),Toast.LENGTH_LONG).show();

                //Sow the popup event infos
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
                        int event_active=0;
                        userDbHelper = new UserDbHelper(getContext());
                        sqLiteDatabase = userDbHelper.getReadableDatabase();
                        cursor=userDbHelper.getInformations(sqLiteDatabase);
                        if(cursor.moveToFirst()){
                            do{
                                if(!(cursor.getString(0).equals(current_event_name))){
                                    event_active=0;
                                }else {
                                    event_active=1;
                                }
                                deleteDbRow(cursor.getString(0));
                                addInfoDb(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                        cursor.getString(3),cursor.getString(4), cursor.getString(5), event_active, cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11));
                            }while (cursor.moveToNext());
                        }
                        userDbHelper.close();
                        dialog.dismiss();

                        Intent intent = new Intent(v.getContext(), MainActivity.class);
                        intent.putExtra("NoNewRun", "value");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
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
                        dialog.dismiss();

                        final Dialog dialog = new Dialog(getContext());
                        TextView textView;
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.popup_confirmation);
                        dialog.show();

                        textView = (TextView) dialog.findViewById(R.id.message_to_confirm);
                        textView.setText("Would you really delete the event:" + current_event_name + ". It will also delete the associated picture folder...");

                        Button close = (Button) dialog.findViewById(R.id.close_confirmation);
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        Button confirm = (Button) dialog.findViewById(R.id.confirm_message);
                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                deleteDbRow(current_event_name);
                                deleteFolder(current_event_name);

                                Intent intent = new Intent(v.getContext(), MainActivity.class);
                                intent.putExtra("NoNewRun", "value");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                    }

                });
                view.setSelected(false);
            }
        });

        //OnClickListener QR code scanner button
        scannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), BarcodeScanner.class);
                startActivity(intent);
            }
        });

        //OnClickListener take picture button
        picturesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check event active
                String event_active_from_db="";

                userDbHelper = new UserDbHelper(getContext());
                sqLiteDatabase = userDbHelper.getReadableDatabase();
                cursor=userDbHelper.getInformations(sqLiteDatabase);
                if(cursor.moveToFirst()){
                    do{
                        if((cursor.getInt(6)==1)){
                            event_active_from_db=cursor.getString(0);
                        }
                    }while (cursor.moveToNext());
                }
                userDbHelper.close();

                if(!event_active_from_db.equals("")) {
                    //Start camera phone application
                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = getFile(event_active_from_db);
                    camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(camera_intent, CAM_REQUEST);
                }else{
                    ShowPopupErrorMessage("Error: No event active!");
                }
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

    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private File getFile(String active_event){

        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"TAS");
        if (!folder.exists()){
            folder.mkdir();
        }
        folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/TAS", active_event);
        if (!folder.exists()){
            folder.mkdir();
        }

        File image_file=new File(folder,getCurrentTimeStamp()+".jpg");
        return image_file;
    }

    private void deleteFolder(String active_event){
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/TAS", active_event);

        if (folder.isDirectory())
        {
            String[] children = folder.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(folder, children[i]).delete();
            }
            folder.delete();
        }
    }

    //Function to add a row inside the event database
    public void addInfoDb(String event_name, String event_time, String event_image_path, String event_description, String php_login,
                          String php_password, int event_active, String spare_1,String spare_2,String spare_3,String spare_4,String spare_5)
    {
        userDbHelper = new UserDbHelper(getContext());
        sqLiteDatabase=userDbHelper.getWritableDatabase();
        userDbHelper.addInformations(event_name, event_time, event_image_path, event_description, php_login, php_password, event_active, spare_1, spare_2, spare_3, spare_4, spare_5, sqLiteDatabase);
        //For debbugging only
        //Toast.makeText(getApplicationContext(),"Data saved",Toast.LENGTH_LONG).show();
        userDbHelper.close();
    }

    public void deleteDbRow(String event_to_delete){
        userDbHelper = new UserDbHelper(getContext());
        sqLiteDatabase=userDbHelper.getWritableDatabase();
        userDbHelper.deleteInfo(event_to_delete, sqLiteDatabase);
        //For debbugging only
        //Toast.makeText(getApplicationContext(),"Data deleted",Toast.LENGTH_LONG).show();
        userDbHelper.close();
    }

    //Error message display
    public void ShowPopupErrorMessage(String error_message){
        final Dialog dialog = new Dialog(getContext());
        TextView textView;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_error_create_event);
        dialog.show();

        textView = (TextView) dialog.findViewById(R.id.error_meassage);
        textView.setText(error_message);

        Button activate = (Button) dialog.findViewById(R.id.close_error_message);
        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }

        });
    }
}

