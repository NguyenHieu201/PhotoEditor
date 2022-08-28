package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class GeneralActivity extends AppCompatActivity {

    private static final int MAX_OF_BITMAPS = 20;
    private static final String cacheFileName = "pic";
    private static final Bitmap[] bitmaps = new Bitmap[MAX_OF_BITMAPS];
    private static int numberOfBitmaps = 0;

    protected void saveBitmapToCache(Bitmap bitmapMaster) {
        File cacheDir = getBaseContext().getCacheDir();
        File f = new File(cacheDir, cacheFileName);
        Bitmap pic = Bitmap.createBitmap(bitmapMaster);

        try {
            FileOutputStream out = new FileOutputStream(
                    f);
            pic.compress(
                    Bitmap.CompressFormat.JPEG,
                    100, out);
            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Bitmap loadBitmapFromCache() {
        File cacheDir = getBaseContext().getCacheDir();
        File f = new File(cacheDir, cacheFileName);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(fis);
    }

    protected void addNewBitMap(Bitmap bitmap) {
        bitmaps[numberOfBitmaps] = bitmap;
        numberOfBitmaps = numberOfBitmaps + 1;
    }

    protected Bitmap removeBitmap() {
        if (numberOfBitmaps == 1)
            return bitmaps[numberOfBitmaps - 1];
        numberOfBitmaps = numberOfBitmaps - 1;
        return bitmaps[numberOfBitmaps - 1];
    }
}