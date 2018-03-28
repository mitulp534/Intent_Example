package com.example.hp.com.intentexample;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static int WRITE_EXIST = 0x3;
    Intent intent = null, chooser = null;

    String imageName = "camila.jpg";

    File checkFile;
    File filepath = Environment.getExternalStorageDirectory();

    Dialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkFile = new File(filepath.getAbsolutePath() + "/ImagesTointent/" + imageName);

        /*
        The advantages of defining stictmode policies within your application is to force you,
         in the development phase, to make your application more well behaved within the device
          it is running on: avoid running consuming operation on the UI thread, avoids Activity
           leakages, and so one. When you define these in your code, you make your application
           crashes if the defined strict polices has been compromised, which makes you fixes the
           issues you've done (the not well behaved approaches, like network operations on the UI thread).
         */
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());



    }

    public void process(View view) {

        if (view.getId() == R.id.LaunchMap) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("geo:37.755510,122.405078"));
            chooser = Intent.createChooser(intent, "Launch Maps");
            startActivity(chooser);
        }
        if (view.getId() == R.id.LaunchMarket) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=com.gameloft.android.ANMP.GloftM4HM"));
            chooser = Intent.createChooser(intent, "Launch Market");
            startActivity(chooser);
        }
        if (view.getId() == R.id.sendEmail) {
            intent = new Intent(Intent.ACTION_SEND, Uri.fromParts("mailto", "", null));
            String[] to = {"gaineyj0349@gmail.com", "gaineyj0349@live.com"};
            intent.putExtra(Intent.EXTRA_EMAIL, to);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Hi this was sent from my app");
            intent.putExtra(Intent.EXTRA_TEXT, "Hello world");
            intent.setType("text/plain");
            chooser = Intent.createChooser(intent, "Send Email");
            startActivity(chooser);

        }

        if (view.getId() == R.id.sendImage) {
            getPermissions();
        }


    }

    public void getPermissions(){
        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXIST);

    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            sendImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){

            sendImage();

            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    public void copyImage(){
        showProgress(2000);
        Bitmap bitmap;
        OutputStream output;

        // Retrieve the image from the res folder
        bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.camila);




        // Create a new folder AndroidBegin in SD Card
        File dir = new File(filepath.getAbsolutePath() + "/ImagesTointent/");
        dir.mkdirs();

        // Create a name for the saved image
        File file = new File(dir, imageName);

        try {


            output = new FileOutputStream(file);

            // Compress into png format image from 0% - 100%
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();




        } catch (FileNotFoundException e) {
            Log.i("JOSH", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("JOSH", e.getMessage());

            e.printStackTrace();
        }

        sendImage();
    }

    public void sendImage(){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }

        if(checkFile.exists()){

            // intent Intent
            intent = new Intent(Intent.ACTION_SEND);

            // Type of file to intent
            intent.setType("image/jpg");

            // Locate the image to intent
            Uri uri = Uri.fromFile(checkFile);

            // Pass the image into an intent
            intent.putExtra(Intent.EXTRA_STREAM, uri);

            // Show the social intent chooser list
            startActivity(Intent.createChooser(intent, "Send Image"));

        }else{
            copyImage();
        }
    }

    public void showProgress(final long timeInMillis) {

        dialog = new Dialog(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.progress_bar, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ProgressBar progressBar = (ProgressBar) mView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        dialog.setContentView(mView);
        dialog.show();
        new Thread(new Runnable() {
            public void run() {
                try {

                    Thread.sleep(timeInMillis);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        }).start();

    }

}