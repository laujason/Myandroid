package com.ormedia.qrscanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import com.ormedia.qrscanner.Network.*;
import org.json.JSONObject;


/**
 * Created by panleung on 20/12/2017.
 */

public class login extends AppCompatActivity {
    public static int userid = 0;
    public static Boolean isadmin;
    private TextView txt_phone;
    private TextView txt_psw;
    private TextView txt_disclaimer;
    private TextView txt_error;
    private CheckBox checkBox;
    private Button btn_login;
    private boolean debug = false;
    private CheckBox cb_read;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);
        txt_phone = findViewById(R.id.txt_phone);
        txt_psw = findViewById(R.id.txt_psw);
        txt_error = findViewById(R.id.txt_error);
        txt_disclaimer = findViewById(R.id.txt_disclaimer);
        btn_login = findViewById(R.id.btn_login);
        cb_read =  findViewById(R.id.cb_read);
        cb_read.setChecked(debug);

        txt_disclaimer.setKeyListener(null);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str_phone = txt_phone.getText().toString();
                Log.d("ORM", str_phone);

                String str_psw = txt_psw.getText().toString();
                Log.d("ORM", str_psw);

                if (cb_read.isChecked()){
                    applogin(str_phone, str_psw);

                } else{
                    Toast.makeText(getApplicationContext(), "請先同意免責聲明", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    @Override
    public void onBackPressed() {
        //Toast.makeText(getApplicationContext(), "cannot go back", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (debug){
            Intent intent = new Intent(getApplicationContext(), home.class);
            userid = 10;
            isadmin = true;
            //intent.putExtra(msg_userid, message);
            startActivity(intent);
            finish();
        }
    }

    public void applogin(String str_phone, String str_psw) {
        Log.d("ORM","phone = " + str_phone);
        Log.d("ORM","psw = " + str_psw);
        Log.d("ORM","http://35.198.210.107/applogin/?action=login&login_phone="+str_phone+"&login_psw="+str_psw);
        new JSONResponse(this, "http://35.198.210.107/applogin/?action=login&login_phone="+str_phone+"&login_psw="+str_psw, new JSONResponse.onComplete() {
            @Override
            public void onComplete(JSONObject json) {
                Log.d("ORM", json.toString());
                try {
                    userid = Integer.parseInt(json.getString("userid"));
                    String str_error = json.getString("error");
                    String isadmin = json.getString("admin");
                    if (isadmin.equals("true".toString())){
                        login.isadmin = true;
                    } else {
                        login.isadmin = false;
                    }
                    Log.d("ORM", str_error);
                    if (str_error =="true"){
                        Log.d("ORM", "return error");
                    } else if (str_error =="false" || debug){
                        txt_error.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(getApplicationContext(), home.class);
                        //intent.putExtra(msg_userid, str_userid);
                        startActivity(intent);
                        finish();

                    } else {
                        txt_error.setVisibility(View.VISIBLE);
                    }

                    //Toast.makeText(getApplicationContext(), "user id = "+return_data, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("ORM", "login onActivity Result : " + e.toString());
                }
            }
        });

    }
}


