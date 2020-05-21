package com.mayank.doodleandrecord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
                    Toast.makeText(UploadAndPreviewActivity.this, "Please Enter title", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void startUpload() {
        final ProgressDialog progressDialog = new ProgressDialog(UploadAndPreviewActivity.this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        caption = etTitle.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, String> item = new HashMap<>();
        item.put("caption", caption);
        item.put("videoTitle", name);
        db.collection("record").add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                progressDialog.setMessage("Uploading Video...");
                uploadVideo(progressDialog, documentReference);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadAndPreviewActivity.this, "Failed to upload!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void uploadVideo(final ProgressDialog progressDialog, DocumentReference documentReference) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference videoReference = storageReference.child("videos/" + documentReference.getId());
        UploadTask uploadTask = videoReference.putFile(Uri.fromFile(new File(path)));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UploadAndPreviewActivity.this, "Success", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                UploadAndPreviewActivity.this.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadAndPreviewActivity.this, "Failed to upload video!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
