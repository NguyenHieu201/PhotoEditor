package com.example.myapplication;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;

public class PainterActivity extends GeneralActivity {

    private ImageView imageView;

    Bitmap bitmapMaster;
    Bitmap tempBitmap;
    Canvas canvasMaster;

    int prvX, prvY;

    Paint paintDraw;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painter);

        imageView = findViewById(R.id.painter_image_view);
        Button applyChangeButton = findViewById(R.id.apply_change_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        paintDraw = new Paint();
        paintDraw.setStyle(Paint.Style.FILL);
        paintDraw.setColor(Color.RED);
        paintDraw.setStrokeWidth(10);


        tempBitmap = loadBitmapFromCache();

        Bitmap.Config config;
        if (tempBitmap.getConfig() != null) {
            config = tempBitmap.getConfig();
        } else {
            config = Bitmap.Config.ARGB_8888;
        }

        //bitmapMaster is Mutable bitmap
        bitmapMaster = Bitmap.createBitmap(
                tempBitmap.getWidth(),
                tempBitmap.getHeight(),
                config);

        canvasMaster = new Canvas(bitmapMaster);
        canvasMaster.drawBitmap(tempBitmap, 0, 0, null);

        imageView.setImageBitmap(bitmapMaster);

        imageView.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            int x = (int) event.getX();
            int y = (int) event.getY();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    prvX = x;
                    prvY = y;
                    drawOnProjectedBitMap((ImageView) v, bitmapMaster, prvX, prvY, x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawOnProjectedBitMap((ImageView) v, bitmapMaster, prvX, prvY, x, y);
                    prvX = x;
                    prvY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    drawOnProjectedBitMap((ImageView) v, bitmapMaster, prvX, prvY, x, y);
                    break;
            }
            /*
             * Return 'true' to indicate that the event have been consumed.
             * If auto-generated 'false', your code can detect ACTION_DOWN only,
             * cannot detect ACTION_MOVE and ACTION_UP.
             */
            return true;
        });

        applyChangeButton.setOnClickListener(v -> {
            saveBitmapToCache(bitmapMaster);
            finish();
        });

        cancelButton.setOnClickListener(v -> finish());
    }

    private void drawOnProjectedBitMap(ImageView iv, Bitmap bm,
                                       float x0, float y0, float x, float y) {
        if (!(x < 0 || y < 0 || x > iv.getWidth() || y > iv.getHeight())) {
            //outside ImageView
            float ratioWidth = (float) bm.getWidth() / (float) iv.getWidth();
            float ratioHeight = (float) bm.getHeight() / (float) iv.getHeight();

            canvasMaster.drawLine(
                    x0 * ratioWidth,
                    y0 * ratioHeight,
                    x * ratioWidth,
                    y * ratioHeight,
                    paintDraw);
            imageView.invalidate();
        }
    }

}