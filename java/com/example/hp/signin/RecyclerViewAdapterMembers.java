package com.example.hp.signin;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapterMembers extends RecyclerView.Adapter<RecyclerViewAdapterMembers.ViewHolder>{

    private ArrayList<User> mUsers;
    private ArrayList<Uri> imageUris;
    private Context mContext;


    private FirebaseAuth mAuth;


    public RecyclerViewAdapterMembers(ArrayList<User> mUsers, ArrayList<Uri> imageUris, Context mContext, FirebaseAuth mAuth) {
        this.mUsers = mUsers;
        this.imageUris = imageUris;
        this.mContext = mContext;

        this.mAuth = mAuth;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_members,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.nameTextVIew.setText(mUsers.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout parentLayout;
        TextView nameTextVIew;
        ImageView photoView;



        public ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.linearLayout);
            nameTextVIew = itemView.findViewById(R.id.member_name);
            photoView = itemView.findViewById(R.id.member_photo);
        }
    }

}
