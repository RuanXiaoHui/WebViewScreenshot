package com.forms.webviewscreenshot;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class WebViewScreenShotUtils {

    //Default Page Max=6
    private static final int defaultPage = 6;


    public static boolean ActionScreenshot(Context context, WebView webView) {

         File  realFile=null;

        if (webView == null || context == null) {
            return false;
        }

        //No External Storage
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

            return false;
        }
        int screenHeight = getScreenHeight(context);
        int MAXScreenshotHeight = defaultPage * screenHeight;
        boolean isWriteSuccess = false;
        float scale = webView.getScale();
        int webViewHeight = (int) (webView.getContentHeight() * scale + 0.5);

        if (webViewHeight > MAXScreenshotHeight) {
            Log.d("xxxx", "screenHeight Very Big");
            webViewHeight = MAXScreenshotHeight;
        }
        Bitmap bitmap = Bitmap.createBitmap(webView.getWidth(), webViewHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        webView.draw(canvas);

        String fileName = System.currentTimeMillis() + ".jpg";

        String path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();

        File file=new File(path,"/iGTB");
        if (!file.exists()){
            file.mkdirs();
        }
        realFile = new File(file, fileName);
        FileOutputStream fos=null;
        try {
            fos = new FileOutputStream(realFile);
            isWriteSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        finally {

            //sendBroadcast
            if (isWriteSuccess&&realFile.exists()){
                scanFile(context,realFile.getAbsolutePath());
            }
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            if (fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return  isWriteSuccess;
    }

    /**
     * getScreenHeight
     * @param context
     * @return
     */
    private static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        return outMetrics.heightPixels;
    }

    private static void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }
}
