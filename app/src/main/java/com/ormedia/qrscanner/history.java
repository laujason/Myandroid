package com.ormedia.qrscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rin on 8/1/18.
 */

public class history extends AppCompatActivity{

    private ListView listView;
    private SimpleAdapter sim_aAdapter;
    private Button btn_back;

    private List<Map<String, Object>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.listView);
        btn_back = findViewById(R.id.btn_back);
        dataList = new ArrayList<Map<String, Object>>();
        sim_aAdapter = new SimpleAdapter(this, getData(), R.layout.item, new String[]{"pic0", "text0"}, new int[]{R.id.pic, R.id.text});
        listView.setAdapter(sim_aAdapter);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), home.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private List<Map<String, Object>> getData(){
        for (int i = 0; i < 20; i++){
            Map<String, Object>map = new HashMap<String, Object>();
            map.put("pic0", R.mipmap.ic_launcher);
            map.put("text0", "item "+i);
            //dataList.add(map);
        }
        return dataList;
    }
}
