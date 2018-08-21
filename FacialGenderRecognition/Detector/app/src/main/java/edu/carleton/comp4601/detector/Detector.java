package edu.carleton.comp4601.detector;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Detector extends AppCompatActivity implements View.OnClickListener{

    private ClientConnection mClientConnection;
    private Button takePictureButton;
    private Button analyzePictureButton;
    private ImageView imageView;
    private TextView mTextViewMsgOutput;
    private Uri file;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detector);

        analyzePictureButton = (Button) findViewById(R.id.analyzeBtn);
        analyzePictureButton.setOnClickListener(this);

        mTextViewMsgOutput = (TextView) findViewById(R.id.messageTextView);

        takePictureButton = (Button) findViewById(R.id.takeBtn);
        imageView = (ImageView) findViewById(R.id.imageView);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        new ConnectionTask().execute("");
        Thread myThread = new Thread(new MyServerThread());
        myThread.start();
    }

    class MyServerThread implements Runnable{
        Socket s;
        ServerSocket ss;
        InputStreamReader isr;
        BufferedReader bufferedReader;
        Handler h = new Handler();
        String message;

        @Override
        public void run(){
            try {
                ss = new ServerSocket(8880);
                while (true){
                    s = ss.accept();
                    isr = new InputStreamReader(s.getInputStream());
                    bufferedReader = new BufferedReader(isr);
                    message = bufferedReader.readLine();
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            analyzePictureButton.setEnabled(true);
                            takePictureButton.setEnabled(true);
                            mTextViewMsgOutput.setText(message);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == analyzePictureButton && file != null){
            analyzePictureButton.setEnabled(false);
            takePictureButton.setEnabled(false);
            mTextViewMsgOutput.setText("Updating Image...");

            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] imageInByte = byteArrayOutputStream.toByteArray();
            String encodeImage = Base64.encodeToString(imageInByte, Base64.DEFAULT);

            if (mClientConnection != null) {
                mClientConnection.sendMessage(encodeImage);
                mClientConnection.sendMessage(getResources().getString(R.string.message_end));
            }
        }
    }

    private class ConnectionTask extends AsyncTask<String, String, ClientConnection> {
        @Override
        protected ClientConnection doInBackground(String... message) {
            mClientConnection = null;
            mClientConnection = new ClientConnection(new ClientConnection.OnMessageReceived() {
                @Override
                public void messageReceived(String message) {
                    publishProgress(message);
                }
            });
            try {
                mClientConnection.run();
            } catch (ConnectException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            updateMessage(values[0]);
        }
    }

    private void updateMessage(String message){
        mTextViewMsgOutput.setText(message);
        mTextViewMsgOutput.setMovementMethod(new ScrollingMovementMethod());
        int lineCount = mTextViewMsgOutput.getLineCount();
        int lineTop = mTextViewMsgOutput.getLayout().getLineTop(lineCount);
        int height = mTextViewMsgOutput.getHeight();
        final int scrollAmount = lineTop - height;
        mTextViewMsgOutput.scrollTo(0, (scrollAmount > 0 ? scrollAmount : 0));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        }
    }

    public void takePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

        startActivityForResult(intent, 100);
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("CameraDemo", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                imageView.setImageURI(file);
            }
        }
    }

}
