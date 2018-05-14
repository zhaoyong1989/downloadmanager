package com.young.reactnative;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.xinpai.reactnative.R;
import com.young.download.DownloadManager;

public class MainActivity extends AppCompatActivity {

    private String apkUrl = "http://img.entgroup.com/upload/1525858620489831.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        download();
    }

    private void download() {
        /*DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));

        request.setTitle("大象投教");
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        //request.setMimeType("application/vnd.android.package-archive");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        //创建目录
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        file.mkdir();

        Log.i("zhaoyong", "path:" + file.getAbsolutePath());

        //设置文件存放路径
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS  , "welcome.png" ) ;

        long id = downloadManager.enqueue(request);*/

        Log.i("zhaoyong", "path:" + getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/apk.png");
        DownloadManager.getInstance().startDownload(apkUrl, getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/apk.png");
    }
}
