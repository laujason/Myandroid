package com.ormedia.qrscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by panleung on 27/12/2017.
 */

public class home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        String message = intent.getStringExtra(login.msg_userid);
        //TextView textView = findViewById(R.id.textView4);
        //textView.setText("login user: "+message);
    }
    @Override
    public void onBackPressed() {
        //Toast.makeText(getApplicationContext(), "cannot go back", Toast.LENGTH_SHORT).show();
    }

}
