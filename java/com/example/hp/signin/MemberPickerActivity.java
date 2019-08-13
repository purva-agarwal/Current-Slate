package com.example.hp.signin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MemberPickerActivity extends AppCompatActivity {
    private static final String TAG = "MemberPickerActivity";

    private EditText searchEditText;
    private Button btnSubmit;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private CollectionReference projectsCollection;

    private ListView searchResultListView,membersListView;

    private Project project = new Project();

    private ArrayList<User> usersResult = new ArrayList<>();
    private ArrayList<User> userMembers = new ArrayList<>();
    private ArrayAdapterSearch arrayAdapterSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_members);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchEditText = findViewById(R.id.search_user_edit_text);

        searchResultListView = findViewById(R.id.list_view_search_result);
        membersListView = findViewById(R.id.list_view_members);

        btnSubmit = findViewById(R.id.btnSubmit);

        mAuth = FirebaseAuth.getInstance();

        projectsCollection = FirebaseFirestore.getInstance().collection("Projects");
        setupFirebaseAuth();

        Intent intent = getIntent();
        if(intent.hasExtra("project")){
            project = (Project) intent.getSerializableExtra("project");
        }
        Log.d(TAG, "onCreate: " + project.toString());
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });
        searchUser();

    }

    private void searchUser(){
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getData(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: " + s.toString());
                Log.d(TAG, "onTextChanged: " + usersResult);
            }
        });
    }

    private void getData(String nameID){
        usersResult.clear();
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Users");

        Query query = (Query) collectionReference.whereEqualTo("name",nameID.toUpperCase());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();

                    for(DocumentSnapshot doc : documentSnapshots){
                        usersResult.add(doc.toObject(User.class));
                    }
                    updateUserList();
                }
                else{

                }
            }
        });



        searchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(!userMembers.contains(usersResult.get(position))){
                    userMembers.add(usersResult.get(position));
                }




                ArrayAdapterSearch arrayAdapterSearch = new ArrayAdapterSearch(MemberPickerActivity.this,R.layout.search_result_list_item,userMembers);
                membersListView.setAdapter(arrayAdapterSearch);
            }
        });
    }

    private void updateUserList(){
        arrayAdapterSearch = new ArrayAdapterSearch(MemberPickerActivity.this,R.layout.search_result_list_item,usersResult);
        searchResultListView.setAdapter(arrayAdapterSearch);

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void sendData(){
        FirebaseUser user = mAuth.getCurrentUser();

        DocumentReference documentReference = projectsCollection.document(user.getUid()).collection("UserProjects").document();
        String documentID = documentReference.getId().substring(0,10);

        project.setProjectID(documentID);

        for (User user1 : userMembers){
            ArrayList<String> projectIDS = new ArrayList<>();
            projectIDS = user1.getProjectID();
            if(projectIDS == null) {
                projectIDS = new ArrayList<>();
            }

            projectIDS.add(project.getProjectID());
            user1.setProjectID(projectIDS);

            CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("Users");

            usersCollection.document(user1.getUserID()).set(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: user data updated");
                    }
                    else{

                    }
                }
            });
        }

        projectsCollection.document(project.getProjectID()).set(project).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: Project data added succesfully");
                }
                else{

                }
            }
        });

        for(User user1 : userMembers) {
            projectsCollection.document(project.getProjectID()).collection("ProjectMembers").document(user1.getUserID()).set(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: User data added");
                    }
                    else{

                    }
                }
            });
        }
        Intent intent = new Intent(MemberPickerActivity.this,Navigate.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

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


}
