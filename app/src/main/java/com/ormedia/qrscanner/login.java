package com.ormedia.qrscanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.ormedia.qrscanner.Network.JSONResponse;

import org.json.JSONObject;

/**
 * Created by panleung on 20/12/2017.
 */

public class login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        login();
    }
    public void login() {
        new JSONResponse(this, "http://35.198.210.107/?action=login", new JSONResponse.onComplete() {
            @Override
            public void onComplete(JSONObject json) {
                Log.d("ORM", json.toString());
                try {
                    String return_data = json.getString("data");

                    Toast.makeText(getApplicationContext(), return_data, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("ORM", "login onActivity Result : " + e.toString());
                }
            }
        });

    }
}


