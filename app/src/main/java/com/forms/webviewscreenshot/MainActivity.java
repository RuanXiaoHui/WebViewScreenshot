package com.forms.webviewscreenshot;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private Button btnScreen;
    private WebView wv;
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //当前手机版本判断，必须在setContentView之前进行，这个是5.0+Android对WebView加载的优化，所以要进行处理下
        checkSdkVersion();
        setContentView(R.layout.activity_main);
        requestPermission();
        initView();
        initData();
        initListener();
    }


    private void initView() {
        btnScreen = (Button) findViewById(R.id.btnScreen);
        wv = (WebView) findViewById(R.id.wv);

        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setSupportZoom(false);
        wv.requestFocusFromTouch();
        wv.setDrawingCacheEnabled(true);

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });

    }

    private void initData() {

        wv.loadUrl("http://m.lashou.com/");
    }


    private void initListener() {


        btnScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                } else {
                    btnScreen.setEnabled(false);
                    boolean b = WebViewScreenShotUtils.ActionScreenshot(MainActivity.this, wv);
                    if (b) {
                        Toast.makeText(MainActivity.this, "保存成功",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                    }
                    btnScreen.setEnabled(true);
                }
            }
        });
    }


    /**
     * 当系统版本大于5.0时 开启enableSlowWholeDocumentDraw 获取整个html文档内容
     * 在5.0+版本上，Android对WebView做了优化，旨在减少内存占用以提高性能。
     * 因此在默认情况下会智能的绘制html中需要绘制的部分，其实就是当前屏幕展示的html内容，
     * 因此会出现未显示的图像是空白的。解决办法是调用enableSlowWholeDocumentDraw()方法
     */
    private void checkSdkVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }
    }

    /**
     * 动态申请权限
     */
    private void requestPermission() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        200);
            }

        }
    }

    /**
     * 申请权限的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200:
                boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Log.d(TAG, "writeAcceped--" + writeAccepted);
                break;

        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
            wv.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
