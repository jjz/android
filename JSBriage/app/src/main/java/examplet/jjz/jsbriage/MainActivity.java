package examplet.jjz.jsbriage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        webView = (WebView) findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webView.addJavascriptInterface(new AndroidToast(), "AndroidToast");
        webView.addJavascriptInterface(new AndroidMessage(), "AndroidMessage");
        webView.loadUrl("file:///android_asset/index.html");
    }


    public class AndroidToast {

        @JavascriptInterface
        public void show(String str) {
            Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
        }

    }

    public class AndroidMessage {
        @JavascriptInterface
        public String getMsg() {
            return "form java";
        }
    }

    public void  javaCallJS(){
        webView.loadUrl("javascript:callFromJava('call from java')");
    }

    @Override
    public void onBackPressed() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                javaCallJS();
            }
        }).start();

    }
}
