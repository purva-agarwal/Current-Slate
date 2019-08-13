package com.example.hp.signin;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.FieldPosition;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.firestore.FieldValue.arrayRemove;
import static com.google.firebase.firestore.FieldValue.delete;

public class Navigate extends AppCompatActivity{
    Navigate.RecycleAdapter adapter;
    ArrayList<Project> projectList;
    private String email = "";

    private CircleImageView circleImageView;
    private TextView nameTextView;
    private FloatingActionButton fab;

    private FirebaseFirestore mRef;
    private CollectionReference usersReference,projectsReference;
    private StorageReference storageReference;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private User user_info;

    private ProgressBar progressBar;
    private RecyclerView recyclerView;


    private CircleImageView navImageView;
    private static final String TAG = "Current Slate";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigate);

        circleImageView = findViewById(R.id.profilephoto);
        nameTextView = findViewById(R.id.name);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(Navigate.this,NewProject.class);
                Navigate.this.startActivity(newIntent);
                Snackbar.make(view, "Create New Project", Snackbar.LENGTH_LONG).setAction("Create", null).show();
            }
        });
        user_info = new User();
        projectList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        projectsReference = FirebaseFirestore.getInstance().collection("Projects");
        recyclerView = (RecyclerView)findViewById(R.id.mrv);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRef = FirebaseFirestore.getInstance();
        usersReference = mRef.collection("Users");
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        setupFirebaseAuth();
    }

    private void getData(FirebaseUser user){
        usersReference.document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    user_info = task.getResult().toObject(User.class);
                    nameTextView.setText(user_info.getName());
                    getImage(user_info.getUrl());
                    if(user_info.getAccounttype().equals("mentor")){
                        fab.setVisibility(View.GONE);
                    }

                    getProjects();
                }
                else{

                }
            }
        });

    }

   private void getProjects(){
        projectList.clear();
        recyclerView.removeAllViews();
        ArrayList<String> userProjects = user_info.getProjectID();
        if(userProjects != null){
            for(String id : userProjects){
                projectsReference.document(id).get().addOnCompleteListener(new OnCompleteListener <DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task <DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            projectList.add(task.getResult().toObject(Project.class));
                            Log.d(TAG, "onComplete: Size: " + projectList.size());
                            setupRecyclerView();
                        }
                        else{

                        }
                    }
                });
            }

        }
   }
    private void setupRecyclerView(){
        Log.d(TAG, "setupRecyclerView: Project List" + projectList.toString());
        RecycleAdapter recycleAdapter = new RecycleAdapter();
        if(recycleAdapter.getItemCount() != 0) {
            recyclerView.setAdapter(recycleAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }


    private void getImage(String url){
        Log.d(TAG, "getImage: " + url);
        StorageReference fileReference = storageReference.child(url);
        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(Navigate.this).load(uri).into(circleImageView);

            }
        });

        progressBar.setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mAuth.signOut();
            System.exit(0);

        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onResume() {
        super.onResume();

    }
    private class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.SimpleItemViewHolder> {
        @Override
        public SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.proj_name,parent,false);
            SimpleItemViewHolder simpleItemViewHolder = new SimpleItemViewHolder(view);
            return  simpleItemViewHolder;
        }
        @Override
            public void onBindViewHolder(SimpleItemViewHolder holder, final int position) {
             holder.title.setText(projectList.get(position).getTitle());
                final Project project = projectList.get(position);
                holder.title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Navigate.this,Display.class);
                        intent.putExtra("project",projectList.get(position));
                        Navigate.this.startActivity(intent);
                    }
                });

        }
        @Override
        public int getItemCount() {
            return projectList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }


        public final  class SimpleItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView title;
            public int position;
            public SimpleItemViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                title = (TextView) itemView.findViewById(R.id.mytextView);
            }

            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(Navigate.this,Display.class);
                newIntent.putExtra("project", projectList.get(position));
                Navigate.this.startActivity(newIntent);

            }
        }

    }


    private void setupFirebaseAuth(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();

                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: Signed in");
                    getData(user);
                    getProjects();
                }
                else{
                    Log.d(TAG, "onAuthStateChanged: Signed out");
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
