package com.example.jil.myapplication;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.jil.myapplication.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ImageActivity extends AppCompatActivity {

    ImageView profile_image;
    String imageUrl;
    Intent intent;
    DatabaseReference reference;
    ImageButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        btn=findViewById(R.id.download);

        profile_image=findViewById(R.id.image);
        intent=getIntent();
        imageUrl=intent.getStringExtra("imageUrl");
        if(imageUrl.equals("default"))
        {
            profile_image.setImageResource(R.mipmap.ic_launcher);
            btn.setVisibility(View.GONE);

        }
        else
        {
            Glide.with(getApplicationContext()).load(imageUrl).into(profile_image);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFiles(imageUrl,DIRECTORY_DOWNLOADS,ImageActivity.this);
            }
        });



    }
    public void status(String status,String lastSeenTime)
    {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        hashMap.put("lastSeen",lastSeenTime);
        reference.updateChildren(hashMap);

    }

    private  String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = (ImageActivity.this).getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void downloadFiles(String url,String destinationDirectory,Context context)
    {

        DownloadManager downloadManager =(DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        Log.d("image uri",uri.toString());

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,destinationDirectory,System.currentTimeMillis() + "." + "jpg");
        downloadManager.enqueue(request);
    }

    @Override
    protected void onResume() {
        super.onResume();

        status("online","online");
    }

    @Override
    protected void onPause() {
        super.onPause();

        status("offline","last seen: "+ DateFormat.getDateTimeInstance().format(new Date()));
    }
}
