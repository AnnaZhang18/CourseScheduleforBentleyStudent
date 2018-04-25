package com.example.eleanor.coursescheduleforbentleystudent;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
/*
This activity will show today's all classes that the student will be having. From here, student can click the course to
enter the course detail activity to check more details. This page will show course's name, course number, classroom location
and the class time.
 */

public class StudentMenuActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ListView CourseNum;
    private ListView CourseLoc;
    private ListView CourseTime;
    ArrayList<String> CNcontent = new ArrayList<>();
    ArrayList<String> CLcontent = new ArrayList<>();
    ArrayList<String> CTcontent = new ArrayList<>();
    private Cursor cursor;
    ArrayAdapter<String> CNaa;
    ArrayAdapter<String> CLaa;
    ArrayAdapter<String> CTaa;
    public static String course;
    private String account;
    private NotificationManager mNotificationManager;
    private Notification notifyDetails;
    private int SIMPLE_NOTFICATION_ID;
    private String contentTitle = "Your Class Will Begin Soon";
    private String contentText = "Your Class Will Begin in 10min";
    private String tickerText = "New Alert, Click Me !!!";
    public static String date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_menu);
/*
Find today's date and time in desired format in order to find today's courses
 */
        TextView Today = findViewById(R.id.todayclass);

        Calendar sCalendar = Calendar.getInstance();
        String dayName = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY");
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
        date = df.format(Calendar.getInstance().getTime());
        Today.setText(Today.getText().toString()+"            "+date+"     "+dayName);
        dayName = dayName.trim().substring(0,3);
        Date now = new Date();
/*
   This activity also can show up notification as the class is approaching or there are any alerts updated based on the date
   and time set here.
    */
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent notifyIntent = new Intent(this, StudentMenuActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
/*
Create and config the notification.
 */
        notifyDetails =
                new Notification.Builder(this)
                        .setContentIntent(pendingIntent)

                        .setContentTitle(contentTitle)   //set Notification text and icon
                        .setContentText(contentText)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)

                        .setTicker(tickerText)            //set status bar text

                        .setWhen(System.currentTimeMillis())    //timestamp when event occurs

                        .setAutoCancel(true)     //cancel Notification after clicking on it

                        //set Android to vibrate when notified
                        .setVibrate(new long[]{1000, 1000, 1000, 1000})

                        // flash LED (color, on in millisec, off)
                        //doesn't work for all handsets
                        .setLights(Integer.MAX_VALUE, 500, 500)

                        .build();
/*
Define the course number, location and time created in layout
 */
        CourseNum = findViewById(R.id.todaylistcourse);
        CourseLoc = findViewById(R.id.todaylistlocation);
        CourseTime = findViewById(R.id.todaylisttime);
/*
Invoke the account information to determine which user login to the system, then invoke the database
to find all courses under this account, and then use the course's primary key to find all courses' day, time and location
in the database. By finding the day of the courses, to check if the date of the day is today's date. If so, keep this course and
list the course's time and location. If not, delete the course from the list.
Therefore, the courses that show up in this menu will only be today's courses and courses' information.
         */
        account = LogInActivity.account;
        SQLiteDatabase db = LogInActivity.db;
        cursor = db.rawQuery("SELECT CoursesSection FROM Schedule WHERE Email="+"\""+account+"\"",null);
        if(cursor.moveToFirst()) {
            Toast.makeText(this,"ABC",Toast.LENGTH_LONG);
            do {
                CNcontent.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        for(int i =0; i<CNcontent.size();i++) {
            cursor = db.rawQuery("SELECT Day, Time,LocationID FROM CoursesDetail WHERE CoursesSection=" + "\"" + CNcontent.get(i) + "\"", null);
            if(cursor.moveToFirst()) {
                String day = cursor.getString(0);
                if (day.trim().contains(dayName)) {
                    CTcontent.add(cursor.getString(1));
                    CLcontent.add(cursor.getString(2));
                } else {
                    CNcontent.remove(i);
                    i--;

                }
            }
        }


        CNaa = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,     //Android supplied List item format
                CNcontent);
        CourseNum.setAdapter(CNaa);

        CLaa = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,     //Android supplied List item format
                CLcontent);
        CourseLoc.setAdapter(CLaa);

        CTaa = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,     //Android supplied List item format
                CTcontent);
        CourseTime.setAdapter(CTaa);

        CourseNum.setOnItemClickListener(this);
        CourseLoc.setOnItemClickListener(this);
        CourseTime.setOnItemClickListener(this);

/*
Set up the condition that if the course will be start in 10 min, when the app is opened, the notification will show up to remind you that the
class is about to start.
 */

        try{
            now = tf.parse(tf.format(now));
            int sp = CTcontent.get(0).toString().trim().indexOf(' ');
            String classtime = CTcontent.get(0).toString().trim().substring(0,sp);
            if(CTcontent.get(0).toString().trim().substring(sp+1,sp+3).equals("PM")){
                int colon = classtime.indexOf(':');
                int hour = Integer.parseInt(classtime.substring(0,colon));
                hour=hour+12;
                classtime = hour+classtime.substring(colon);
            }
            Date classt = tf.parse(classtime);
            long diff = classt.getTime()-now.getTime();
            diff = diff/(1000*60);
            int intdiff = (int)diff;
            if(intdiff>0&&intdiff<10){
                mNotificationManager.notify(SIMPLE_NOTFICATION_ID,
                        notifyDetails);
            }
        }catch (ParseException e){}


/*
   Set up Menu option for "Switch User"
 */
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //When "Switch User" menu option has been selected, redirect user to the LoginActivity to sign-out


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.switchuser:
                Intent i1 = new Intent(this,LogInActivity.class);
                startActivity(i1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //set up onItemClick method
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        course=CNcontent.get(position);
        openCourseDetail();
    }
    //declare openCoursedetail method
    public void openCourseDetail(){
        Intent i = new Intent(this, CourseDetail.class);
        startActivity(i);
    }
}
