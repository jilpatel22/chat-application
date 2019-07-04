package com.example.jil.myapplication;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.sip.SipAudioCall;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.jil.myapplication.Adapter.MessageAdapter;
import com.example.jil.myapplication.Model.Chat;
import com.example.jil.myapplication.Model.User;
//import com.example.jil.myapplication.Notifications.Client;
//import com.example.jil.myapplication.Notifications.Data;
//import com.example.jil.myapplication.Notifications.MyResponse;
//import com.example.jil.myapplication.Notifications.Sender;
//import com.example.jil.myapplication.Notifications.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;
    ImageButton btn_send;
    EditText text_send;
    String phone_number;
   // SipManager sipManager = null;
   // SipProfile sipProfile = null;


    private Uri imageUri;
    private StorageTask uploadTask;
    StorageReference storageReference;
    String status;
    String imageUrl;

    FirebaseUser fuser;
    DatabaseReference reference;

    MessageAdapter messageAdapter;
    List<Chat> mChat;
    private static final int IMAGE_REQUEST=1;

    RecyclerView recyclerView;

    Intent intent;

    ValueEventListener seenListener;
    String userid;

    TextView lastSeen;

    // APIService apiService;

    boolean notify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });



        // apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        //for adding message from the bottom of the recycler view
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        lastSeen= findViewById(R.id.last_seen);

        text_send=findViewById(R.id.text_send);
        btn_send=findViewById(R.id.btn_send);



        intent=getIntent();
        userid=intent.getStringExtra("userid");
        fuser=FirebaseAuth.getInstance().getCurrentUser();
        storageReference =FirebaseStorage.getInstance().getReference("uploads");



        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;
                String msg= text_send.getText().toString();
                if(!msg.equals(""))
                {
                    sendMessage(fuser.getUid(),userid,msg);
                }
                else
                {
                    Toast.makeText(MessageActivity.this,"you cant send empty message",Toast.LENGTH_SHORT).show();

                }
                text_send.setText("");
            }
        });



        fuser=FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                phone_number=user.getPhone_no();
                if(user.getImageURL().equals("default"))
                {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                    imageUrl="default";

                }
                else
                {

                    // Glide.with(MessageActivity.this).load(user.getImageURL()).into(profile_image);
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                    imageUrl=user.getImageURL();

                }
                lastSeen.setText(user.getLastSeen());

                readMessage(fuser.getUid(),userid,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this,ImageActivity.class);
                intent.putExtra("imageUrl",imageUrl);
                startActivity(intent);
            }
        });

        seenMessage(userid);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upload_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.share_img: openIamge();
                        break;
            case R.id.audio_call: phoneCall();
                        break;

        }

        return true;
    }

    private void phoneCall()
    {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+phone_number));

        startActivity(callIntent);

    }









    private void openIamge()
    {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);

    }

    private  String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data !=null && data.getData()!=null)
        {
            imageUri=data.getData();

            uploadImage();
        }
    }

    private void uploadImage() {
        // final ProgressDialog pd = new ProgressDialog(getContext());
        // pd.setMessage("Uploading");
        //pd.show();
        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);

            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    HashMap<String,Object> map = new HashMap<>();
                                    map.put("sender",fuser.getUid());
                                    map.put("receiver",userid);
                                    map.put("message",uri.toString());
                                    map.put("isseen",false);
                                    map.put("type","image");
                                    map.put("isDownloaded",false);

                                    // map.put("imageURL",taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                                    //  Log.d("url:",taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                                    // reference.updateChildren(map);
                                    reference1.child("Chats").push().setValue(map);

                                }
                            });

                            //  Glide.with(getContext()).load(user.getImageURL()).into(profile_image);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MessageActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            /*double progress =(100.0 *taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());*/


                        }
                    });
        }
        else
        {
            Toast.makeText(MessageActivity.this,"NO image selected",Toast.LENGTH_SHORT).show();
        }
    }



    private void seenMessage(final String userid)
    {
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid))
                    {
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);
        hashMap.put("type","msg");
        hashMap.put("isDownloaded",true);

        reference.child("Chats").push().setValue(hashMap);

       /* final String msg=message;
        reference= FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user= dataSnapshot.getValue(User.class);
                if(notify) {
                    sendNotification(receiver, user.getUsername(), msg);
                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }

   /* private  void sendNotification(String receiver, final String username, final String message)
    {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query= tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(),R.mipmap.ic_launcher,username+":"+message,"New Message",userid);

                    Sender sender = new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200)
                                    {
                                        if(response.body().success!=1) {
                                            Toast.makeText(MessageActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                        }
                                        }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }*/


    /*   private  void sendNotification(String receiver,String username,String message)
       {
           DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
           Query query= tokens.orderByKey().equalTo(receiver);
           query.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   for(DataSnapshot snapshot : dataSnapshot.getChildren())
                   {
                       Token token = snapshot.getValue(Token.class);
                       Data data = new Data(fuser.getUid(),R.mipmap.ic_launcher+":"+message,"New Message",userid);

                       Sender sender = new Sender(data,token.getToken());

               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           })
       }
   */
    private void readMessage( final String myid,final String userid, final String imageurl)
    {
        mChat=new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(myid) )
                    {
                        mChat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this,mChat,imageurl,userid);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void status(String status,String lastSeenTime)
    {
        reference=FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        hashMap.put("lastSeen",lastSeenTime);
        reference.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();

        status("online","online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);


        status("offline","last seen: "+DateFormat.getDateTimeInstance().format(new Date()));


      /*  final int size=mChat.size();

        DatabaseReference reference1=  FirebaseDatabase.getInstance().getReference("Chats");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int pos=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {

                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid) && chat.getType().equals("image"))
                    {
                        if(pos==size)
                        {
                            break;
                        }
                        else
                        {
                            while((!mChat.get(pos).getReceiver().equals(fuser.getUid()) || mChat.get(pos).getType().equals("msg"))&& pos!=(size-1))
                            {
                                pos++;
                            }
                            if(mChat.get(pos).getType().equals("image"))
                            {
                                if(!chat.isDownloaded())
                                {
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("isDownloaded", mChat.get(pos).isDownloaded());
                                    snapshot.getRef().updateChildren(hashMap);

                                }
                            }


                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/



    }
}
