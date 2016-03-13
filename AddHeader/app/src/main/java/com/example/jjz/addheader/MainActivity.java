package com.example.jjz.addheader;

import android.content.Intent;
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


    }

    private void initListener() {
        btnError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ListViewActivity.class);
                intent.putExtra(ListViewActivity.LISTVIEW_HEADER_STATUS,ListViewActivity.HEADER_ERROR);
                startActivity(intent);


            }
        });
        btnHeaderOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MainActivity.this,ListViewActivity.class);
                intent.putExtra(ListViewActivity.LISTVIEW_HEADER_STATUS,ListViewActivity.HEADER_ONE);
                startActivity(intent);

            }
        });
        btnHeaderTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MainActivity.this,ListViewActivity.class);
                intent.putExtra(ListViewActivity.LISTVIEW_HEADER_STATUS,ListViewActivity.HEADER_TWO);
                startActivity(intent);

            }
        });
    }
}
