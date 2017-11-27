package com.ormedia.qrscanner.Network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ormedia.qrscanner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by Vivienne on 2016/1/7.
 */
public class JSONResponse {
    public interface onComplete {
        void onComplete(JSONObject json);
    }
    private Context context;
    private String url;
    private onComplete listener;

  //  private JSONObject jsonObj;
    public JSONResponse(Context c, String u, onComplete cb) {
        context = c;
        url =u;
        listener = cb;
        getJsonAsync();
    }
    public JSONResponse(Context c, String u) {
        context = c;
        url =u;
    }



    public void getJsonAsync() {

        asyncNetwork network = (asyncNetwork) new asyncNetwork(new asyncNetwork.OnAsyncTaskCompleted() {
            @Override
            public void onAsyncTaskCompleted(String response) {
                JSONObject jsonObj = new JSONObject();
                try {
                    jsonObj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (context!=null) {
                        Toast.makeText(context, R.string.connection_error, Toast.LENGTH_LONG).show();
                    }
                }
                listener.onComplete(jsonObj);
            }
        },this.context, url).execute();
    }

    public JSONObject getJsonObj(int i,String responseText){
        JSONObject c=null;
        if (responseText != null) {
            try {
                JSONObject jsonObj = new JSONObject(responseText);
                JSONArray unitPt = jsonObj.getJSONArray("data");
                c = unitPt.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    return c;
    }




//    public void parse() {
//
//
//        if (responseText != null) {
//            try {
//                JSONObject jsonObj = new JSONObject(responseText);
//                JSONArray unitPt = jsonObj.getJSONArray("data");
//
//                // looping through All Contacts
//                for (int i = 0; i < unitPt.length(); i++) {
//                    JSONObject c = unitPt.getJSONObject(i);
//
//                    String id = c.getString("id");
//                    String building_zh = c.getString("building_zh");
//                    String street_zh = c.getString("street_zh");
//                    String price = c.getString("price");
//                    JSONArray k = c.getJSONArray("images");
//                    String image = k.getJSONArray(0).getString(0);
//
//                    // tmp hashmap for single contact
//                    Unit unit = new Unit(id,building_zh,street_zh,image,price);
//
//                    //contact.put(TAG_PHONE_MOBILE, mobile);
//
//                    // adding contact to contact list
////                    unitList.add(unit);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }


}
