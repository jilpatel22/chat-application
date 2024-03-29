package com.example.jil.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jil.myapplication.Fragments.ChatsFragment;
import com.example.jil.myapplication.Fragments.ProfileFragment;
import com.example.jil.myapplication.Fragments.UsersFragment;
import com.example.jil.myapplication.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;
    String imageUrl;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    public static Context contextOfApplication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        contextOfApplication=getApplicationContext();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                // Log.d("STATE","username is: "+user.getUsername());

                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                    imageUrl="default";
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                    imageUrl=user.getImageURL();
                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ImageActivity.class);
                intent.putExtra("imageUrl",imageUrl);
                startActivity(intent);
            }
        });



        TabLayout tabLayout=findViewById(R.id.tab_layout);
        ViewPager viewPager= findViewById(R.id.view_pager);
        ViewPagerAdapter viewPagerAdapter =new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new ChatsFragment(),"Chats");
        viewPagerAdapter.addFragment(new UsersFragment(),"Users");
        viewPagerAdapter.addFragment(new ProfileFragment(),"Profile");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                //due to clear top it will remove main activity and all other previous activity from the top stack and so the user gets offline
                startActivity(new Intent(MainActivity.this,StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP ));

                return true;
        }

        return false;
    }



    class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm)
        {
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        public void addFragment(Fragment fragment, String title)
        {
            fragments.add(fragment);
            titles.add(title);
        }

    }

    public void status(String status,String lastSeenTime)
    {
        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
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

        status("offline","last seen: "+DateFormat.getDateTimeInstance().format(new Date()));
    }

    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }
}

