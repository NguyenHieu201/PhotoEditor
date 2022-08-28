package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.FileNotFoundException;

public class MainActivity extends GeneralActivity {
    Bitmap bitmapMaster;
    Canvas canvasMaster;

    Button btnLoadImage, btnSaveImage, btnUndo;
    ImageView imageResult;

    final int RQS_IMAGE1 = 1;

    Uri source;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityResultLauncher<Intent> painterLauncher = createPainterLauncher();

        setContentView(R.layout.activity_main);

        btnLoadImage = findViewById(R.id.loadimage);
        btnSaveImage = findViewById(R.id.saveimage);
        btnUndo = findViewById(R.id.undo_button);
        imageResult = findViewById(R.id.result);



        btnLoadImage.setOnClickListener(arg0 -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, RQS_IMAGE1);
        });

        btnSaveImage.setOnClickListener(v -> {
            Intent painterIntent = new Intent(this, PainterActivity.class);
            saveBitmapToCache(bitmapMaster);
            painterLauncher.launch(painterIntent);
        });

        btnUndo.setOnClickListener(v -> {
            bitmapMaster = removeBitmap();
            imageResult.setImageBitmap(bitmapMaster);
        });




    }

    /*
    Project position on ImageView to position on Bitmap draw on it
     */

    private ActivityResultLauncher<Intent> createPainterLauncher() {
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    bitmapMaster = loadBitmapFromCache();
                    imageResult.setImageBitmap(bitmapMaster);
                    addNewBitMap(Bitmap.createBitmap(bitmapMaster));
                }
        );
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap tempBitmap;

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case RQS_IMAGE1:
                    source = data.getData();

                    try {
                        //tempBitmap is Immutable bitmap,
                        //cannot be passed to Canvas constructor
                        tempBitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(source));

                        Bitmap.Config config;
                        if(tempBitmap.getConfig() != null){
                            config = tempBitmap.getConfig();
                        }else{
                            config = Bitmap.Config.ARGB_8888;
                        }

                        //bitmapMaster is Mutable bitmap
                        bitmapMaster = Bitmap.createBitmap(
                                tempBitmap.getWidth(),
                                tempBitmap.getHeight(),
                                config);

                        canvasMaster = new Canvas(bitmapMaster);
                        canvasMaster.drawBitmap(tempBitmap, 0, 0, null);

                        imageResult.setImageBitmap(bitmapMaster);
                        addNewBitMap(Bitmap.createBitmap(bitmapMaster));
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }


}
