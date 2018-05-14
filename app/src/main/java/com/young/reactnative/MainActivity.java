package com.young.reactnative;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.young.download.DownloadManager;
import com.young.downloadmanager.R;

public class MainActivity extends AppCompatActivity {

    private String apkUrl = "";//填入下载地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        download();
    }

    private void download() {
        /*DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));

        request.setTitle("download");
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        //request.setMimeType("application/vnd.android.package-archive");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        //创建目录
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        file.mkdir();

        //设置文件存放路径
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS  , "welcome.png" ) ;

        long id = downloadManager.enqueue(request);*/

        DownloadManager.getInstance().startDownload(apkUrl, getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/apk.png");
    }
}
