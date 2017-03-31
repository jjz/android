package jjz.uselibrary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NativeUtil.callLogFromJni();
        TextView tv = (TextView) findViewById(R.id.tv);
        TextView tv2 = (TextView) findViewById(R.id.tv_2);
        tv2.setText(Base64.encodeToString(NativeUtil.getRandom(), Base64.DEFAULT));
    }
}
