package com.example.jil.myapplication.Adapter;
import android.content.Context;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isChat;
    String theLastMessage;

    public UserAdapter(Context mContext,List<User> mUsers,boolean isChat){
        this.mUsers=mUsers;
        this.mContext=mContext;
        this.isChat=isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user= mUsers.get(position);
        holder.username.setText(user.getUsername());
        if(user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else
        {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }

        if(isChat)
        {
            lastMessage(user.getId(),holder.last_msg,holder.msg_count);
            //  count(user.getId(),holder.msg_count);
        }
        else
        {
            // holder.last_msg.setVisibility(View.GONE);
            holder.last_msg.setText(user.getTxt_status());
            holder.msg_count.setVisibility(View.GONE);
        }
        if(isChat) {
            if (user.getStatus().equals("online")) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else
            {
                holder.img_off.setVisibility(View.VISIBLE);
                holder.img_on.setVisibility(View.GONE);
            }

        }
        else
        {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }



        //on clicking user chat will be started
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,MessageActivity.class);
                intent.putExtra("userid",user.getId());
                mContext.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;
        private TextView msg_count;

        public ViewHolder(View itemView)
        {
            super(itemView);

            username=itemView.findViewById(R.id.username);
            profile_image=itemView.findViewById(R.id.profile_image);
            img_on=itemView.findViewById(R.id.img_on);
            img_off=itemView.findViewById(R.id.img_off);
            last_msg=itemView.findViewById(R.id.last_msg);
            msg_count=itemView.findViewById(R.id.msg_count);
        }
    }

  /*  private void count(final String userid, final TextView msg_count)
    {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int c=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    if(firebaseUser!=null)
                    {
                        Chat chat = snapshot.getValue(Chat.class);
                        if(chat.getReceiver().equals(firebaseUser)  && chat.getSender().equals(userid) && chat.isIsseen()==false ){

                            c++;

                        }
                    }
                   // msg_count.setText((String)c);
                    msg_count.setText(c);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }*/

    private void lastMessage(final String userid, final TextView last_msg,final TextView msg_count )
    {
        theLastMessage="default";
        int c=0;

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int c=0;
                int bold=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    if(firebaseUser!=null) {
                        Chat chat = snapshot.getValue(Chat.class);
                        // Log.d("CREATION", "myself" + firebaseUser.getUid());
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            if(chat.getType().equals("image"))
                            {
                                theLastMessage= "image";
                            }
                            else {
                                theLastMessage = chat.getMessage();
                            }
                        }



                        if(chat.getReceiver().equals(firebaseUser.getUid())  && chat.getSender().equals(userid) && chat.isIsseen()==false ){

                            c++;
                            bold=1;

                        }
                        else
                        {
                            bold=0;
                        }

                    }
                }

                if(c==0)
                {
                    msg_count.setVisibility(View.GONE);
                }

                msg_count.setText(Integer.toString(c));

                c=0;

                switch (theLastMessage)
                {
                    case "default":
                        last_msg.setText("No message..");
                        break;

                    default:last_msg.setText(theLastMessage);
                        break;
                }
                if(bold==1)
                {
                    last_msg.setTypeface(null,Typeface.BOLD);
                }
                theLastMessage="default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
