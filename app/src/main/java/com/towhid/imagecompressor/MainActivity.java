package com.towhid.imagecompressor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity {
    private int PICK_IMAGE_REQUEST = 1;
    SharedPref sharedPref;
    ImageView image;
    ImageView image1;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = new SharedPref(this);
        image = (ImageView) findViewById(R.id.image);
        image1 = (ImageView) findViewById(R.id.image1);
        img = (ImageView) findViewById(R.id.img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionUpload();
            }
        });
    }

    private void permissionUpload() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                        ,
                        101
                );
                sharedPref.setDeny(true);
            } else {
                if (sharedPref.getDeny()) showSettingsDialog();
                else {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            101
                    );
                }
            }
        } else {
            openFileChooser();
        }

    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts(
                "package",
                this.getPackageName(),
                null
        );
        intent.setData(uri);
        this.startActivityForResult(intent, 101);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        String[] mimeTypes = {"image/jpeg", "image/jpg", "image/png"};
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, PICK_IMAGE_REQUEST, new Bundle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null
        ) {
            Uri imageUri = data.getData();
            try {
                File file1 = new File(getRealPathFromURI(imageUri));
                File file = new Compressor(this).compressToFile(file1);
                int file_size1 = Integer.parseInt(String.valueOf(file1.length() / 1024));
                int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
                Log.d("fileSize", "file_size1: " + file_size1 + " , file_size: " + file_size);
                image1.setImageURI(Uri.fromFile(file1));
                image.setImageURI(Uri.fromFile(file));
            } catch (IOException e) {
                e.printStackTrace();
            }


           /* try {
                File compressedImageFile = new Compressor(this).compressToFile(file);
              //  int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
            //    int compressedImageFile_size = Integer.parseInt(String.valueOf(compressedImageFile.length()/1024));
           //     Log.d("size", "file_size: "+file_size+"  compressedImageFile_size: "+compressedImageFile_size);

            } catch (IOException e) {
                e.printStackTrace();
            }*/


        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        assert cursor != null;
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

}