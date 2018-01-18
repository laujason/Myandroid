package com.ormedia.qrscanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.ormedia.qrscanner.Network.JSONResponse;
import com.ormedia.qrscanner.barcode.BarcodeCaptureActivity;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by panleung on 27/12/2017.
 */

public class home extends AppCompatActivity {

    private TextView txt_pdname;
    private TextView txt_pdcode;
    private TextView txt_pdgtin;
    private TextView txt_pdqty;
    private TextView txt_code;
    private TextView txt_exp;
    private TextView txt_lot;
    private Button btn_search;
    private Button btn_scan;
    private Button btn_logout;
    private Button btn_hist;
    private Button btn_add;
    private Button btn_sbtr;
    private Button btn_new;
    public static String code;
    public static String oricode="";
    public static String method="";
    private int userid;
    private static final String LOG_TAG = FullscreenActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try {
            userid = login.userid;
            Log.d("ORM","userid= " + userid);

            Log.d("ORM","method= " + method);
            if (oricode!=""){
                downLoadFromServer(oricode);
            } else if (code!=""){
                downLoadFromServer(code);
            }
        } catch (Exception e){

        }

        //TextView textView = findViewById(R.id.textView4);
        //textView.setText("login user: "+message);

        txt_pdname = findViewById(R.id.txt_pdname);
        txt_pdcode = findViewById(R.id.txt_pdcode);
        txt_pdgtin = findViewById(R.id.txt_pdgtin);
        txt_pdqty = findViewById(R.id.txt_pdqty);
        txt_code = findViewById(R.id.txt_code);
        txt_exp = findViewById(R.id.txt_exp);
        txt_lot = findViewById(R.id.txt_lot);
        btn_scan = findViewById(R.id.btn_scan);
        btn_logout = findViewById(R.id.btn_logout);
        btn_hist = findViewById(R.id.btn_hist);
        btn_search = findViewById(R.id.btn_serach);
        btn_add = findViewById(R.id.btn_add);
        btn_sbtr = findViewById(R.id.btn_sbtr);
        btn_new = findViewById(R.id.btn_new);
        btn_scan.requestFocus();

        if (!login.isadmin){
            btn_new.setVisibility(View.INVISIBLE);
        }

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code = txt_code.getText().toString();
                Log.d("ORM",code);
                if (code.equals("adm")){
                    Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
                    startActivity(intent);
                } else {
                    downLoadFromServer(code);

                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });

        btn_hist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), history.class);
                startActivity(intent);
            }
        });


        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productex()){
                    goinv("in");
                } else {
                    Toast.makeText(getApplicationContext(),"請先掃描產品", Toast.LENGTH_LONG).show();
                }

            }
        });

        btn_sbtr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productex()) {
                    goinv("out");
                } else {
                    Toast.makeText(getApplicationContext(),"請先掃描產品", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
                method  = "pre";
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        /*
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        */

    }
    @Override
    public void onBackPressed() {
        //Toast.makeText(getApplicationContext(), "cannot go back", Toast.LENGTH_SHORT).show();
    }


    public void downLoadFromServer(final String GTIN){
        new JSONResponse(this, "http://35.198.210.107/scan?action=scan&code="+ encode(GTIN), new JSONResponse.onComplete() {
            @Override
            public void onComplete(JSONObject json) {
                Log.d("ORM",json.toString());
                Log.d("ORM","http://35.198.210.107/scan?action=scan&code="+ encode(GTIN));
                try {
                    String GTIN = json.getString("code");
                    String productName = json.getString("productName");
                    String supplier = json.getString("supplierName");
                    String quantity = json.getString("quantity");
                    String productID = json.getString("postid");
                    String lot = json.getString("lot");
                    String exp = json.getString("exp");


                    if (method.equals("pre".toString())){
                        method = "new";
                        if (login.isadmin){
                            Intent intent = new Intent(getApplicationContext(), newpd.class);
                            startActivity(intent);
                        }
                    }

                    if (!productName.equals("".toString())) {
                        while ((productID.length()<5)){
                            productID="0"+productID;
                        }
                        productName = supplier + "\n" + productName;
                        txt_pdname.setText(productName);
                        txt_pdcode.setText(productID);
                        txt_pdgtin.setText(GTIN);
                        txt_pdqty.setText(quantity);
                        txt_lot.setText(lot);
                        txt_exp.setText(exp);
                        code = GTIN;
                    } else {
                        if (!method.equals("new".toString())) {
                            Toast.makeText(getApplicationContext(), "物品不存在", Toast.LENGTH_SHORT).show();
                        }
                    }

                    //txt_pdqty.setText(quantity);
                    //Toast.makeText(getApplicationContext(), "1234 aquired", Toast.LENGTH_SHORT).show();
                } catch(Exception e) {
                    Log.e("ORM","FullScreenActivity onActivity Result : "+e.toString());

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String GTIN = "";
        String checkString="";
        //String longCode;
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    GTIN = barcode.displayValue;
                    oricode = GTIN;
                    downLoadFromServer(GTIN);
                } else txt_pdgtin.setText(R.string.no_barcode_captured);
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    public static String decode(String url) {
        try {
            String prevURL = "";
            String decodeURL = url;
            while(!prevURL.equals(decodeURL)) {
                prevURL = decodeURL;
                decodeURL = URLDecoder.decode( decodeURL, "UTF-8" );
            }
            return decodeURL;
        } catch (UnsupportedEncodingException e) {
            return "Error: " + e.getMessage();
        }
    }

    public static String encode(String url) {
        try {
            String encodeURL = URLEncoder.encode( url, "UTF-8" );
            return encodeURL;
        } catch (UnsupportedEncodingException e) {
            return "Error: " + e.getMessage();
        }
    }

    private  Boolean productex() {
        if (txt_pdname.getText().toString()!=""){
            return true;
        } else {
            return false;
        }
    }

    private void goinv (String method){
        Intent intent = new Intent(getApplicationContext(), inventory.class);
        home.method = method;

        if (oricode ==""){
            oricode = code;
        }

        startActivity(intent);
    }


}
