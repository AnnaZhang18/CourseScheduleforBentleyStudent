package com.example.eleanor.coursescheduleforbentleystudent;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class LogInActivity extends AppCompatActivity {
    private Button login;
    public static SQLiteDatabase db;
    private Cursor cursor;
    private EditText email;
    private EditText password;
    public static String account;
    private int count;
    private String str;
    private OutputStreamWriter out;
    private CheckBox remember;
    private NotificationManager mNotificationManager;
    private Notification notifyDetails;
    private int SIMPLE_NOTFICATION_ID;
    private String contentTitle = "Simple Notification Example";
    private String contentText = "Get back to Application by clicking me";
    private String tickerText = "New Alert, Click Me !!!";
/*
This activity file aims to perform a function to allow student enter their email address and password to
enter matched student menu page. Meanwhile, perform a password matching function to check if password entered is match
with the password stored in database.

In addition, two users' information, matched courses, professor's name, phone and email address are stored in database by
using SQLite function under this activity.
 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        login = findViewById(R.id.loginbtn);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        remember = findViewById(R.id.remember);
        try {
            FileInputStream in = openFileInput("account.txt");
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(isr);
            String str = null;
            if(getFilesDir().exists()) {
                if ((str = reader.readLine()) != null) {
                    email.setText(str);
                    remember.setChecked(true);
                }
            }
            reader.close();

        }catch (IOException e) {}

//       create database connection and infrastructure

        db = openOrCreateDatabase("CS680BS.db",
                Context.MODE_PRIVATE, null);
        db.setLocale(Locale.getDefault());
        db.execSQL("Drop TABLE IF EXISTS 'Users'");
        db.execSQL("Create TABLE IF not EXISTS 'Users'('Email' Varchar(30) not null, 'Password' Varchar(30) not null)");
        db.execSQL("Drop TABLE IF EXISTS 'Teacher'");
        db.execSQL("Create TABLE IF not EXISTS 'Teacher'('TeacherID' Varchar(30) PRIMARY KEY not null, 'Name' Varchar(30) not null,'TeacherEmail' Varchar(30) not null,'Phone' Varchar(30) not null)");
        db.execSQL("Drop TABLE IF EXISTS 'Location'");
        db.execSQL("Create TABLE IF not EXISTS 'Location'('LocationID' Varchar(30) PRIMARY KEY not null, 'lat' Varchar(30) not null,'longt' Varchar(30) not null)");
        db.execSQL("Drop TABLE IF EXISTS 'Courses'");
        db.execSQL("Create TABLE IF not EXISTS 'Courses'('CourseID' Varchar(30) PRIMARY KEY not null,'TeacherID' Varchar(30)  not null,'Coursename' Varchar(30) not null,FOREIGN KEY('TeacherID') REFERENCES 'Teacher'('TeacherID'))");
        db.execSQL("Drop TABLE IF EXISTS 'CoursesDetail'");
        db.execSQL("Create TABLE IF not EXISTS 'CoursesDetail'('CoursesSection' Varchar(30) PRIMARY KEY not null,'CourseID' Varchar(30)  not null,'LocationID' Varchar(30) not null,'Day' Varchar(30) not null,'Time' Varchar(30) not null,FOREIGN KEY('CourseID') REFERENCES 'Courses'('CourseID'), FOREIGN KEY('LocationID') REFERENCES 'Location'('LocationID'))");
        db.execSQL("Drop TABLE IF EXISTS 'Notedetail'");
        db.execSQL("Create TABLE IF not EXISTS 'Notedetail'('Note' Varchar(30) not null,'Assignmentnote' Varchar(30) ,PRIMARY KEY(Note,Assignmentnote))");
        db.execSQL("Drop TABLE IF EXISTS 'Schedule'");
        db.execSQL("Create TABLE IF not EXISTS 'Schedule'('CoursesSection' Varchar(30) not null,'Email' Varchar(30) not null, 'Note' Varchar(30) not null, PRIMARY KEY(CoursesSection,Email),FOREIGN KEY('CoursesSection') REFERENCES 'CoursesDetail'('CoursesSection'),FOREIGN KEY('Email') REFERENCES 'Users'('Email'),FOREIGN KEY('Note') REFERENCES 'Notedetail'('Note') ON DELETE CASCADE)");

       //insert information into database
        db.execSQL("INSERT INTO Users Values('test@bentley.edu','12345')");
        db.execSQL("INSERT INTO Users Values('echen@bentley.edu','123456')");

        db.execSQL("INSERT INTO Teacher Values('T01','Bill Schiano','SBill@bentley.edu','611-666-8888')");
        db.execSQL("INSERT INTO Teacher Values('T02','Wendy Lucas','WLUCAS@bentley.edu','611-666-8887')");
        db.execSQL("INSERT INTO Teacher Values('T03','James Pepe','JPEPE@bentley.edu','611-666-8886')");
        db.execSQL("INSERT INTO Teacher Values('T04','Heikki Topi','HTOPI@bentley.edu','611-666-8885')");
        db.execSQL("INSERT INTO Teacher Values('T05','Bill Schiano','BSCHIANO@bentley.edu','611-666-8884')");
        db.execSQL("INSERT INTO Teacher Values('T06','Jennifer Xu','JXU@bentley.edu','611-666-8881')");

        db.execSQL("INSERT INTO Courses Values('C01','T01','Enterprise Architecture')");
       db.execSQL("INSERT INTO Courses Values('C02','T02','Object-Oriented Application Develop')");
       db.execSQL("INSERT INTO Courses Values('C03','T03','Mobile Application Development')");
       db.execSQL("INSERT INTO Courses Values('C04','T04','System Analysis and Design')");
        db.execSQL("INSERT INTO Courses Values('C05','T05','Global IT Project Management')");
        db.execSQL("INSERT INTO Courses Values('C06','T06','Intro to Programing w/Python')");


        db.execSQL("INSERT INTO CoursesDetail Values('CS610-100','C01','JEN102','MonWed','3:00 PM-5:00 PM')");
        db.execSQL("INSERT INTO CoursesDetail Values('CS603-200','C02','SMI302','TueThu','5:00 PM-7:20 PM')");
        db.execSQL("INSERT INTO CoursesDetail Values('CS680-100','C03','SMI212','ThuFri','5:00 PM-7:20 PM')");
        db.execSQL("INSERT INTO CoursesDetail Values('CS630-300','C04','SMI212','MonThuFri','7:30 PM-9:50 PM')");
        db.execSQL("INSERT INTO CoursesDetail Values('CS620-100','C05','SMI211','TueWedThu','5:00 PM-7:20 PM')");
        db.execSQL("INSERT INTO CoursesDetail Values('CS299-100','C06','SMI307','SatSun','9:30 AM-10:50 AM')");

        db.execSQL("INSERT INTO Location Values('JEN102','42.388098','-71.220846')");
        db.execSQL("INSERT INTO Location Values('SMI302','42.387247','-71.220466')");
        db.execSQL("INSERT INTO Location Values('SMI307','42.387247','-71.220466')");
        db.execSQL("INSERT INTO Location Values('SMI212','42.387247','-71.220466')");
        db.execSQL("INSERT INTO Location Values('SMI211','42.387247','-71.220466')");

        db.execSQL("INSERT INTO Notedetail Values('Note1','Assignment1 due 04/13')");
        db.execSQL("INSERT INTO Notedetail Values('Note1','Assignment2 due 05/03')");
        db.execSQL("INSERT INTO Notedetail Values('Note1','Assignment3 due 05/13')");
        db.execSQL("INSERT INTO Notedetail Values('Note1','Assignment4 due 05/30')");
        db.execSQL("INSERT INTO Notedetail Values('Note2','Assignment4 due 05/15')");
        db.execSQL("INSERT INTO Notedetail Values('Note3','Assignment5 due 05/17')");
        db.execSQL("INSERT INTO Notedetail Values('Note4','Project due 05/20')");
        db.execSQL("INSERT INTO Notedetail Values('Note5','Paper4 due 05/19')");
        db.execSQL("INSERT INTO Notedetail Values('Note6','Assignment5 due 05/21')");

        db.execSQL("INSERT INTO Schedule Values('CS610-100','test@bentley.edu','Note1')");
        db.execSQL("INSERT INTO Schedule Values('CS603-200','echen@bentley.edu','Note2')");
        db.execSQL("INSERT INTO Schedule Values('CS680-100','test@bentley.edu','Note3')");
        db.execSQL("INSERT INTO Schedule Values('CS630-300','echen@bentley.edu','Note4')");
        db.execSQL("INSERT INTO Schedule Values('CS620-100','echen@bentley.edu','Note5')");
        db.execSQL("INSERT INTO Schedule Values('CS299-100','echen@bentley.edu','Note6')");
        db.execSQL("INSERT INTO Schedule Values('CS299-100','test@bentley.edu','Note7')");
        db.execSQL("INSERT INTO Schedule Values('CS620-100','test@bentley.edu','Note8')");


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*
                If the remember user checkbox is checked, a boolean value true will be returned. The user email address will be saved for
                future use.*/
                if(remember.isChecked()){
                    try {
                        out = new OutputStreamWriter(openFileOutput("account.txt", MODE_PRIVATE));

                        out.write(email.getText().toString());
                        out.close();
                    }catch (IOException e) {}
                }

                /*
Check if the password in the database match with the password that user entered. If not match, a toast will show up to state
that the password is wrong. If match, enter the loading page and enter the student menu page.
                 */
                account = email.getText().toString();
                cursor = db.rawQuery("SELECT Password FROM Users WHERE Email="+"\""+account+"\"",null);
                cursor.moveToFirst();
                str = cursor.getString(0);

                if(password.getText().toString().equals(str)){
                    openloading();
                    Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            openStudentmenu();
                        }
                    }, 5000);


                }
                else{
                    Toast.makeText(getApplicationContext(), "The Email Address and Password are not match", Toast.LENGTH_LONG).show();
                    password.setText("");
                }

            }
        });






    }
    /*
        Functions that use to opent the loading page and student menu page.
         */
    public void openStudentmenu(){

        Intent i = new Intent(this, StudentMenuActivity.class);
        startActivity(i);

    }

    public void openloading(){
        Intent i = new Intent(this,Animation.class);
        startActivity(i);

    }

}

