package com.example.hp.signin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Display extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView titleTextView,descriptionTextView,deadlineTextView;
    private static final String TAG = "Current Slate";

    RecyclerView membersrecyclerView,mentorsrecyclerView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private final ArrayList<Uri> imageUris = new ArrayList<>();
    private final ArrayList<User> mUsers = new ArrayList<>();
    private final ArrayList<User> mentors = new ArrayList<>();
    private final ArrayList<User> mentees = new ArrayList<>();
    private TextView Name,Email;
    private ImageView profilePhoto;

    private User user;

    private Project mProject;

    private CollectionReference usersReference,projectReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        deadlineTextView = findViewById(R.id.deadlineTextView);

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
        Intent intent = getIntent();
        if(intent.hasExtra("project")){
            mProject = (Project) intent.getSerializableExtra("project");
        }

        titleTextView.setText(""+ mProject.getTitle());
        descriptionTextView.setText("" + mProject.getDescription());
        String deadline = "Deadline         : " + mProject.getDayOfMonth() + "/" + mProject.getMonth() + "/" + mProject.getYear();

        deadlineTextView.setText(deadline);
        membersrecyclerView = findViewById(R.id.members_recycler_view);
        mentorsrecyclerView = findViewById(R.id.mentors_recycler_view);

        mAuth = FirebaseAuth.getInstance();

        usersReference = FirebaseFirestore.getInstance().collection("Users");
        projectReference = FirebaseFirestore.getInstance().collection("Projects");
        setupFirebaseAuth();
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
                    StorageReference photoReference = (StorageReference) FirebaseStorage.getInstance().getReference("uploads").child(user.getUrl());

                    photoReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener <Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(Display.this).load(uri).into(profilePhoto);
                        }
                    });
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
            Intent newIntent = new Intent(Display.this,Navigate.class);
            newIntent.putExtra("project",mProject);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Display.this.startActivity(newIntent);

        } else if (id == R.id.task) {
            Intent newIntent = new Intent(Display.this,TaskList.class);
            newIntent.putExtra("project",mProject);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Display.this.startActivity(newIntent);
        } else if (id == R.id.chat) {
            Intent intent = new Intent(this,Group_Discussion.class);
            intent.putExtra("project",mProject);
            intent.putExtra("project",mProject);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void getUsers(){
        mUsers.clear();
        projectReference.document(mProject.getProjectID()).collection("ProjectMembers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();

                    for(DocumentSnapshot documentSnapshot : docs){
                        mUsers.add(documentSnapshot.toObject(User.class));
                    }
                    calculate();
                    setupRecyclerView();
                }
                else{

                }
            }
        });



    }

    private void calculate(){
        mentees.clear();
        mentors.clear();
        for(User mentor: mUsers){
            if(mentor.getAccounttype().equals("mentor")){
                mentors.add(mentor);
            }
            else{
                mentees.add(mentor);
            }
        }

    }



    private void setupRecyclerView(){
        Log.d(TAG, "setupRecyclerView: Users: " + mUsers);
        Log.d(TAG, "setupRecyclerView: Image Urls" + imageUris);
        RecyclerViewAdapterMembers recyclerViewAdapterMembers = new RecyclerViewAdapterMembers(mentees,imageUris,Display.this,mAuth);
        membersrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        membersrecyclerView.setAdapter(recyclerViewAdapterMembers);

        RecyclerViewAdapterMembers recyclerViewAdapterMembers1 = new RecyclerViewAdapterMembers(mentors,imageUris,Display.this,mAuth);
        mentorsrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mentorsrecyclerView.setAdapter(recyclerViewAdapterMembers1);

    }
    private void setupFirebaseAuth(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();

                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: Signed in");
                    getData();
                    getUsers();
                }
                else{
                    Log.d(TAG, "onAuthStateChanged: Singed out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
