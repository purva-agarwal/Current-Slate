package com.example.hp.signin;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;


public class SignUp extends AppCompatActivity implements View.OnClickListener {
    private Uri imageUri;
    //defining view objects
    private static final int PICK_IMAGE_REQUEST = 1;

    private static final String TAG = "Current Slate";
    String accounttype;
    
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private Button buttonSignup;
    private TextView textViewLogin;
    private CircleImageView profileImageView;
    private CheckBox mentorcb;
    private CheckBox menteecb;

    private User user1;

    private ProgressDialog progressDialog;
    private FirebaseFirestore mRef;
    private CollectionReference usersCollection;
    private StorageReference storageReference;

    private boolean imagePicked;
    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        imagePicked = false;
        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        mRef = FirebaseFirestore.getInstance();
        usersCollection = mRef.collection("Users");

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        mentorcb = findViewById(R.id.mentor);
        menteecb = findViewById(R.id.mentee);
        //if getCurrentUser does not returns null
        if(firebaseAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), Navigate.class));
        }

        //initializing views
        editTextName = (EditText) findViewById(R.id.input_name);
        editTextEmail = (EditText) findViewById(R.id.input_email);
        editTextPassword = (EditText) findViewById(R.id.input_password);
        textViewLogin = (TextView) findViewById(R.id.btn_login);
        profileImageView = findViewById(R.id.profilephoto);
        buttonSignup = (Button) findViewById(R.id.btn_signup);

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignup.setOnClickListener(this);
        textViewLogin.setOnClickListener(this);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openGallery();
            }
        });
        selectItem();
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile(String imageUrl){
        if(imageUri != null) {
            final StorageReference fileReference  = storageReference.child(imageUrl);

            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    user1.setUrl(fileReference.getDownloadUrl().toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUp.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(this,"Please Select a profile photo",Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode != RESULT_CANCELED && data != null && data.getData() != null){
            imageUri = data.getData();
            imagePicked = true;
            Glide.with(this).load(imageUri).into(profileImageView);

        }
    }

    protected void selectItem(){
        menteecb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mentorcb.isChecked()){
                    mentorcb.setChecked(false);
                }
                accounttype = "mentee";
            }
        });

        mentorcb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menteecb.isChecked()){
                    menteecb.setChecked(false);
                }
                accounttype = "mentor";
            }
        });

    }


    private void registerUser(){

        //getting email and password from edit texts
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        //checking if name,email and passwords are empty
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please enter Name",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter Email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter Password",Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            sendVerificationEmail();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String imageUrl = System.currentTimeMillis() + "." + getFileExtension(imageUri);
                            user1 = new User(name.toUpperCase(),email,user.getUid(),imageUrl,null,accounttype);
                            usersCollection.document(user1.getUserID()).set(user1);
                            uploadFile(imageUrl);
                        }
                        progressDialog.dismiss();


                    }
                });

    }
    private void sendVerificationEmail()
    {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // email sent
                                finish();
                                Toast.makeText(SignUp.this,"Please verify your email-id by clicking on the verification link sent on your email account and log in...",Toast.LENGTH_LONG).show();
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(SignUp.this, LoginActivity.class));

                            }
                            else
                            {
                                // email not sent, so display message and restart the activity or do whatever you wish to do

                                //restart this activity
                                overridePendingTransition(0, 0);
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());

                            }
                        }
                    });
        }
    }

    @Override
    public void onClick(View view) {

        if(view == buttonSignup){
            if(!imagePicked){
                Toast.makeText(this,"Please select a profile photo",Toast.LENGTH_SHORT).show();
            }else {
                registerUser();
            }
            }

        if(view == textViewLogin){
            //open login activity when user taps on the already registered textview
            startActivity(new Intent(SignUp.this, LoginActivity.class));
        }

    }
}
