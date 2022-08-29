package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;

public class PainterActivity extends GeneralActivity {

    private ImageView imageView;
    private Slider strokeWidthSlider;
    private boolean isDrawText = false;
    private FrameLayout layout;

    FrameLayout.LayoutParams params;

    private TextView tv;

    private int offset_x = 0;
    private int offset_y = 0;

    private EditText edt;

    Bitmap bitmapMaster;
    Bitmap tempBitmap;
    Canvas canvasMaster;

    int prvX, prvY;

    Paint paintDraw;

    private int _xDelta;
    private int _yDelta;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painter);

        imageView = findViewById(R.id.painter_image_view);
        Button applyChangeButton = findViewById(R.id.apply_change_button);
        Button cancelButton = findViewById(R.id.cancel_button);
        Button addTextButton = findViewById(R.id.add_text_button);
        layout = findViewById(R.id.painter_image_layout);


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
            float scaleRatio = 1.0f / imageView.getWidth() * bitmapMaster.getWidth();
            float textSize = tv.getTextSize();
//            bitmapMaster = Bitmap.createScaledBitmap(
//                    bitmapMaster,
//                    imageView.getWidth(),
//                    imageView.getHeight(),
//                    false
//            );
            Log.i("HIEU", scaleRatio + " ");
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(textSize);
            canvasMaster.drawBitmap(bitmapMaster, 0, 0, null);
            canvasMaster.drawText((String) tv.getText(),
                    tv.getPivotY(),
                    tv.getPivotY(),
                    paint);
            imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmapMaster,
                    tempBitmap.getWidth(),
                    tempBitmap.getHeight(),
                    false));
            imageView.invalidate();
            saveBitmapToCache(bitmapMaster);
            finish();
        });

        cancelButton.setOnClickListener(v -> {
            finish();
        });


//        layout.setOnDragListener(this);

        addTextButton.setOnClickListener(view -> {
            TextView editText = new TextView(this);
            editText.setText("HIEU");
            editText.setTextSize(60);
            editText.setTextColor(Color.BLACK);
            editText.setTag("editText");
            editText.setFocusable(false);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layout.addView(editText, layout.getChildCount(), layoutParams);
            tv = editText;
//            editText.setOnLongClickListener(this);
//            editText.setOnDragListener(this);
            editText.setOnTouchListener((view1, motionEvent) -> {
                final int X = (int) motionEvent.getRawX();
                final int Y = (int) motionEvent.getRawY();
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) view1.getLayoutParams();
                        _xDelta = X - lParams.leftMargin;
                        _yDelta = Y - lParams.topMargin;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view1.getLayoutParams();
                        params.leftMargin = X - _xDelta;
                        params.topMargin = Y - _yDelta;
                        params.rightMargin = -250;
                        params.bottomMargin = -250;
                        view1.setLayoutParams(params);
                        break;
                }
                layout.invalidate();
                return true;
            });

        });
    }

    private void drawOnProjectedBitMap(ImageView iv, Bitmap bm,
                                       float x0, float y0, float x, float y) {
        if (!(x < 0 || y < 0 || x > iv.getWidth() || y > iv.getHeight())) {
            //outside ImageView
            float ratioWidth = (float) bm.getWidth() / (float) iv.getWidth();
            float ratioHeight = (float) bm.getHeight() / (float) iv.getHeight();
            if (isDrawText) {
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
}