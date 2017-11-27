package com.ormedia.qrscanner;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
        Button scanBarcodeButton = (Button) findViewById(R.id.scan_barcode_button);
        Button siemensButton = (Button) findViewById(R.id.siemens_button);
        Button submitBarcodeButton = (Button) findViewById(R.id.submit_button);
        Button trimButton = (Button) findViewById(R.id.trim_button);
        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });


        submitBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update(mResultTextView.getText().toString(), productTextView.getText().toString(), supplierTextView.getText().toString());
            }

        });

        siemensButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supplierTextView.setText("SIEMENS");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editButton_btn:
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
                View mView = layoutInflaterAndroid.inflate(R.layout.editbutton_dialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputDialogButtonNumber = (EditText) mView.findViewById(R.id.btnNumber_id);
                final EditText userInputDialogButtontext = (EditText) mView.findViewById(R.id.btnText_id);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here
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
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    mResultTextView.setText(barcode.displayValue);

                    new JSONResponse(this, "http://35.198.198.0/scan?action=scan&code="+barcode.displayValue, new JSONResponse.onComplete() {
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



                } else mResultTextView.setText(R.string.no_barcode_captured);
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }

}
