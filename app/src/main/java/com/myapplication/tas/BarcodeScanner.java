package com.myapplication.tas;

/**
 * Created by i7-3930 on 25/01/2016.
 */
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class BarcodeScanner extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    private Button scanButton;
    private ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_scanner);

        initControls();
    }

    private void initControls() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        // Instance barcode scanner
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(BarcodeScanner.this, mCamera, previewCb,
                autoFocusCB);
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(mPreview);

        scanButton = (Button) findViewById(R.id.ScanButton);

        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (barcodeScanned) {
                    barcodeScanned = false;
                    mCamera.setPreviewCallback(previewCb);
                    mCamera.startPreview();
                    previewing = true;
                    mCamera.autoFocus(autoFocusCB);
                }
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            releaseCamera();
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {

                    Log.i("<<<<<<Asset Code>>>>> ",
                            "<<<<Bar Code>>> " + sym.getData());
                    String scanResult = sym.getData().trim();

                    analyseResult(scanResult);

                  /*  Toast.makeText(BarcodeScanner.this, scanResult,
                            Toast.LENGTH_SHORT).show();*/

                    barcodeScanned = true;

                    break;
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    public class EventInfosFromMessage{
        String from_message_event_name="";
        String from_message_event_time="";
        String from_message_php_login="";
        String from_message_php_password="";
    }

    UserDbHelper userDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    File file;


    private void analyseResult(String message) {
        EventInfosFromMessage eventInfosFromMessage;
        boolean err;
        final int event_active=1;
        final int event_not_active=0;
        String event_description="";
        eventInfosFromMessage=new EventInfosFromMessage();
        eventInfosFromMessage=QRCodeValidityCheckAndParse(message);

        if(!(eventInfosFromMessage.from_message_event_name.equals(""))){
            //Valid QR code detected

            err=createEventFolder(eventInfosFromMessage.from_message_event_name);
            if (err==false){
                err=getEventExtraDataFromServer(eventInfosFromMessage.from_message_event_name, eventInfosFromMessage.from_message_php_login, eventInfosFromMessage.from_message_php_password);
                if (err==false){
                    //Read file event description
                    event_description=readEventDescriptionFile(eventInfosFromMessage.from_message_event_name);
                    //Create event in database.
                    //Delete db line based on event name to avoid to have 2 times the same event registered.
                    deleteDbRow(eventInfosFromMessage.from_message_event_name);
                    //Create database row and set as active event.
                    addInfoDb(eventInfosFromMessage.from_message_event_name, eventInfosFromMessage.from_message_event_time, Environment.getExternalStorageDirectory().getAbsolutePath() + eventInfosFromMessage.from_message_event_name + "event_icon.jpg",
                            event_description, eventInfosFromMessage.from_message_php_login, eventInfosFromMessage.from_message_php_password, event_active, "", "", "", "", "");
                    //Ensure that no other event is active... Register all other event as inactive and place the last entered in first position
                    userDbHelper = new UserDbHelper(this);
                    sqLiteDatabase = userDbHelper.getReadableDatabase();
                    cursor=userDbHelper.getInformations(sqLiteDatabase);
                    if(cursor.moveToFirst()){
                        do{
                            if(!(cursor.getString(0).equals(eventInfosFromMessage.from_message_event_name))){
                                deleteDbRow(cursor.getString(0));
                                addInfoDb(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                        cursor.getString(3),cursor.getString(4), cursor.getString(5), event_not_active, cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11));
                            }
                        }while (cursor.moveToNext());
                    }
                    userDbHelper.close();

                    //Restart main activity
                    releaseCamera();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("NoNewRun", "value");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else {
                    //Error during event datas sync from server.
                    ShowPopupErrorMessage("Error: Sync with server couldn't be done.");
                }
            }
                else{
                //Error folder could not be created.
                ShowPopupErrorMessage("Error:The pictures folder couldn't be created. Event not created.");
            }

        }else{
            //display pop-up not valid QR code detected.
            ShowPopupErrorMessage("Error: QR code incorrect!!! Event not created.");
        }


    }

    //Error message display
    public void ShowPopupErrorMessage(String error_message){
        final Dialog dialog = new Dialog(this);
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

    public String readEventDescriptionFile(String event_name){
        FileInputStream fileInputStream=null;
        String infoStringRead="";
        byte [] inputBuffer = new byte[1024];
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+event_name+"/", "event_description.txt");

        //Read file infos
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(inputBuffer);
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //Parse string infos
            infoStringRead = new String(inputBuffer, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return infoStringRead;
    }

    //Function to add a row inside the event database
    public void addInfoDb(String event_name, String event_time, String event_image_path, String event_description, String php_login,
                          String php_password, int event_active, String spare_1,String spare_2,String spare_3,String spare_4,String spare_5)
    {
        userDbHelper = new UserDbHelper(this);
        sqLiteDatabase=userDbHelper.getWritableDatabase();
        userDbHelper.addInformations(event_name, event_time, event_image_path, event_description, php_login, php_password, event_active, spare_1, spare_2, spare_3, spare_4, spare_5, sqLiteDatabase);
        //For debbugging only
        //Toast.makeText(getApplicationContext(),"Data saved",Toast.LENGTH_LONG).show();
        userDbHelper.close();
    }

    //Function to delete a row inside a database.
    public void deleteDbRow(String event_to_delete){
        userDbHelper = new UserDbHelper(this);
        sqLiteDatabase=userDbHelper.getWritableDatabase();
        userDbHelper.deleteInfo(event_to_delete, sqLiteDatabase);
        //For debbugging only
        //Toast.makeText(getApplicationContext(),"Data deleted",Toast.LENGTH_LONG).show();
        userDbHelper.close();
    }

    //Function to sychronize event image and event description from server
    public boolean getEventExtraDataFromServer(String event_name, String php_login, String php_password){
        return false;
    }

    //Function to detect the validity of the QR code and store datas inside a container
    public EventInfosFromMessage QRCodeValidityCheckAndParse(String message){
        EventInfosFromMessage eventInfosFromMessage;
        eventInfosFromMessage = new EventInfosFromMessage();
        final String APP_FLAG="SHAVENT";
        String app_flag="";
        String qr_event_name="";
        String qr_event_time="";
        String qr_php_login="";
        String qr_php_password="";

        try {
            //Parse string infos
            String delims = "[+]";
            String[] tokens = message.split(delims);
            //Parse name string part
            app_flag=tokens[0];
            qr_event_name=tokens[1];
            qr_event_time=tokens[2];
            qr_php_login=tokens[3];
            qr_php_password=tokens[4];
        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        if(app_flag.equals(APP_FLAG)&& !qr_event_name.equals("") && !qr_event_time.equals("") && !qr_php_login.equals("") && !qr_php_password.equals("")){
            eventInfosFromMessage.from_message_event_name=qr_event_name;
            eventInfosFromMessage.from_message_event_time=qr_event_time;
            eventInfosFromMessage.from_message_php_login=qr_php_login;
            eventInfosFromMessage.from_message_php_password=qr_php_password;
        }else{
            eventInfosFromMessage.from_message_event_name="";
            eventInfosFromMessage.from_message_event_time="";
            eventInfosFromMessage.from_message_php_login="";
            eventInfosFromMessage.from_message_php_password="";
        }

        // Add popup confirmation to allow the event creation.
        return  eventInfosFromMessage;
    }

    public boolean createEventFolder(String event_name){
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"TAS");
        if (!folder.exists()){
            folder.mkdir();
        }
        folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/TAS", event_name);
        if (!folder.exists()){
            folder.mkdir();
        }
        if (!folder.exists()){
            return true;
        }else{
            return false;
        }

    }

}
