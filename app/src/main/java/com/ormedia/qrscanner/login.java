package com.ormedia.qrscanner;

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

    private TextView txt_phone;
    private TextView txt_psw;
    private TextView txt_disclaimer;
    private CheckBox checkBox;
    private Button btn_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);
        txt_phone = findViewById(R.id.txt_phone);
        txt_psw = findViewById(R.id.txt_psw);
        txt_disclaimer = findViewById(R.id.txt_disclaimer);
        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    String str_phone = txt_phone.getText().toString();
                    Log.d("ORM", str_phone);

                    String str_psw = txt_psw.getText().toString();
                    Log.d("ORM", str_psw);


                login(str_phone, str_psw);
            }

        });

    }

    public void login(String str_phone, String str_psw) {
        Log.d("ORM","phone = " + str_phone);
        Log.d("ORM","psw = " + str_psw);
        Log.d("ORM","http://35.198.210.107/applogin/?action=login&login_phone="+str_phone+"&login_psw="+str_psw);
        new JSONResponse(this, "http://35.198.210.107/applogin/?action=login&login_phone="+str_phone+"&login_psw="+str_psw, new JSONResponse.onComplete() {
            @Override
            public void onComplete(JSONObject json) {
                Log.d("ORM", json.toString());
                try {
                    String return_data = json.getString("userid");
                    Toast.makeText(getApplicationContext(), "user id = "+return_data, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("ORM", "login onActivity Result : " + e.toString());
                }
            }
        });

    }
}


