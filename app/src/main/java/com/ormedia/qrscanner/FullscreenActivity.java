package com.ormedia.qrscanner;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.ormedia.qrscanner.barcode.BarcodeCaptureActivity;
import com.ormedia.qrscanner.Network.*;

import org.json.JSONObject;

import java.util.zip.Inflater;

import static android.app.PendingIntent.getActivity;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    private static final String LOG_TAG = FullscreenActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;

    private TextView mResultTextView;
    private TextView productTextView;
    private TextView supplierTextView;
    private TextView trimbTextview;
    private TextView trimaTextview;
    private String code;
    private boolean longCodeMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mResultTextView = (TextView) findViewById(R.id.result_textview);
        productTextView = (TextView) findViewById(R.id.product_name_textview);
        supplierTextView = (TextView) findViewById(R.id.supplier_textview);
        trimbTextview = (TextView) findViewById(R.id.trim_before_textview);
        trimaTextview = (TextView) findViewById(R.id.trim_after_textview);
        productTextView.requestFocus();
        Button scanBarcodeButton = (Button) findViewById(R.id.scan_btn);
        Button supplierButton1 = (Button) findViewById(R.id.supplier_btn1);
        Button supplierButton2 = (Button) findViewById(R.id.supplier_btn2);
        Button supplierButton3 = (Button) findViewById(R.id.supplier_btn3);

        Button productButton1 = (Button) findViewById(R.id.product_btn1);
        Button productButton2 = (Button) findViewById(R.id.product_btn2);
        Button productButton3 = (Button) findViewById(R.id.product_btn3);
        Button productButton4 = (Button) findViewById(R.id.product_btn4);
        Button productButton5 = (Button) findViewById(R.id.product_btn5);
        Button productButton6 = (Button) findViewById(R.id.product_btn6);
        Button productButton7 = (Button) findViewById(R.id.product_btn7);
        Button productButton8 = (Button) findViewById(R.id.product_btn8);
        Button productButton9 = (Button) findViewById(R.id.product_btn9);
        Button submitBarcodeButton = (Button) findViewById(R.id.upload_btn);
        Button trimButton = (Button) findViewById(R.id.trim_button);

        supplierButton1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSupplierButtonPopup(v);
                return true;
            }
        });

        supplierButton2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSupplierButtonPopup(v);
                return true;
            }
        });

        supplierButton3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSupplierButtonPopup(v);
                return true;
            }
        });

        productButton1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSupplierButtonPopup(v);
                return true;
            }
        });

        productButton2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSupplierButtonPopup(v);
                return true;
            }
        });

        productButton3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSupplierButtonPopup(v);
                return true;
            }
        });

        productButton4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSupplierButtonPopup(v);
                return true;
            }
        });

        productButton5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSupplierButtonPopup(v);
                return true;
            }
        });

        productButton6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSupplierButtonPopup(v);
                return true;
            }
        });
        productButton7.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSupplierButtonPopup(v);
                return true;
            }
        });

        productButton8.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSupplierButtonPopup(v);
                return true;
            }
        });

        productButton9.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSupplierButtonPopup(v);
                return true;
            }
        });

        trimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    code = mResultTextView.getText().toString();
                }catch (Exception e) {

                }

                int trimbefore;
                int trimafter;
                try {
                    trimbefore = Integer.parseInt(trimbTextview.getText().toString());
                    Log.d("ORM", String.valueOf(trimbefore));
                } catch (Exception e) {
                    trimbefore = 0;
                }

                try {
                    trimafter = Integer.parseInt(trimaTextview.getText().toString());
                    Log.d("ORM", String.valueOf(trimafter));
                } catch (Exception e) {
                    trimafter = 0;
                }
                try {
                    code = code.substring(trimbefore,code.length());
                    code = code.substring(0,code.length()-trimafter);
                    Log.d("ORM", code);
                } catch (Exception e) {

                }
                mResultTextView.setText(code);

                downLoadFromServer(code);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.longCode_cbox:
                if(item.isChecked()){
                    // If item already checked then unchecked it
                    item.setChecked(false);
                    longCodeMode = false;
                }else {
                    // If item is unchecked then checked it
                    item.setChecked(true);
                    longCodeMode = true;
                }
                // Update the text view text style
            case R.id.scan_btn:
                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
                break;
            case R.id.upload_btn:
                update(mResultTextView.getText().toString(), productTextView.getText().toString(), supplierTextView.getText().toString());
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void supplier_btn_onclick(View v){
        final TextView textView = (TextView)v;
        supplierTextView.setText(textView.getText().toString());
    }

    public void product_btn_onclick(View v){
        final TextView textView = (TextView)v;
        productTextView.append(textView.getText().toString());
    }

    public void update(String code, String productName, String supplier) {
        new JSONResponse(this, "http://35.198.198.0/scan?action=save&code="+code+"&productName="+productName+"&supplierName="+supplier, new JSONResponse.onComplete() {
            @Override
            public void onComplete(JSONObject json) {
                Log.d("ORM",json.toString());
                try {
                    String productName = json.getString("productName");
                    String code = json.getString("code");
                    String supplier = json.getString("supplierName");
                    Toast.makeText(getApplicationContext(), "information updated", Toast.LENGTH_SHORT).show();
                } catch(Exception e) {
                    Log.e("ORM","FullScreenActivity onActivity Result : "+e.toString());
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String gimt = "";
        String checkString = "";
        String longCode = "";
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    longCode = barcode.displayValue;
                    if(longCodeMode){
                        char[] longCodeArray = longCode.toCharArray();
                        for(int x=13;x<longCode.length();x++){
                            if(longCode.length() - x > 1){
                                checkString = "" + longCodeArray[x] + longCodeArray[x+1];
                            }
                            if(checkString.equals("17") || checkString.equals("10")){
                                gimt = longCode.substring(0,x);
                                break;
                            }
                        }
                        mResultTextView.setText(gimt);
                        downLoadFromServer(gimt);
                    }else{
                        mResultTextView.setText(longCode);
                        downLoadFromServer(longCode);
                    }

                } else mResultTextView.setText(R.string.no_barcode_captured);
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }
    public void showEditButtonDialog(View v){
        final Button textView = (Button)v;
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.editbutton_dialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText supplierName = (EditText) mView.findViewById(R.id.btnText_id);
        supplierName.setText(textView.getText().toString());
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        textView.setText(supplierName.getText().toString());
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    public void showSupplierButtonPopup(final View v){
        Button button = (Button)v;
        PopupMenu popup = new PopupMenu(FullscreenActivity.this, button);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.supplier_btn_popupmenu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()){
                    case "Edit":    showEditButtonDialog(v);
                                    break;
                    case "Delete":  ((ViewGroup)v.getParent()).removeView(v);
                                    break;
                    default:
                        Toast.makeText(FullscreenActivity.this, "MenuItemClick error", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    public void downLoadFromServer(String gimt){
        new JSONResponse(this, "http://35.198.198.0/scan?action=scan&code="+ gimt, new JSONResponse.onComplete() {
            @Override
            public void onComplete(JSONObject json) {
                Log.d("ORM",json.toString());
                try {
                    String productName = json.getString("productName");
                    String code = json.getString("code");
                    String supplier = json.getString("supplierName");
                    productTextView.setText(productName);
                    supplierTextView.setText(supplier);
                    Toast.makeText(getApplicationContext(), "information aquired", Toast.LENGTH_SHORT).show();
                } catch(Exception e) {
                    Log.e("ORM","FullScreenActivity onActivity Result : "+e.toString());
                }
            }
        });
    }
}
