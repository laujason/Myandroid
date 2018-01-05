package com.ormedia.qrscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ormedia.qrscanner.Network.JSONResponse;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by rin on 5/1/18.
 */

public class inventory extends AppCompatActivity {

    private TextView txt_pdname;
    private TextView txt_pdcode;
    private TextView txt_pdgtin;
    private TextView txt_pdqty;
    private TextView txt_code;
    private TextView txt_sup;
    private TextView txt_actqyt;
    private Button btn_scan;
    private Button btn_logout;
    private Button btn_confirm;
    private Button btn_cancel;
    private String code;
    private String method;
    private Double unitprice;
    private Integer actqty;
    private ImageView img_title;
    private String postid;
    public static final String msg_code = "com.ormedia.qrscanner.code";
    public static final String msg_method = "com.ormedia.qrscanner.method";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        Intent intent = getIntent();
        code = intent.getStringExtra(home.msg_code).toString();
        method = intent.getStringExtra(home.msg_method).toString();
        Log.d("ORM","method= " + method);

        txt_pdname = findViewById(R.id.txt_pdname);
        txt_pdcode = findViewById(R.id.txt_pdcode);
        txt_pdgtin = findViewById(R.id.txt_pdgtin);
        txt_pdqty = findViewById(R.id.txt_pdqty);
        txt_code = findViewById(R.id.txt_code);
        txt_sup = findViewById(R.id.txt_sup);
        btn_scan = findViewById(R.id.btn_scan);
        btn_logout = findViewById(R.id.btn_logout);
        img_title = findViewById(R.id.img_title);
        txt_actqyt =findViewById(R.id.txt_actqty);
        //btn_search = findViewById(R.id.btn_serach);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_cancel = findViewById(R.id.btn_cancel);

        downLoadFromServer(code);

        if (method.equals("out".toString()) ){
            img_title.setImageResource(R.drawable.home_out);
            txt_sup.setText("使用量：");
        } else if (method.equals("in".toString())){
            img_title.setImageResource(R.drawable.home_in);
            txt_sup.setText("補充量：");
        }

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inventory();

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), home.class);
                intent.putExtra(msg_code, code);
                intent.putExtra(msg_method, "cancel");
                startActivity(intent);
                finish();
            }
        });

        txt_actqyt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (txt_actqyt.getText().length()>0){
                    actqty = Integer.parseInt(txt_actqyt.getText().toString());
                } else {
                    actqty = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {



            }
        });

    }

    public void downLoadFromServer(String GTIN){
        new JSONResponse(this, "http://35.198.210.107/scan?action=scan&code="+ encode(GTIN), new JSONResponse.onComplete() {
            @Override
            public void onComplete(JSONObject json) {
                Log.d("ORM",json.toString());
                try {
                    String GTIN = json.getString("code");
                    String productName = json.getString("productName");
                    String supplier = json.getString("supplierName");
                    String quantity = json.getString("quantity");
                    String price = json.getString("price");
                    String productID = json.getString("postid");
                    postid = productID;
                    while ((productID.length()<5)){
                        productID="0"+productID;
                    }
                    productName = supplier +  "\n" + productName ;
                    txt_pdname.setText(productName);
                    txt_pdgtin.setText(GTIN);
                    code = GTIN;
                    txt_pdqty.setText(quantity);
                    txt_pdcode.setText(productID);
                    try {
                        unitprice = Double.parseDouble(price);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "error getting price", Toast.LENGTH_SHORT).show();
                    }

                    //Toast.makeText(getApplicationContext(), "information aquired", Toast.LENGTH_SHORT).show();
                } catch(Exception e) {
                    Log.e("ORM","FullScreenActivity onActivity Result : "+e.toString());
                }
            }
        });
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

    public void inventory(){
        new JSONResponse(this, "http://35.198.210.107/moveinv?method="+ method + "&postid=" + postid + "&quantity=" + actqty, new JSONResponse.onComplete() {
            @Override
            public void onComplete(JSONObject json) {
                Log.d("ORM","http://35.198.210.107/moveinv?method="+ method + "&postid=" + postid + "&quantity=" + actqty);
                Log.d("ORM",json.toString());
                try {
                    String quantity = json.getString("quantity");
                    String error = json.getString("error");
                    String method = json.getString("method");
                    if (error=="false"){
                        Toast toast = Toast.makeText(getApplicationContext(), "庫存變更成功", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER|Gravity.BOTTOM, 0, 500);
                        toast.show();

                    }
                    Intent intent = new Intent(getApplicationContext(), home.class);
                    intent.putExtra(msg_code, code);
                    intent.putExtra(msg_method, method);
                    startActivity(intent);
                    finish();
                } catch(Exception e) {
                    Log.e("ORM","Inventory Activity onActivity Result : "+e.toString());
                }
            }
        });
    }


}
