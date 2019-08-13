package com.example.hp.signin;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewProject extends AppCompatActivity {

    private Button btnNext;
    private EditText projectTitle, projectDescription;
    private static final String TAG = "New Project";
    private String projectTitleString, projectDescriptionString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnNext = findViewById(R.id.btnNext);
        projectTitle = findViewById(R.id.nameEditText);
        projectDescription = findViewById(R.id.messageEditText);


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getData();
                Intent intent = new Intent(NewProject.this, DatePickerActivity.class);
                intent.putExtra("project_title", projectTitleString);
                intent.putExtra("project_description", projectDescriptionString);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });


    }

    private void getData() {
        projectTitleString = projectTitle.getText().toString().trim();
        projectDescriptionString = projectDescription.getText().toString().trim();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
