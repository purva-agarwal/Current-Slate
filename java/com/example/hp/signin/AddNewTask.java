package com.example.hp.signin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddNewTask extends AppCompatActivity {

    private Button button;
    private EditText taskTitle,taskMessage;
    private String taskTitleString,taskDescriptionString;
    private int dayOfMonth,month,year;
    private DatePicker datePicker;
    private static final String TAG = "Task List";
    private FirebaseFirestore mRef;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Project mProject;

    private CollectionReference tasksReference,projectReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button = findViewById(R.id.button);
        taskTitle = findViewById(R.id.nameEditText);
        taskMessage = findViewById(R.id.messageEditText);
        datePicker = findViewById(R.id.date_picker);

        Intent intent = getIntent();
        mProject = (Project) intent.getSerializableExtra("project");
        mAuth = FirebaseAuth.getInstance();
        projectReference = FirebaseFirestore.getInstance().collection("Projects");
        tasksReference = projectReference.document(mProject.getProjectID()).collection("Tasks");
        setupFirebaseAuth();

        mRef = FirebaseFirestore.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendData();


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

                }
                else{
                    Log.d(TAG, "onAuthStateChanged: Signed out");
                }
            }
        };
    }

    private void sendData() {
        taskTitleString = taskTitle.getText().toString().trim();
        taskDescriptionString = taskMessage.getText().toString().trim();
        dayOfMonth = datePicker.getDayOfMonth();
        month = datePicker.getMonth() + 1;
        year = datePicker.getYear();
        DocumentReference documentReference = tasksReference.document();
        String ID = documentReference.getId().substring(0,10);
        Task task = new Task(taskTitleString,taskDescriptionString,dayOfMonth,month,year,ID);

        tasksReference.document(task.getTaskID()).set(task).addOnCompleteListener(new OnCompleteListener <Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: Data added");
                    finish();
                }
                else{

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
