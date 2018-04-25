package com.example.eleanor.coursescheduleforbentleystudent;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AssignmentPage extends AppCompatActivity {
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    private TextToSpeech speaker;
    private int pos;
    private String noteid;
    private int result;
    private NotificationManager mNotificationManager;
    private Notification notifyDetails;
    private int SIMPLE_NOTFICATION_ID;
 /*
    This activity will show Assignment Detail page for selected course. All upcoming assignment list and due date will be
    listed here. Student can choose to listen(text to speech)the assignment list by click Listen button. The assignment list
    background color will also be changed according to the due date.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_page);



		  Intent notifyIntent = new Intent();
		  notifyIntent.setComponent(new ComponentName("com.course.example",
		                  "com.course.example.IOTest"));


        //set up ListView
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<String>();
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        String Courseid = StudentMenuActivity.course;
        String StudentEmail = LogInActivity.account;

        //connect Database
        SQLiteDatabase db = LogInActivity.db;
        String date = StudentMenuActivity.date;
        final int month = Integer.parseInt(date.trim().substring(0,2));
        final int day = Integer.parseInt(date.trim().substring(3,5));

        //get cursor from Database, insert into listview from cursor
        Cursor cursor = db.rawQuery("SELECT Note FROM Schedule WHERE CoursesSection=" + "\"" + Courseid +"\" and Email="+ "\"" + StudentEmail +"\"", null);
        cursor.moveToFirst();
        noteid=cursor.getString(0);
        cursor = db.rawQuery("SELECT Assignmentnote FROM Notedetail WHERE Note=" + "\"" + noteid + "\"", null);
        cursor.moveToFirst();
        do {

            items.add(cursor.getString(0));

        } while (cursor.moveToNext());

        //Get the due date month and day from the database, and set up highlighted notification for assignment with
        // due date within a month
        itemsAdapter = new ArrayAdapter<String>(this,
                R.layout.listview_text, R.id.list_content, items){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                String assignment = items.get(position);
                String duedate = assignment.trim().substring(assignment.trim().length()-5);

                int duemonth = Integer.parseInt(duedate.substring(0,2));
                int dueday = Integer.parseInt(duedate.substring(3));

                View v = super.getView(position,convertView,parent);
//                int difference = dueday-day;
                if(duemonth<month || (duemonth==month && dueday<day)) {
                    v.setBackgroundColor(Color.parseColor("#aaaaaa"));
                }
                else if((duemonth==month && dueday>=day) || (duemonth-month==1 && dueday<day)){
                    v.setBackgroundColor(Color.parseColor("#FF0000"));
                }
                else{
                    v.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                return v;
            }
        };
        lvItems.setAdapter(itemsAdapter);


        //set speaker
        speaker=new TextToSpeech(AssignmentPage.this,new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    result = speaker.setLanguage(Locale.US);
                } else {
                    Toast.makeText(getApplicationContext(),"Feature not supported in your device",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    //set up "listen" menu option
    public boolean onCreateOptionsMenu(Menu menu1) {
        getMenuInflater().inflate(R.menu.menu1, menu1);
        return true;
    }
    @Override

    //speak assignment list when "listen" button clicked
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.speaker:
                String speech=" ";
                for(int i=0; i<items.size();i++){

                    speech=speech+" "+items.get(i);

                }
                speak(speech);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void speak(String output){
        speaker.speak(output, TextToSpeech.QUEUE_FLUSH, null, "Id 0");
    }

}
