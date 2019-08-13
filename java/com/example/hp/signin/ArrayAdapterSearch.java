package com.example.hp.signin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ArrayAdapterSearch extends ArrayAdapter<User> {

    private static final String TAG = "ArrayAdapterSearch";


    private LayoutInflater mLayoutInflator;
    private ArrayList<User> mUsers = null;
    private int layoutResource;
    private Context mContext;


    public ArrayAdapterSearch(@NonNull Context context, int resource, @NonNull ArrayList<User> objects) {
        super(context, resource, objects);
        this.mLayoutInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = context;
        this.layoutResource = resource;
        this.mUsers = objects;

    }

    private static class ViewHolder{
        TextView userEmail,userName;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        User user = getItem(position);
        final View result;

        if(convertView == null){
            convertView = mLayoutInflator.inflate(layoutResource,parent,false);

            holder = new ViewHolder();

            holder.userEmail = convertView.findViewById(R.id.emailTextView);
            holder.userName = convertView.findViewById(R.id.nameTextView);



            result = convertView;
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        holder.userEmail.setText(getItem(position).getEmail());
        holder.userName.setText(getItem(position).getName());



        return convertView;
    }
}
