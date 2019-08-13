package com.example.hp.signin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class Group_Discussion extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private EditText message;
    private FloatingActionButton Send;

    private Message mMessage;
    private TextView Name,Email;
    private ImageView profilePhoto;
    private User user;
    private CollectionReference projectReference,messageReference;
    private static final String TAG = "Group Discussion";
    private Project mProject;
    private User user_info;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ArrayList<Message> messages = new ArrayList <>();
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_discussion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        mAuth = FirebaseAuth.getInstance();
        message = (EditText) findViewById(R.id.inputMessageEditText);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.sendMessageBtn);

        CollectionReference usersReference = FirebaseFirestore.getInstance().collection("Users");

        usersReference.document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener <DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task <DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    user_info = task.getResult().toObject(User.class);
                }
                else{

                }
            }
        });


        Intent intent = getIntent();

        mProject = (Project) intent.getSerializableExtra("project");


        recyclerView = findViewById(R.id.recyclerViewMessages);
        projectReference = FirebaseFirestore.getInstance().collection("Projects");
        messageReference = projectReference.document(mProject.getProjectID()).collection("Messages");
        setupFirebaseAuth();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        Name = view.findViewById(R.id.name);
        Email = view.findViewById(R.id.email);
        profilePhoto = view.findViewById(R.id.profilephoto);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(message.getText().toString());
                message.setText("");

            }
        });

    }
    private void getData(){
        final FirebaseUser user1 = mAuth.getCurrentUser();
        FirebaseFirestore.getInstance().collection("Users").document(user1.getUid()).get().addOnCompleteListener(new OnCompleteListener <DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task <DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    user = task.getResult().toObject(User.class);
                    Name.setText(user.getName());
                    Email.setText(user.getEmail());
                    StorageReference photoReference = FirebaseStorage.getInstance().getReference("uploads").child(user.getUrl());

                    photoReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(Group_Discussion.this).load(uri).into(profilePhoto);
                        }
                    });
                }
                else{

                }
            }
        });


    }



    private void setupFirebaseAuth(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                
                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: Signed in");
                    getData();
                    getMessages();
                }
                else{
                    Log.d(TAG, "onAuthStateChanged: Signed out");
                }
            }
        };
    }

    private void getMessages(){
        messageReference.orderBy("timeStamp").addSnapshotListener(new EventListener <QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, "onEvent: " + e.getMessage());
                }
                else{
                    messages.clear();
                    for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        messages.add(doc.toObject(Message.class));
                    }
                    sortMessages();

                }
            }
        });
    }

    private void sortMessages(){
        FirebaseUser user = mAuth.getCurrentUser();
        for(Message message : messages){
            if(message.getSenderUserID().equals(user.getUid())){
                message.setState("sent");
            }
            else{
                message.setState("recieved");
            }

        }
        setupRecyclerView();

    }
    private void setupRecyclerView(){
        MessageAdapter messageAdapter = new MessageAdapter(this,messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    private void sendMessage(String message){
        FirebaseUser user = mAuth.getCurrentUser();
        mMessage = new Message(user_info.getName(),user.getUid(),System.currentTimeMillis(),message,"");
        
        messageReference.document().set(mMessage).addOnCompleteListener(new OnCompleteListener <Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: Message Sent");
                }
                else{
                    
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.user) {
            Intent newIntent = new Intent(Group_Discussion.this, Navigate.class);

            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Group_Discussion.this.startActivity(newIntent);

        } else if (id == R.id.display) {
            Intent newIntent = new Intent(Group_Discussion.this, Display.class);
            newIntent.putExtra("project",mProject);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Group_Discussion.this.startActivity(newIntent);
        } else if (id == R.id.task) {
            Intent newIntent = new Intent(Group_Discussion.this, TaskList.class);
            newIntent.putExtra("project",mProject);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Group_Discussion.this.startActivity(newIntent);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
