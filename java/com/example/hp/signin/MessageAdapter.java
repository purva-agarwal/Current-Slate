package com.example.hp.signin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter {

    Context mContext;
    ArrayList<Message> mMessages;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECIEVED = 2;

    public MessageAdapter(Context mContext, ArrayList <Message> mMessages) {
        this.mContext = mContext;
        this.mMessages = mMessages;

    }

    @Override
    public int getItemViewType(int position) {
        if(mMessages.get(position).getState().equals("recieved")){
            return VIEW_TYPE_MESSAGE_RECIEVED;
        }
        else{
            return VIEW_TYPE_MESSAGE_SENT;
        }


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_MESSAGE_SENT) {
            View viewSent = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_sent,parent,false);
            ViewHolder2 viewHolder2 = new ViewHolder2(viewSent);
            return viewHolder2;
        }
        else if(viewType==VIEW_TYPE_MESSAGE_RECIEVED){
            View viewRecieved = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_recieved,parent,false);
            ViewHolder1 viewHolder1 = new ViewHolder1(viewRecieved);
            return viewHolder1;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case VIEW_TYPE_MESSAGE_RECIEVED:
                ((ViewHolder1)holder).bind(mMessages.get(position));

                break;
            case VIEW_TYPE_MESSAGE_SENT:
                ((ViewHolder2)holder).bind(mMessages.get(position));



                break;
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder{
        TextView userNameTextViewRecieved,messageTextViewRecieved;
        LinearLayout parentLayoutRecieved;
        public ViewHolder1(View itemView) {
            super(itemView);
            userNameTextViewRecieved = itemView.findViewById(R.id.userNameTextViewRecieved);
            messageTextViewRecieved = itemView.findViewById(R.id.messageTextViewRecieved);
            parentLayoutRecieved = itemView.findViewById(R.id.parentLayoutRecieved);
        }

        void bind(Message message){
            userNameTextViewRecieved.setText(message.getUserName());
            messageTextViewRecieved.setText(message.getMessageText());
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder{
        TextView userNameTextViewSent,messageTextViewSent;
        LinearLayout parentLayoutSent;


        public ViewHolder2(View itemView) {
            super(itemView);
            userNameTextViewSent = itemView.findViewById(R.id.userNameTextViewSent);
            messageTextViewSent = itemView.findViewById(R.id.messageTextViewSent);
            parentLayoutSent = itemView.findViewById(R.id.parentLayoutSent);


        }


        void bind(Message message){
            userNameTextViewSent.setText(message.getUserName());
            messageTextViewSent.setText(message.getMessageText());
        }
    }

}
