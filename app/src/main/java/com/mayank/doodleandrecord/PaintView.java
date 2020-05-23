package com.mayank.doodleandrecord;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PaintView extends View {
    private ViewGroup.LayoutParams layoutParams;
    private Path path = new Path();
    private Paint paintBrush = new Paint();
    private static View view;
    private static Context context;
    private static int i = 1;
    private static boolean record = false;

    public PaintView(Context context) {
        super(context);
        this.context = context;
        paintBrush.setColor(Color.RED);
        paintBrush.setAntiAlias(true);
        paintBrush.setStyle(Paint.Style.STROKE);
        paintBrush.setStrokeJoin(Paint.Join.ROUND);
        paintBrush.setStrokeWidth(12);
        layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

//        view.setDrawingCacheEnabled(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                view = PaintView.this;
                view.setBackgroundColor(Color.WHITE);
                view.setDrawingCacheEnabled(true);
                int totalHeight = this.getHeight();
                int totalWidth = this.getWidth();
                view.layout(0, 0, totalWidth, totalHeight);
                view.buildDrawingCache(true);
                record = true;
                takePic();
                return true;
            case MotionEvent.ACTION_UP:
                record = false;
                performClick();
                return true;
            case MotionEvent.ACTION_MOVE:
                takePic();
                path.lineTo(x, y);

        }
        postInvalidate();
        return false;
    }


    static void takePic() {
        final String dirPath = Environment.getExternalStorageDirectory().toString() + File.separator + "Folder";
        final File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdirs();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                while (record == true) {
                    final Bitmap b = Bitmap.createBitmap(view.getDrawingCache());

//                    String fileName = new SimpleDateFormat("yyyyMMddhhmmss'_report.jpg'").format(new Date());
                    String fileName = String.valueOf(i) + ".jpg";
                    i++;
                    File image = new File(dir, fileName);

                    try {
                        FileOutputStream fos = new FileOutputStream(image);
                        Bitmap bm = b;
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();

//            MediaStore.Images.Media.insertImage(context.getContentResolver(), b, "Screen", "screen");
                    } catch (FileNotFoundException e) {

                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                return null;
            }
        }.execute();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paintBrush);

    }

    public int getTotalFrames() {
        return i;
    }

    public void clearFiles() {

        i = 1;
        String dirPath = Environment.getExternalStorageDirectory().toString() + File.separator + "Folder";
        File dir = new File(dirPath);
        if (dir.exists())
            dir.getAbsoluteFile().delete();

    }
}
