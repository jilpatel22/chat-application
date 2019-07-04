package com.example.jil.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.Visibility;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
//import android.widget.Toolbar;

import com.example.jil.myapplication.Model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
    EditText username,email,password;
    Button btn_register;
  //  private SignInButton goole_btn;
    private String  mVerificationId;
    private int btn_type=0;

    User userTemp=null;

    AlertDialog dialog;
    int temp=0;


    EditText phone_number;

    private static final int RC_SIGN_IN=1;

    private GoogleApiClient mGoogleApiClient;

    FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseUser user;

    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private FirebaseAuth mAuth;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Toolbar toolbar=findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        // getSupportActionBar().setTitle("Register");
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_register);
        username=findViewById(R.id.username);
       // email=findViewById(R.id.email);
       // password=findViewById(R.id.password);
        btn_register=findViewById(R.id.btn_register);
       // goole_btn=findViewById(R.id.google_btn);
        phone_number= findViewById(R.id.phone_number);
       // otp=findViewById(R.id.otp);
       // otp.setVisibility(View.GONE);
        auth=FirebaseAuth.getInstance();

        mAuth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_type==0)
                {



                if(TextUtils.isEmpty(username.getText().toString()) || TextUtils.isEmpty(phone_number.getText().toString()) )
                {
                    Toast.makeText(RegisterActivity.this, "All Fields are Required", Toast.LENGTH_SHORT).show();
                }
                if(phone_number.getText().toString().length()<12 || phone_number.getText().toString().length()>14)
                {
                    Toast.makeText(RegisterActivity.this, "phone number should be of exact of 10 length ", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String phoneNumber = phone_number.getText().toString();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            RegisterActivity.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks
                    auth.setLanguageCode("fr");




                }
                }

            }
        });

   mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
       @Override
       public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

           signInWithPhoneAuthCredential(phoneAuthCredential);


       }

       @Override
       public void onVerificationFailed(FirebaseException e) {

       }

       @Override
       public void onCodeSent(String verificationId,
                              PhoneAuthProvider.ForceResendingToken token) {



           // Save verification ID and resending token so we can use them later
           mVerificationId = verificationId;
           mResendToken = token;
          // btn_type=1;

          // goole_btn.setVisibility(View.GONE );
           open();

          // otp.setVisibility(View.VISIBLE);
          // btn_register.setText("Verify OTP");

           // ...
       }
   };




      /*  GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(RegisterActivity.this,"you got error",Toast.LENGTH_SHORT).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        goole_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });*/



      /*  btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username=username.getText().toString();
                String txt_email=email.getText().toString();
                String txt_password=password.getText().toString();

                if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email)||TextUtils.isEmpty(txt_password))
                {
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }
                else if(txt_password.length()<6)
                {
                    Toast.makeText(RegisterActivity.this, "Password should not be less than 6", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    register(txt_username,txt_email,txt_password);
                }
            }
        });*/
    }


  /*  private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }*/


  /*  @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            //Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }
    }*/

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithCredential:success");
                            // FirebaseUser user = mAuth.getCurrentUser();
                            // updateUI(user);
                            Toast.makeText(RegisterActivity.this,"authentication failure",Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            //  Log.w(TAG, "signInWithCredential:failure", task.getException());
                            // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            // updateUI(null);

                            user = mAuth.getCurrentUser();
                            String userid= user.getUid();

                            reference=FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            open();



                           // Log.d("phone_no",user.getPhoneNumber());

                          /*  HashMap<String,String> hashMap= new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("phone_no",user.getPhoneNumber());
                            hashMap.put("username",user.getDisplayName());
                            hashMap.put("imageURL","default");
                            hashMap.put("txt_status","hey there i am using chat app");


                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });*/



                        }

                        // ...
                    }
                });


    }



   private void register(final String username, String email, String password)
    {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    FirebaseUser firebaseuser= auth.getCurrentUser();
                    assert firebaseuser!=null;
                    String userid= firebaseuser.getUid();
                    reference=FirebaseDatabase.getInstance().getReference("Users").child(userid);

                    HashMap<String,String> hashMap= new HashMap<>();
                    hashMap.put("id",userid);
                    hashMap.put("username",username);
                    hashMap.put("imageURL","default");
                    hashMap.put("txt_status","hey there i am using chat app");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "This Email id is already in use", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Success", "signInWithCredential:success");

                             user = task.getResult().getUser();
                          final   String userid = user.getUid();
                            reference=FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            HashMap<String,String> hashMap= new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("phone_no",phone_number.getText().toString());
                            hashMap.put("username", username.getText().toString());
                            hashMap.put("imageURL","default");
                            hashMap.put("txt_status","hey there i am using chat app");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });





                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("Failure", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    public void open()
    {
        final AlertDialog.Builder alertDialogueBuilder = new AlertDialog.Builder(RegisterActivity.this);
        final EditText editText = new EditText(RegisterActivity.this);
      //  LayoutInflater inflater = getLayoutInflater();
        alertDialogueBuilder.setTitle("Enter Otp").setView(editText).setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {



                String verificationCode=editText.getText().toString();
                PhoneAuthCredential credential =PhoneAuthProvider.getCredential(mVerificationId,verificationCode);
                signInWithPhoneAuthCredential(credential);



             /*   HashMap<String,String> hashMap= new HashMap<>();
                hashMap.put("id",user.getUid());
                hashMap.put("phone_no",phone_number.getText().toString());
                hashMap.put("username",username.getText().toString());
                hashMap.put("imageURL","default");
                hashMap.put("txt_status","hey there i am using chat app");*/


               /* reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });*/

            }
        });
        dialog = alertDialogueBuilder.create();
        dialog.show();
    }


}
