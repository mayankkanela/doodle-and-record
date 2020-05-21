package com.mayank.doodleandrecord;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class UploadAndPreviewActivity extends AppCompatActivity {
    //views
    private Button button;
    private EditText etTitle;
    private VideoView videoView;
    //data
    private String path;
    private String name;
    private String caption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_and_preview);
        initViews();
        initData();
        initListeners();
    }


    private void initViews() {
        button = findViewById(R.id.bt_upload);
        etTitle = findViewById(R.id.et_title);
        videoView = findViewById(R.id.vv_video);
    }

    private void initData() {
        Intent intent = getIntent();
        path = intent.getStringExtra("PATH");
        name = intent.getStringExtra("NAME");
        videoView.setVideoPath(path);
        videoView.canPause();
        videoView.canSeekForward();
        videoView.canSeekForward();
        MediaController mediaController = new MediaController(UploadAndPreviewActivity.this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
    }

    private void initListeners() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etTitle.getText().toString().isEmpty()) {
                    startUpload();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter title", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void startUpload() {
        ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

    }
}
