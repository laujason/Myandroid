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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ormedia.qrscanner.Network.JSONResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by rin on 5/1/18.
 */

public class newpd extends AppCompatActivity {

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
    private TextView txt_suppid;
    private Button btn_confirm;
    private Button btn_cancel;
    private String code;
    private String method;
    private String oricode;
    private String exp;
    private String lot;
    private String inv;
    private int userid;
    private String postid;
    private String action;
    private Double ttp;
    private Double unp;
    private Integer actqty = 0;
    private Integer giftqty = 0;
    private AlertDialog dlg_confirm;
    private Activity activity;
    private AutoCompleteTextView txt_supplier;
    public static final String msg_code = "com.ormedia.qrscanner.code";
    public static final String msg_method = "com.ormedia.qrscanner.method";
    public static final String msg_oricode = "com.ormedia.qrscanner.oricode";
    public static final String msg_userid = "com.ormedia.qrscanner.userid";

    private static String[] suppliers = new String[200];
    private static String[] custom_id = new String[200];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        get_supplier();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, suppliers);
        txt_supplier = (AutoCompleteTextView) findViewById(R.id.txt_supplier);
        txt_supplier.setAdapter(adapter);

        method = "new";
        try {
            userid = login.userid;
            oricode = home.oricode;
            if (oricode.equals("".toString())){
                Toast.makeText(getApplicationContext(),"獲取產品編碼失敗，請手動輸入。",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){

        }

        txt_pdname = findViewById(R.id.txt_pdname);
        txt_pdcode = findViewById(R.id.txt_pdcode);
        txt_pdgtin = findViewById(R.id.txt_pdgtin);
        txt_sup = findViewById(R.id.txt_sup);
        txt_exp = findViewById(R.id.txt_exp);
        txt_lot = findViewById(R.id.txt_lot);
        txt_inv = findViewById(R.id.txt_inv);
        txt_ttp = findViewById(R.id.txt_ttp);
        txt_unp = findViewById(R.id.txt_unp);
        txt_remark = findViewById(R.id.txt_remark);
        txt_actqyt = findViewById(R.id.txt_actqty);
        txt_gift = findViewById(R.id.txt_gift);
        txt_suppid = findViewById(R.id.txt_suppid);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_cancel = findViewById(R.id.btn_cancel);


        activity = this;

        downLoadFromServer(oricode);

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

        txt_supplier.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    String suppliertext = txt_supplier.getText().toString();
                    if (!suppliertext.equals("".toString())) {
                        int customid = Arrays.asList(suppliers).indexOf(suppliertext)+1;
                        if (customid > 0) {
                            txt_suppid.setText(String.valueOf(customid));
                        } else {
                            txt_suppid.setText("");
                        }
                    }
                } catch (Exception e) {
                    actqty = 0;
                }
                update_unp();
            }

            @Override
            public void afterTextChanged(Editable editable) {
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
                    TextUtils.isEmpty(txt_inv.getText()) ||
                    TextUtils.isEmpty(txt_ttp.getText()) ||
                    TextUtils.isEmpty(txt_supplier.getText())
        );
        Log.d("ORM",Boolean.toString(cansubmit));
        if (!cansubmit){
            Toast.makeText(getApplicationContext(), "請填入所有項", Toast.LENGTH_SHORT).show();
        }
        return cansubmit;
    }

    public void confirmMove(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        Log.d("ORM", getApplicationContext().toString());
        builder.setTitle("確認增加新產品");
        Integer total = actqty + giftqty;
        builder.setMessage(action + total.toString()+"件嗎？");
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

                    if (!productName.equals("".toString())){
                        action = "你確定要修改產品資料並補充";
                        Toast.makeText(getApplicationContext(),"產品已經存在，繼續修改將覆蓋資料！",Toast.LENGTH_LONG).show();
                    } else {
                        action = "你確定要新增產品並補充";
                    }
                    String supplier = json.getString("supplierName");
                    String quantity = json.getString("quantity");
                    String lot = json.getString("lot");
                    String exp = json.getString("exp");
                    String productID = json.getString("postid");
                    postid = productID;
                    while ((productID.length()<5)){
                        productID="0"+productID;
                    }
                    txt_supplier.setText(supplier);
                    txt_pdname.setText(productName);
                    txt_pdgtin.setText(GTIN);
                    txt_lot.setText(lot);
                    txt_exp.setText(exp);
                    code = GTIN;
                    txt_pdcode.setText(productID);

                } catch(Exception e) {
                    Log.e("ORM","FullScreenActivity onActivity Result : "+e.toString());
                }
            }
        });
    }

    public void get_supplier(){
        new JSONResponse(this, "http://35.198.210.107/supplier?action=get", new JSONResponse.onComplete() {
            @Override
            public void onComplete(JSONObject json) {
                Log.d("ORM",json.toString());
                try {
                    JSONArray data = json.getJSONArray("sup_list");
                    ArrayList<String> sup_list = new ArrayList<String>();
                    for (int i=0; i<200; i++){
                        suppliers[i] = "";
                        custom_id[i] = "";
                    }
                    for (int i=0;i<data.length();i++){
                        String data_string = data.getString(i);
                        String[] sup_array = data_string.split("\"");
                        suppliers[i] = sup_array[7].toString();
                        custom_id[i] = sup_array[11].toString();
                    }
                } catch (Exception e){
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
        String supplierid = txt_suppid.getText().toString();
        String supplier = txt_supplier.getText().toString();
        String pdname = encode(txt_pdname.getText().toString());
        oricode = txt_pdgtin.getText().toString();
        String remark = encode(txt_remark.getText().toString());

        String url = "http://35.198.210.107/inventory?method=" + method +
                "&code=" + oricode + "&productName=" + pdname +
                "&postid=" + postid + "&quantity=" + actqty +
                "&giftqty=" + giftqty +"&remark=" + remark +
                "&exp=" + exp + "&lot=" + lot + "&supplierName=" + supplier +
                "&inv=" + inv + "&ttp=" + ttp + "&custom_id=" + supplierid +
                "&userid=" + userid;
        Log.d("ORM",url);
        return (url);
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
