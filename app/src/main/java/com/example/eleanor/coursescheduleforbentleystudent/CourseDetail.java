package com.example.eleanor.coursescheduleforbentleystudent;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CourseDetail extends AppCompatActivity {

    private TextView course;
    private TextView loc;
    private TextView time;
    private TextView assignment;
    private TextView instructor;
    private Button assignmentbtn;
    private Button Map;
    private Button Dial;
    private Button Emailprof;
    String email;
    String phone;
    String coordinator;
    /*
    This activity use to display the course detail after the user select the course in the student menu after login.
    This activity includes many functions.
    1. Use google map to provide location of the course selected by user. The map can locate the building.
    2. The student an dial to professor by clicking one button.
    3. The student can link their email (outlook, or gamil) to use the email function and send email to professor.
    4. The student can click a button to enter the assignment page to look up their recently due assingments, project or meetings.
    All these information will be stored in the database.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        /*
Find following buttons defined in layout
 */
        Map = (Button) findViewById(R.id.map);

        Dial = (Button) findViewById(R.id.dial);

        Emailprof = (Button) findViewById(R.id.emailprof);

        assignmentbtn = (Button) findViewById(R.id.assignment);

/*
Invoking the SQLiteDatabase from the login activity and the course ID from student menu activity, and the student email
from the login activity. In addition, define the textviews for future use.
 */
        SQLiteDatabase db = LogInActivity.db;
        String Courseid = StudentMenuActivity.course;
        String StudentEmail = LogInActivity.account;
        course = findViewById(R.id.classdetail);
        loc = findViewById(R.id.location);
        time = findViewById(R.id.texttime);
        assignment = findViewById(R.id.assignmentinfo);
        instructor = findViewById(R.id.instructorinfo);
        assignment.setText("");
        /*
Based on the courses listed in the student menu, read the course location, instructor, email, phone, assignment from
the database.
         */
        Cursor cursor = db.rawQuery("SELECT Day,Time,LocationID,CourseID FROM CoursesDetail WHERE CoursesSection=" + "\"" + Courseid + "\"", null);
        cursor.moveToFirst();
        time.setText(time.getText().toString() + cursor.getString(0) + " " + cursor.getString(1));
        loc.setText(loc.getText().toString() + cursor.getString(2));
        coordinator=cursor.getString(2);
        cursor = db.rawQuery("SELECT Coursename,TeacherID FROM Courses WHERE CourseID=" + "\"" + cursor.getString(3) + "\"", null);
        cursor.moveToFirst();
        course.setText(Courseid + "     " + cursor.getString(0));
        cursor = db.rawQuery("SELECT Name,TeacherEmail,Phone FROM Teacher WHERE TeacherID=" + "\"" + cursor.getString(1) + "\"", null);
        cursor.moveToFirst();
        instructor.setText(cursor.getString(0) + "\n" + cursor.getString(1)+"\n"+cursor.getString(2));
        email = cursor.getString(1);
        phone=cursor.getString(2);
        cursor = db.rawQuery("SELECT Note FROM Schedule WHERE CoursesSection=" + "\"" + Courseid +"\" and Email="+ "\"" + StudentEmail +"\"", null);
        cursor.moveToFirst();
        cursor = db.rawQuery("SELECT Assignmentnote FROM Notedetail WHERE Note=" + "\"" + cursor.getString(0) + "\"", null);
        cursor.moveToFirst();
        //assignment.setText(cursor.getString(0));
        cursor = db.rawQuery("SELECT lat,longt FROM Location WHERE LocationID=" + "\"" + coordinator + "\"", null);
        cursor.moveToFirst();
        coordinator = "geo:"+cursor.getString(0)+","+cursor.getString(1)+"?z=18";
        assignmentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAssignment();
            }
        });
        Emailprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openemail();
            }
        });
        Dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendial();
            }
        });
        Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openmap();
            }
        });


            /*
    Upcoming Assignment
     */
        String date = StudentMenuActivity.date;
        final int month = Integer.parseInt(date.trim().substring(0,2));
        final int day = Integer.parseInt(date.trim().substring(3,5));
        cursor = db.rawQuery("SELECT Note FROM Schedule WHERE CoursesSection=" + "\"" + Courseid +"\" and Email="+ "\"" + StudentEmail +"\"", null);
        cursor.moveToFirst();
        String noteid=cursor.getString(0);
        cursor = db.rawQuery("SELECT Assignmentnote FROM Notedetail WHERE Note=" + "\"" + noteid + "\"", null);
        cursor.moveToFirst();
        do {
            int i=0;
            String assignmentstr = cursor.getString(i);
            String duedate = assignmentstr.trim().substring(assignmentstr.trim().length() - 5);
            int duemonth = Integer.parseInt(duedate.substring(0, 2));
            int dueday = Integer.parseInt(duedate.substring(3));
            if((duemonth==month && dueday>=day) || (duemonth-month==1 && dueday<day)){
                assignment.setText(assignment.getText()+assignmentstr+"\n");
            }
        }while(cursor.moveToNext());
    }



/*
Open the assignment detail page.
 */

    public void openAssignment(){
        Intent i4 = new Intent(this, AssignmentPage.class);
        startActivity(i4);
    }
/*
Open the email intent.
 */

    public void openemail(){
        Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
        i.putExtra(Intent.EXTRA_EMAIL, email);
        i.setType("message/rfc822");
        startActivity(i);
    }

    /*
   Open the dial intent.
    */
    public void opendial(){
        Uri uri2 = Uri.parse("tel:"+phone);
        Intent i2 = new Intent(Intent.ACTION_DIAL, uri2);
        startActivity(i2);
    }

    /*
Open the map intent.
 */
    public void openmap(){
        Uri uri3 = Uri.parse(coordinator);
        Intent i3 = new Intent(Intent.ACTION_VIEW, uri3);
        if (i3.resolveActivity(getPackageManager()) != null) {
            startActivity(i3);
        }
    }
}
