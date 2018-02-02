package com.ormedia.qrscanner;

import android.annotation.SuppressLint;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.ormedia.qrscanner.Network.JSONResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import static java.lang.String.*;


/**
 * Created by rin on 5/1/18.
 */

public class newpd extends AppCompatActivity {

    private TextView txt_pdname;
    private TextView txt_pdcode;
    private TextView txt_pdgtin;
    private TextView txt_actqyt;
    private TextView txt_remark;
    private TextView txt_exp;
    private TextView txt_lot;
    private TextView txt_inv;
    private TextView txt_ttp;
    private TextView txt_unp;
    private TextView txt_gift;
    private TextView txt_suppid;
    private String code;
    private String method;
    private String oricode;
    private int userid;
    private String postid;
    private String action;
    private Double ttp;
    private Integer actqty = 0;
    private Integer giftqty = 0;
    private Activity activity;
    private AutoCompleteTextView txt_supplier;


    private static String[] suppliers = new String[200];
    public static String[] custom_id = new String[200];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        get_supplier();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suppliers);
        txt_supplier = findViewById(R.id.txt_supplier);
        txt_supplier.setAdapter(adapter);

        method = "new";
        try {
            userid = login.userid;
            oricode = home.oricode;
            if (oricode.equals("")){
                Toast.makeText(getApplicationContext(),"獲取產品編碼失敗，請手動輸入。",Toast.LENGTH_LONG).show();
            }
        } catch (Exception ignored){

        }

        txt_pdname = findViewById(R.id.txt_pdname);
        txt_pdcode = findViewById(R.id.txt_pdcode);
        txt_pdgtin = findViewById(R.id.txt_pdgtin);
        txt_exp = findViewById(R.id.txt_exp);
        txt_lot = findViewById(R.id.txt_lot);
        txt_inv = findViewById(R.id.txt_inv);
        txt_ttp = findViewById(R.id.txt_ttp);
        txt_unp = findViewById(R.id.txt_unp);
        txt_remark = findViewById(R.id.txt_remark);
        txt_actqyt = findViewById(R.id.txt_actqty);
        txt_gift = findViewById(R.id.txt_gift);
        txt_suppid = findViewById(R.id.txt_suppid);
        Button btn_confirm = findViewById(R.id.btn_confirm);
        Button btn_cancel = findViewById(R.id.btn_cancel);


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
                    if (!suppliertext.equals("")) {
                        int customid = Arrays.asList(suppliers).indexOf(suppliertext)+1;
                        if (customid > 0) {
                            txt_suppid.setText(valueOf(customid));
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
                    ttp = 0d;
                }
                update_unp();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @SuppressLint("DefaultLocale")
    private boolean checkForm() {
        boolean cansubmit;
        txt_ttp.setText(format("%.2f", ttp));
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
        AlertDialog dlg_confirm = builder.create();
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

                    if (!productName.equals("")){
                        action = "你確定要修改產品資料並補充";
                        Toast.makeText(getApplicationContext(),"產品已經存在，繼續修改將覆蓋資料！",Toast.LENGTH_LONG).show();
                    } else {
                        action = "你確定要新增產品並補充";
                    }
                    String supplier = json.getString("supplierName");
                    String lot = json.getString("lot");
                    String exp = json.getString("exp");
                    StringBuilder productID = new StringBuilder(json.getString("postid"));
                    postid = productID.toString();
                    while ((productID.length()<5)){
                        productID.insert(0, "0");
                    }
                    txt_supplier.setText(supplier);
                    txt_pdname.setText(productName);
                    txt_pdgtin.setText(GTIN.replace("\\u001d",""));
                    txt_lot.setText(lot);
                    txt_exp.setText(exp);
                    code = GTIN;
                    txt_pdcode.setText(productID.toString());

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
                    for (int i=0; i<200; i++){
                        suppliers[i] = "";
                        custom_id[i] = "";
                    }
                    for (int i=0;i<data.length();i++){
                        String data_string = data.getString(i);
                        String[] sup_array = data_string.split("\"");
                        suppliers[i] = sup_array[7];
                        custom_id[i] = sup_array[11];
                    }
                } catch (Exception ignored){
                }
            }
        });
    }

    public static String encode(String url) {
        try {
            return URLEncoder.encode( url, "UTF-8" );
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
                    String error = json.getString("error");
                    home.rexp = json.getString("exp");
                    home.rlot = json.getString("lot");

                    if (error.equals("false")){
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
        String exp = txt_exp.getText().toString();
        String lot = txt_lot.getText().toString();
        String inv = txt_inv.getText().toString();
        String supplierid = txt_suppid.getText().toString();
        String supplier = txt_supplier.getText().toString();
        String pdname = encode(txt_pdname.getText().toString());
        oricode = txt_pdgtin.getText().toString().replace("\\u001d","");
        String remark = encode(txt_remark.getText().toString());

        String url = "http://35.198.210.107/move_inv?method=" + method +
                "&code=" + oricode + "&productName=" + pdname +
                "&postid=" + postid + "&quantity=" + actqty +
                "&giftqty=" + giftqty +"&remark=" + remark +
                "&exp=" + exp + "&lot=" + lot + "&supplierName=" + supplier +
                "&inv=" + inv + "&ttp=" + ttp + "&custom_id=" + supplierid +
                "&userid=" + userid;
        Log.d("ORM",url);
        return (url);
    }



    @SuppressLint("DefaultLocale")
    public void update_unp(){
        try {
            ttp = Double.valueOf(txt_ttp.getText().toString());
        } catch (Exception e){
            ttp = 0d;
        }
        Double unp;
        try {
            unp = ttp / (actqty + giftqty);
        } catch (Exception e){
            unp = 0d;
        }
        if (unp.isNaN() || unp.isInfinite()){
            unp = 0d;
        }
        txt_unp.setText(format("單價: %s", format("%.2f", unp)));

    }
}
