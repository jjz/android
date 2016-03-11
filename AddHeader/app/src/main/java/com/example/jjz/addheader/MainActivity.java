package com.example.jjz.addheader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnError;
    private Button btnHeaderOne;
    private Button btnHeaderTwo;

    private ListView lv;

    private TextView tvHeader;
    private List<String> stringList = Arrays.asList(new String[]{"one", "two", "three"});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
    }

    private void initView() {
        btnError = (Button) findViewById(R.id.btn_add_error);
        btnHeaderOne = (Button) findViewById(R.id.btn_add_header_one);
        btnHeaderTwo = (Button) findViewById(R.id.btn_add_header_two);
        lv = (ListView) findViewById(R.id.lv);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1);
        adapter.addAll(stringList);
        lv.setAdapter(adapter);

        tvHeader = (TextView) findViewById(R.id.tv_header);

    }

    private void initListener() {
        btnError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lv.addHeaderView(tvHeader);

            }
        });
        btnHeaderOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT);
                tvHeader.setLayoutParams(layoutParams);
                lv.addHeaderView(tvHeader);


            }
        });
        btnHeaderTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
