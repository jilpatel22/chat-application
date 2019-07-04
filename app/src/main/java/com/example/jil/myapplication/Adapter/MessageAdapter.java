package com.example.jil.myapplication.Adapter;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;


import com.bumptech.glide.Glide;
import com.example.jil.myapplication.ImageActivity;
import com.example.jil.myapplication.MessageActivity;
import com.example.jil.myapplication.Model.Chat;
import com.example.jil.myapplication.Model.User;
import com.example.jil.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;
    String userid;
    ValueEventListener listener;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    StorageReference ref;


    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    public static final int IMG_TYPE_LEFT=2;
    public static final int IMG_TYPE_RIGHT=3;
    // private BroadcastReceiver mDLCompleteReceiver;


    FirebaseUser fuser;

    public MessageAdapter(Context mContext,List<Chat> mChat,String imageurl,String userid){
        this.mChat=mChat;
        this.mContext=mContext;
        this.imageurl=imageurl;
        this.userid=userid;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType== MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else if(viewType==MSG_TYPE_LEFT)
        {

            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else if(viewType==IMG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.image_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else
        {

            View view = LayoutInflater.from(mContext).inflate(R.layout.image_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position) {

        final Chat chat = mChat.get(position);
        if(chat.getType().equals("msg"))
        {
            holder.show_message.setText(chat.getMessage());
        }
        else
        {
            Glide.with(mContext).load(chat.getMessage()).into(holder.img);
            final String imageUrl2;
            imageUrl2=chat.getMessage();
            fuser=FirebaseAuth.getInstance().getCurrentUser();



           /* if(chat.getReceiver().equals(fuser.getUid()))
            {
                if(!chat.isDownloaded())
                {


                    final Bitmap bmp;
                    BitmapDrawable abmp =(BitmapDrawable) holder.img.getDrawable();
                    //abmp =(BitmapDrawable) holder.img.getDrawable();*/

                  /*  if(abmp!=null)
                    {

                        bmp= abmp.getBitmap();
                        Bitmap blurBmp;
                        blurBmp=BlurBuilder.blur(mContext,bmp);
                        holder.img.setImageBitmap(blurBmp);
                    }*/


                   // final Query query= FirebaseDatabase.getInstance().getReference("Chats").orderByChild("message").equalTo(imageUrl2);



                   /* holder.img.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {




                            // holder.img.setImageBitmap(bmp);
                            // BitmapDrawable abmp =(BitmapDrawable) holder.img.getDrawable();
                            //  Glide.with(mContext).load(chat.getMessage()).into(holder.img);
                            // bmp= abmp.getBitmap();



                            if(isExternalStorageWritable())
                            {
                                SaveImage(((BitmapDrawable) holder.img.getDrawable()).getBitmap());
                            }



                          //  ValueEventListener listener;

                            final Query query= FirebaseDatabase.getInstance().getReference("Chats").orderByChild("message").equalTo(imageUrl2);
                           listener= query.addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                   Log.d("image view ","download ");
                                   for(DataSnapshot snapshot : dataSnapshot.getChildren())
                                   {
                                       Chat ch= snapshot.getValue(Chat.class);
                                       if(!ch.isDownloaded())
                                       {
                                           HashMap<String,Object> hashmap = new HashMap<>();
                                           hashmap.put("isDownloaded",true);
                                           snapshot.getRef().updateChildren(hashmap);


                                       }

                                   }




                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {

                               }
                           });*/







                          //  Glide.with(mContext).load(mChat.get(position).getMessage()).into(holder.img);

                           /* final int size=mChat.size();

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




                           /* final Chat ch = mChat.get(position);
                            Glide.with(mContext).load(ch.getMessage()).into(holder.img);
                            DatabaseReference reference;
                            reference=FirebaseDatabase.getInstance().getReference("Chats");
                            Query queryRef= reference.orderByChild("message").equalTo(ch.getMessage());
                            queryRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot : dataSnapshot.getChildren())
                                    {
                                        Chat chat = snapshot.getValue(Chat.class);
                                        if(chat.getMessage().equals(ch.getMessage()))
                                        {
                                            HashMap<String,Object> hashmap = new HashMap<>();
                                            hashmap.put("isDownloaded",true);
                                            snapshot.getRef().updateChildren(hashmap);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });*/




                        /*    return true;
                        }

                    });

                   // query.removeEventListener(listener);
                }




            }*/


            holder.img.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                   // downloadFiles(imageUrl2,DIRECTORY_DOWNLOADS);
                    Intent intent = new Intent(mContext, ImageActivity.class);
                    intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("imageUrl", imageUrl2);
                    mContext.startActivity(intent);
                    return true;
                }
            });











        }

        if(imageurl.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else
        {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }
        if(position==mChat.size()-1)
        {
            if(chat.isIsseen())
            {
                holder.txt_seen.setText("Seen");
            }
            else
            {
                holder.txt_seen.setText("Delivered");
            }
        }
        else
        {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;
        public ImageView img;
        public ViewHolder(View itemView)
        {
            super(itemView);

            show_message=itemView.findViewById(R.id.show_message);
            profile_image=itemView.findViewById(R.id.profile_image);
            txt_seen=itemView.findViewById(R.id.txt_seen);
            img=itemView.findViewById(R.id.img);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser =FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fuser.getUid()))
        {
            if(mChat.get(position).getType().equals("image"))
            {
                return IMG_TYPE_RIGHT;
            }
            else {
                return MSG_TYPE_RIGHT;
            }
        }
        else
        {
            if(mChat.get(position).getType().equals("image"))
            {
                return IMG_TYPE_LEFT;
            }
            else
            {
                return MSG_TYPE_LEFT;
            }

        }
    }

  /*  private  String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = mContext.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void downloadFiles(String url,String destinationDirectory)
    {

        DownloadManager downloadManager =(DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(mContext,destinationDirectory,System.currentTimeMillis() + "." + getFileExtension(uri));
        downloadManager.enqueue(request);
    }*/

    public boolean isExternalStorageWritable()
    {
        String state= Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state))
        {
            return true;
        }
        return false;

    }

    public void SaveImage(Bitmap finalBitmap)
    {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root+ "/saved_images");
        if(!myDir.exists())
        {
            myDir.mkdirs();
        }
        Random generator = new Random();
        int n = 1000;
        n= generator.nextInt(n);
        String fname = "Image-"+n+".jpg";
        File file = new File(myDir,fname);
        if(file.exists())
        {
            file.delete();
        }
        try
        {
            FileOutputStream out  = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG,90,out);
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return;
    }


}
class BlurBuilder {
    private static final float BITMAP_SCALE = 0.6f;
    private static final float BLUR_RADIUS = 15f;

    public static Bitmap blur(Context context, Bitmap image) {
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);

        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

        intrinsicBlur.setRadius(BLUR_RADIUS);
        intrinsicBlur.setInput(tmpIn);
        intrinsicBlur.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;

    }
}
