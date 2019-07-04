package com.example.jil.myapplication.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jil.myapplication.MainActivity;
import com.example.jil.myapplication.Model.User;
import com.example.jil.myapplication.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    CircleImageView image_profile;
    TextView username;

    FirebaseUser fuser;
    DatabaseReference reference;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private Uri imageUri;
    private StorageTask uploadTask;
    TextView txt_status;
    ImageButton modify_btn;

    EditText edit_status;
    String status;
    // User user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        image_profile=view.findViewById(R.id.profile_image);
        username=view.findViewById(R.id.username);
        txt_status=view.findViewById(R.id.txt_status);
        modify_btn=view.findViewById(R.id.modify_btn);
        edit_status=view.findViewById(R.id.edit_status);

        storageReference =FirebaseStorage.getInstance().getReference("uploads");


        fuser= FirebaseAuth.getInstance().getCurrentUser();
        reference=FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
               /* if(user.getTxt_status()==null)
                {
                    return;
                }*/
                status=user.getTxt_status();
                txt_status.setText(user.getTxt_status());
                // edit_status.setText(user.getTxt_status());

                if(user.getImageURL().equals("default"))
                {
                    image_profile.setImageResource(R.mipmap.ic_launcher);
                }
                else
                {
                    if(getActivity()==null)
                    {
                        return;
                    }

                    Glide.with(getContext()).load(user.getImageURL()).into(image_profile);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        modify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_status.setVisibility(View.VISIBLE);

                edit_status.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {


                        Log.d("status ","got changed");
                        DatabaseReference reference1= FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String,Object> hashMap = new HashMap<>();


                        hashMap.put("txt_status", s.toString());
                        reference1.updateChildren(hashMap);
                        // edit_status.setText("");
                        edit_status.setVisibility(View.GONE);


                    }
                });



            }
        });

     /*   edit_status.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                Log.d("status ","got changed");
                DatabaseReference reference1= FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                HashMap<String,Object> hashMap = new HashMap<>();


                hashMap.put("txt_status", s.toString());
                reference1.updateChildren(hashMap);
                // edit_status.setText("");
                edit_status.setVisibility(View.GONE);


            }
        });*/

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIamge();
            }
        });

        return view;
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
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        //final ProgressDialog pd = new ProgressDialog(getContext());
        //pd.setMessage("Uploading");
        //pd.show();

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
                                    map.put("imageURL",uri.toString());
                                    // map.put("imageURL",taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                                    //  Log.d("url:",taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                                    reference.updateChildren(map);

                                }
                            });

                            //  Glide.with(getContext()).load(user.getImageURL()).into(profile_image);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

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
            Toast.makeText(getContext(),"NO image selected",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data !=null && data.getData()!=null)
        {
            imageUri=data.getData();

            uploadImage();
        }

    }
}
