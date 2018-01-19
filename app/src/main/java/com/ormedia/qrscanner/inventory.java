package com.ormedia.qrscanner;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ormedia.qrscanner.Network.JSONResponse;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static java.lang.Math.round;


/**
 * Created by rin on 5/1/18.
 */

public class inventory extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView txt_pdname;
    private TextView txt_pdcode;
    private TextView txt_pdgtin;
    private TextView txt_pdqty;
    private TextView txt_sup;
    private TextView txt_actqyt;
    private TextView txt_remark;
    private TextView txt_exp;
    private TextView txt_lot;
    private TextView txt_inv;
    private TextView txt_ttp;
    private TextView txt_unp;
    private TextView txt_gift;
    private Button btn_confirm;
    private Button btn_cancel;

    private String code;
    private String method;
    private String oricode;
    private String exp;
    private String lot;
    private String inv;
    private int userid;
    private String reason;
    private String postid;
    private String action;
    private Double ttp;
    private Double unp;
    private Integer actqty = 0;
    private Integer giftqty = 0;
    private ImageView img_title;
    private AlertDialog dlg_confirm;
    private Spinner spinner;
    private Activity activity;
    public static final String msg_code = "com.ormedia.qrscanner.code";
    public static final String msg_method = "com.ormedia.qrscanner.method";
    public static final String msg_oricode = "com.ormedia.qrscanner.oricode";
    public static final String msg_userid = "com.ormedia.qrscanner.userid";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);


        try {
            method = home.method;
            code = home.code;
            userid = login.userid;
            oricode = home.oricode;
            Log.d("exp: ",exp);
            if (oricode.equals("".toString())){
                oricode = code;
            }
        } catch (Exception e){

        }

        txt_pdname = findViewById(R.id.txt_pdname);
        txt_pdcode = findViewById(R.id.txt_pdcode);
        txt_pdgtin = findViewById(R.id.txt_pdgtin);
        txt_pdqty = findViewById(R.id.txt_pdqty);
        txt_sup = findViewById(R.id.txt_sup);
        txt_exp = findViewById(R.id.txt_exp);
        txt_lot = findViewById(R.id.txt_lot);
        txt_inv = findViewById(R.id.txt_inv);
        txt_ttp = findViewById(R.id.txt_ttp);
        txt_unp = findViewById(R.id.txt_unp);
        txt_remark = findViewById(R.id.txt_remark);
        txt_actqyt =findViewById(R.id.txt_actqty);
        txt_gift =findViewById(R.id.txt_gift);
        img_title = findViewById(R.id.img_title);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_cancel = findViewById(R.id.btn_cancel);
        RelativeLayout rl_gift = findViewById(R.id.rl_gift);
        RelativeLayout rl_price = findViewById(R.id.rl_price);
        RelativeLayout rl_inv = findViewById(R.id.rl_inv);
        RelativeLayout rl_reason = findViewById(R.id.rl_reason);


        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        activity = this;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.consume_reason, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);




        downLoadFromServer(oricode);
        if (method.equals("out".toString()) ){
            img_title.setImageResource(R.drawable.home_out);
            txt_sup.setText("使用量：");
            action = "消耗";
            rl_gift.setVisibility(View.INVISIBLE);
            rl_price.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            rl_price.setVisibility(View.INVISIBLE);
            rl_inv.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            rl_inv.setVisibility(View.INVISIBLE);
        } else if (method.equals("in".toString())){
            img_title.setImageResource(R.drawable.home_in);
            txt_sup.setText("購買量：");
            action = "補充";
            rl_reason.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            rl_reason.setVisibility(View.INVISIBLE);

        }




        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkForm()){
                    confirmMove();
                }
            }
        });



        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), home.class);
                home.method = "cancel";
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
                try{
                    actqty = Integer.parseInt(txt_actqyt.getText().toString());
                } catch (Exception e) {
                    actqty = 0;
                }
                update_unp();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        txt_gift.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    giftqty = Integer.parseInt(txt_gift.getText().toString());
                } catch (Exception e) {
                    giftqty = 0;
                }
                update_unp();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        txt_ttp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    ttp = Double.valueOf(txt_ttp.getText().toString());

                } catch (Exception e) {
                    ttp = Double.valueOf(0);
                }
                update_unp();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



    }

    private boolean checkForm() {
        boolean cansubmit;
        txt_ttp.setText(String.format("%.2f", ttp));
        cansubmit = !(TextUtils.isEmpty(txt_actqyt.getText()) ||
                    TextUtils.isEmpty(txt_exp.getText()) ||
                    TextUtils.isEmpty(txt_lot.getText()) ||
                    //TextUtils.isEmpty(txt_inv.getText())  ||
                    //TextUtils.isEmpty(txt_ttp.getText()) && method == "out")
                (method.equals("in".toString()) && TextUtils.isEmpty(txt_inv.getText()))  ||
                (method.equals("in".toString()) && TextUtils.isEmpty(txt_ttp.getText())));
        Log.d("ORM",Boolean.toString(cansubmit));
        if (!cansubmit){
            Toast.makeText(getApplicationContext(), "請填入所有項", Toast.LENGTH_SHORT).show();
        }

        return cansubmit    ;
    }

    public void confirmMove(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        Log.d("ORM", getApplicationContext().toString());
        builder.setTitle("確認庫存變更");
        Integer total = actqty + giftqty;
        builder.setMessage("你確定要"+ action + total.toString()+"件嗎？");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                inventory();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        dlg_confirm = builder.create();
        dlg_confirm.show();
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
                    String lot = json.getString("lot");
                    String exp = json.getString("exp");
                    String productID = json.getString("postid");

                    postid = productID;
                    while ((productID.length()<5)){
                        productID="0"+productID;
                    }
                    productName = supplier +  "\n" + productName ;
                    txt_pdname.setText(productName);
                    txt_pdgtin.setText(GTIN);
                    if (exp.equals("".toString())){
                        txt_exp.setText(home.rexp);
                    } else {
                        txt_exp.setText(exp);
                    }
                    if (lot.equals("".toString())){
                        txt_lot.setText(home.rlot);
                    } else {
                        txt_lot.setText(lot);
                    }

                    code = GTIN;
                    txt_pdqty.setText(quantity);
                    txt_pdcode.setText(productID);
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
        new JSONResponse(this, inv_url(), new JSONResponse.onComplete() {
            @Override
            public void onComplete(JSONObject json) {
                Log.d("ORM",json.toString());
                try {
                    //String quantity = json.getString("quantity");
                    String error = json.getString("error");
                    String method = json.getString("method");
                    home.rexp = json.getString("exp");
                    home.rlot = json.getString("lot");
                    if (error=="false"){
                        Toast toast = Toast.makeText(getApplicationContext(), "庫存變更成功", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER|Gravity.BOTTOM, 0, 500);
                        toast.show();
                        Intent intent = new Intent(getApplicationContext(), home.class);
                        startActivity(intent);
                        finish();

                    } else {
                        String errormsg = json.getString("msg");
                        Toast.makeText(getApplicationContext(),errormsg,Toast.LENGTH_LONG).show();
                    }

                } catch(Exception e) {
                    Log.e("ORM","Inventory Activity onActivity Result : "+e.toString());
                }
            }
        });
    }

    public String inv_url(){
        exp = txt_exp.getText().toString();
        lot = txt_lot.getText().toString();
        inv = txt_inv.getText().toString();
        //ttp = txt_ttp.getText().toString();
        if (method.equals("in".toString())){
            reason = "";
        }
        String remark = txt_remark.getText().toString();
        String url = "http://35.198.210.107/inventory?method=" + method +
                "&postid=" + postid + "&quantity=" + actqty +
                "&giftqty=" + giftqty +"&remark=" + reason + remark +
                "&exp=" + exp + "&lot=" + lot +
                "&inv=" + inv + "&ttp=" + ttp +
                "&userid=" + userid;
        Log.d("ORM",url);
        return (url);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        reason = "rsn_" + spinner.getSelectedItem().toString() + "cmt_";
        Log.d("ORM", "reason= " + reason);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        reason = "";
        Log.d("ORM", "reason= " + reason);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void update_unp(){
        try {
            ttp = Double.valueOf(txt_ttp.getText().toString());
        } catch (Exception e){
            ttp = Double.valueOf(0);
        }
        try {
            unp = ttp / (actqty + giftqty);
        } catch (Exception e){
            unp = Double.valueOf(0);
        }
        if (unp.isNaN() || unp.isInfinite()){
            unp = Double.valueOf(0);
        }
        txt_unp.setText("單價: " + String.format("%.2f", unp));

    }
}
