package com.example.jjz.addheader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class ListViewActivity extends Activity {

    public static String LISTVIEW_HEADER_STATUS = "header_status";

    public static int HEADER_ERROR = 0;
    public static int HEADER_ONE = 1;
    public static int HEADER_TWO = 2;

    private ListView lv;

    private TextView tvHeader;
    private List<String> stringList = Arrays.asList(new String[]{"one", "two", "three"});

    private int headerStatus = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(LISTVIEW_HEADER_STATUS)) {
            headerStatus = intent.getIntExtra(LISTVIEW_HEADER_STATUS, 0);
        }
        initView();


    }

    private void initView() {
        lv = (ListView) findViewById(R.id.lv);
        initHeader();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1);
        adapter.addAll(stringList);
        lv.setAdapter(adapter);

        tvHeader = (TextView) findViewById(R.id.tv_header);
    }

    private void initHeader() {
        switch (headerStatus) {
            case 0:
                lv.addHeaderView(tvHeader);
                break;
            case 1:
                TextView textView=new TextView(ListViewActivity.this);
                textView.setText("I am header");
                textView.setTextSize(12);
                lv.addHeaderView(textView);
                break;
            case 2:
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View headerView = inflater.inflate(R.layout.view_heder, lv, false);
                lv.addHeaderView(headerView);
                break;
        }

    }
}
