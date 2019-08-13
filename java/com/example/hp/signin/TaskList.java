package com.example.hp.signin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class TaskList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TaskList.RecycleAdapter adapter;
    ArrayList<Task> taskList;
    private FirebaseFirestore mRef;
    private static final String TAG = "Task List";
    private CollectionReference tasksReference,projectReference;
    private FirebaseAuth mAuth;
    private Project mProject;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView Name,Email;
    private ImageView profilePhoto;
    private User user;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(TaskList.this,AddNewTask.class);
                newIntent.putExtra("project",mProject);
                TaskList.this.startActivity(newIntent);
                Snackbar.make(view, "Create New Task", Snackbar.LENGTH_LONG).setAction("Create", null).show();
            }
        });

        Intent intent = getIntent();
        taskList = new ArrayList<>();
        mProject = (Project) intent.getSerializableExtra("project");
        mAuth = FirebaseAuth.getInstance();
        projectReference = FirebaseFirestore.getInstance().collection("Projects");
        tasksReference = projectReference.document(mProject.getProjectID()).collection("Tasks");
        setupFirebaseAuth();

        mRef = FirebaseFirestore.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

    }

    private void getData(){
        final FirebaseUser user1 = mAuth.getCurrentUser();
        FirebaseFirestore.getInstance().collection("Users").document(user1.getUid()).get().addOnCompleteListener(new OnCompleteListener <DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    user = task.getResult().toObject(User.class);
                    Name.setText(user.getName());
                    Email.setText(user.getEmail());
                    StorageReference photoReference = FirebaseStorage.getInstance().getReference("uploads").child(user.getUrl());

                    photoReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(TaskList.this).load(uri).into(profilePhoto);
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
                    getTasks();
                }
                else{
                    Log.d(TAG, "onAuthStateChanged: Signed out");
                }
            }
        };
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

    private void getTasks(){
                taskList.clear();
                tasksReference.get().addOnCompleteListener(new OnCompleteListener <QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();

                            for(DocumentSnapshot documentSnapshot : documentSnapshots){
                                taskList.add(documentSnapshot.toObject(Task.class));
                            }
                            setupRecyclerView();
                        }
                        else{

                        }
                    }
                });
    }

    private void setupRecyclerView(){
        Log.d(TAG, "setupRecyclerView: Task List" + taskList.toString());
        RecycleAdapter recycleAdapter = new RecycleAdapter();
        if(recycleAdapter.getItemCount() != 0) {
            recyclerView.setAdapter(recycleAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.SimpleItemViewHolder> {


        @Override
        public SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_name,parent,false);
            SimpleItemViewHolder simpleItemViewHolder = new SimpleItemViewHolder(view);
            return  simpleItemViewHolder;
        }

        @Override
        public void onBindViewHolder(SimpleItemViewHolder holder, final int position) {
            holder.title.setText(taskList.get(position).getTitle());
            Task task = taskList.get(position);
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent newIntent = new Intent(TaskList.this,ViewTask.class);
                    newIntent.putExtra("task", taskList.get(position));
                    TaskList.this.startActivity(newIntent);

                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    projectReference = FirebaseFirestore.getInstance().collection("Projects");
                    tasksReference = projectReference.document(mProject.getProjectID()).collection("Tasks");
                    Log.d(TAG, "onClick: Task id" + taskList.get(position).getTaskID());
                    tasksReference.document(taskList.get(position).getTaskID()).delete().addOnSuccessListener(new OnSuccessListener <Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            taskList.remove(position);
                            notifyDataSetChanged();
                            notifyItemRemoved(position);
                            Log.d(TAG, "onSuccess: Data deleted");
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return taskList.size();
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
            ImageButton delete;
            public SimpleItemViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                title = (TextView) itemView.findViewById(R.id.mytextView);
                delete = (ImageButton) itemView.findViewById(R.id.deletebtn);
            }

            @Override
            public void onClick(View view) {

            }
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.user) {
            Intent newIntent = new Intent(TaskList.this,Navigate.class);
            TaskList.this.startActivity(newIntent);

        } else if (id == R.id.display) {
            Intent newIntent = new Intent(TaskList.this,Display.class);
            newIntent.putExtra("project",mProject);
            TaskList.this.startActivity(newIntent);

        } else if (id == R.id.chat) {
            Intent intent = new Intent(TaskList.this,Group_Discussion.class);
            intent.putExtra("project",mProject);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
