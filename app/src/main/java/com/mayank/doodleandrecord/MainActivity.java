package com.mayank.doodleandrecord;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import org.jcodec.api.android.AndroidSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int WRITE_EXTERNAL_REQUEST_CODE = 1;
    private MediaProjectionManager mediaProjectionManager;
    private int i = 0;
    private PaintView paintView;
    private  String name;
    private String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paintView = new PaintView(getApplicationContext());
        LinearLayout linearLayout = findViewById(R.id.ll_paint_view);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.addView(paintView, params);
        intListener();

    }

    private void intListener() {
        Button preview = findViewById(R.id.bt_next);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Encoding video...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                int n = paintView.getTotalFrames();
                encodeImages(n, progressDialog);

            }
        });

    }

    private void encodeImages(final int n, final ProgressDialog progressDialog) {
        final SeekableByteChannel[] out = {null};
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    name =  new Date() +"_Output.mp4";
                    path = Environment.getExternalStorageDirectory().toString() + File.separator + name;
                    out[0] = NIOUtils.writableFileChannel(path);
                    // for Android use: AndroidSequenceEncoder
                    AndroidSequenceEncoder encoder = new AndroidSequenceEncoder(out[0], Rational.R(12, 1));
                    for (int i = 1; i < n; i++) {
                        // Generate the image, for Android use Bitmap
                        Bitmap image = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + File.separator + "Folder" + File.separator + i + ".jpg");

                        Bitmap m = image.copy(Bitmap.Config.ARGB_8888, true);
                        if (image.getHeight() % 2 != 0)
                            m.setHeight(image.getHeight() - 1);
                        // Encode the image
                        if (image != null)
                            encoder.encodeImage(m);
                    }
                    // Finalize the encoding, i.e. clear the buffers, write the header, etc.
                    encoder.finish();
                    initUploadActivity(path, name);
                    progressDialog.dismiss();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    paintView.clearFiles();
                    NIOUtils.closeQuietly(out[0]);
                }

                return null;
            }
        }.execute();


    }

    private void initUploadActivity(String path, String name) {
        Intent intent = new Intent(MainActivity.this,UploadAndPreviewActivity.class);
        intent.putExtra("PATH", path);
        intent.putExtra("NAME", name);
        startActivity(intent);
        MainActivity.this.finish();

    }


}
