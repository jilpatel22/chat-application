package com.example.jil.myapplication.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jil.myapplication.Adapter.UserAdapter;
import com.example.jil.myapplication.Model.Chat;
import com.example.jil.myapplication.Model.User;
//import com.example.jil.myapplication.Notifications.Token;
import com.example.jil.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    FirebaseUser fuser;
    DatabaseReference reference;

    private List<String> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        userList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getSender().equals(fuser.getUid())) {
                        userList.add(chat.getReceiver());
                    }
                    if (chat.getReceiver().equals(fuser.getUid())) {
                        userList.add(chat.getSender());
                    }
                }

                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    /*  private void updateToken(String token)
      {
          DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
          Token token1 = new Token(token);
          reference.child(fuser.getUid()).setValue(token);
      }
  */
    private void readChats()
    {
        mUsers= new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    User user = snapshot.getValue(User.class);

                    for(String id:userList)
                    {
                        if(user.getId().equals(id))
                        {
                            mUsers.add(user);
                            break;
                            //if(mUsers.size()!=0)
                            //{
                               /* for(User user1: mUsers)
                                {
                                    if(!user.getId().equals(user1.getId()))
                                    {
                                        mUsers.add(user);
                                    }
                                }*/

                             /*   ListIterator<User> iterator=mUsers.listIterator();
                             while(iterator.hasNext()){
                                 User user1=iterator.next();
                                 if(!user.getId().equals(user1.getId()))
                                 {
                                     iterator.add(user);
                                 }

                             }
                            }
                            else
                            {
                                mUsers.add(user);
                            }*/
                        }
                    }
                }

                userAdapter = new UserAdapter(getContext(),mUsers,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }





}
