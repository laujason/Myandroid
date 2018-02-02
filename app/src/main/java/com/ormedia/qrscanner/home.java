package com.ormedia.qrscanner;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URLEncoder;


public class home extends AppCompatActivity {

    private TextView txt_pdname;
    private TextView txt_pdcode;
    private TextView txt_pdgtin;
    private TextView txt_pdqty;
    private TextView txt_code;
    private TextView txt_exp;
    private TextView txt_lot;
    protected Button btn_new;
    public static String code;
    public static String oricode="";
    public static String method="";
    public static String rexp="";
    public static String rlot="";
    public static Integer userid;
    private Activity activity;
    private static final String LOG_TAG = FullscreenActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    protected static String[] result_name;
    protected static Integer[] result_id;
    protected static String[] result_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try {
            userid = login.userid;
            if (!oricode.equals("")){
                downLoadFromServer(oricode);
            } else if (!code.equals("")){
                downLoadFromServer(code);
            }
        } catch (Exception e){
            Log.e("ORM", e.toString());
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
        Button btn_scan = findViewById(R.id.btn_scan);
        Button btn_logout = findViewById(R.id.btn_logout);
        Button btn_hist = findViewById(R.id.btn_hist);
        Button btn_search = findViewById(R.id.btn_serach);
        Button btn_add = findViewById(R.id.btn_add);
        Button btn_sbtr = findViewById(R.id.btn_sbtr);
        btn_new = findViewById(R.id.btn_new);
        btn_scan.requestFocus();
        activity = this;
        getExp();
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
                oricode = code;
                Log.d("ORM",code);
                if (code.equals("adm")){
                    Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
                    startActivity(intent);
                } else {
                    downLoadFromServer(code);

                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
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

    private void show_results() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("搜尋結果:");

        builder.setItems(result_name, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("result code",result_code[which]);
                code = result_code[which];
                oricode = code;
                Log.d("filtered code",code);
                downLoadFromServer(code);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog search_result = builder.create();
        search_result.show();
    }

    @Override
    public void onBackPressed() {

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
                    StringBuilder productID = new StringBuilder(json.getString("postid"));
                    String lot = json.getString("lot");
                    String exp = json.getString("exp");


                    if (lot.equals("")){
                        lot = rlot;
                    }
                    if (exp.equals("")){
                        exp = rexp;
                    }


                    if (method.equals("pre")){
                        method = "new";
                        if (login.isadmin){
                            Intent intent = new Intent(getApplicationContext(), newpd.class);
                            startActivity(intent);
                        }
                    }
                    if (!json.getString("search").equals("")) {
                        if (json.getString("search").equals("no result") && !(method.equals("new")||method.equals("cancel"))) {
                            Toast.makeText(getApplicationContext(),"沒有搜尋結果",Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray data = json.getJSONArray("search");
                            Integer datalength = data.length();
                            if (datalength > 0) {
                                result_name = new String[datalength];
                                result_code = new String[datalength];
                                result_id = new Integer[datalength];
                                for (int i = 0; i < datalength; i++) {
                                    String data_string = data.getString(i);
                                    String[] result_array = data_string.split("\"");
                                    result_id[i] = Integer.valueOf(result_array[3]);
                                    result_name[i] = result_array[7];
                                    result_code[i] = result_array[11].replace("\\u001d","");
                                }
                                show_results();
                            }
                        }
                    }

                    if (!productName.equals("")) {
                        while ((productID.length()<5)){
                            productID.insert(0, "0");
                        }
                        productName = supplier + "\n" + productName;
                        txt_pdname.setText(productName);
                        txt_pdcode.setText(productID.toString());
                        txt_pdgtin.setText(GTIN.replace("\u001d",""));
                        Log.d("gtin text", txt_pdgtin.getText().toString());
                        txt_pdqty.setText(quantity);
                        txt_lot.setText(lot);
                        txt_exp.setText(exp);
                        code = GTIN;
                    } else {
                        if (!(method.equals("new") || method.equals("cancel") )) {
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
        String GTIN;
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    GTIN = barcode.displayValue;
                    oricode = GTIN;
                    downLoadFromServer(GTIN);
                } else txt_pdgtin.setText(R.string.no_barcode_captured);
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    public static String encode(String url) {
        try {
            return URLEncoder.encode( url, "UTF-8" );
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private  Boolean productex() {
        return !txt_pdname.getText().toString().equals("");
    }

    private void goinv (String method){
        Intent intent = new Intent(getApplicationContext(), inventory.class);
        home.method = method;

        if (oricode.equals("")){
            oricode = code;
        }
        getExp();
        startActivity(intent);
    }

    private void getExp(){
        if (!TextUtils.isEmpty(txt_exp.getText())){
            rexp = txt_exp.getText().toString();
        }
        if (!TextUtils.isEmpty(txt_lot.getText())){
            rlot = txt_lot.getText().toString();
        }

    }


}
