package com.example.hp.signin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatePickerActivity extends AppCompatActivity {

    private Button btnNext;
    private String projectTitle,projectDescription;
    private int dayOfMonth,month,year;
    private static final String TAG = "New Project";
    private DatePicker datePicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        datePicker = findViewById(R.id.date_picker);
        btnNext = findViewById(R.id.btnNext);

        Intent intent = getIntent();
        if(intent.hasExtra("project_title") && intent.hasExtra("project_description")){
            projectTitle = intent.getStringExtra("project_title");
            projectDescription = intent.getStringExtra("project_description");


        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DatePickerActivity.this,MemberPickerActivity.class);
                getDate();
                Project project = new Project("",projectTitle,projectDescription,dayOfMonth,month,year);
                intent.putExtra("project",project);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

            }
        });

    }

    private void getDate(){
         dayOfMonth = datePicker.getDayOfMonth();
         month = datePicker.getMonth() + 1;
         year = datePicker.getYear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
