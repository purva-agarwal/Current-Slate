package com.example.hp.signin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewTask extends AppCompatActivity {
    private TextView titleTextView,descriptionTextView,deadlineTextView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Task mProject;
    private static final String TAG = "ViewTask";
    private CollectionReference tasksReference,projectReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);

        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        deadlineTextView = findViewById(R.id.deadlineTextView);

        Intent intent = getIntent();
        if(intent.hasExtra("task")){
            mProject = (Task) intent.getSerializableExtra("task");
        }
        titleTextView.setText("Task : " + mProject.getTitle());
        descriptionTextView.setText("Description   : " + mProject.getDescription());
        String deadline = "Deadline : " + mProject.getDayOfMonth() + "/" + mProject.getMonth() + "/" + mProject.getYear();
        deadlineTextView.setText(deadline);

        mAuth = FirebaseAuth.getInstance();
        projectReference = FirebaseFirestore.getInstance().collection("Projects");
        setupFirebaseAuth();

    }
    private void setupFirebaseAuth(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();

                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: Signed in");

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
