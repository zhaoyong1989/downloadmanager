package com.young.download;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadManager {

    private volatile static DownloadManager sInstance;

    /**
     * 下载线程池
     */
    private final ExecutorService mThreadPool;

    private final ConcurrentHashMap<Long, AbstractDownloadThread> mDownloadTaskThreadMap;

    private final ConcurrentHashMap<Long, DownloadRecord> mDownloadRecordMap;

    private final Handler mUiThreadHandler;

    private DownloadListener mListener;

    private DownloadRecord mRecord;

    private HttpClient mClient;

    private long mDownloadedSize = 0;

    private long mTotalSize = 0;

    private DownloadManager() {
        mThreadPool = Executors.newFixedThreadPool(DownloadConstants.THREAD_POOL_SIZE);
        mDownloadTaskThreadMap = new ConcurrentHashMap<>();
        mDownloadRecordMap = new ConcurrentHashMap<>();
        mUiThreadHandler = new Handler(Looper.getMainLooper());
    }

    public static DownloadManager getInstance() {
        if(sInstance == null) {
            synchronized (DownloadManager.class) {
                if(sInstance == null) {
                    sInstance = new DownloadManager();
                }
            }
        }

        return sInstance;
    }

    /**
     * 下载任务的线程
     */
    private abstract class AbstractDownloadThread implements Runnable {
        final DownloadRecord mDownloadRecord;

        private volatile boolean mIsDownloadCancelled;

        private AbstractDownloadThread(DownloadRecord downloadrecord) {
            mDownloadRecord = downloadrecord;
            this.mIsDownloadCancelled = false;
        }

        @Override
        public void run() {
            File file;
            try {
                file = this.download();
                if(file == null) {
                    dealWithException(DownloadStatus.IO_ERROR);
                    return;
                }

                mDownloadTaskThreadMap.remove(mDownloadRecord.id);
                mDownloadRecordMap.remove(mDownloadRecord.id);

                notifyDownloadStatusChange(DownloadStatus.COMPLETED);
            } catch (DownloadException e) {
                e.printStackTrace();
            }
        }

        protected abstract File download() throws DownloadException;

        void cancelDownload() {
            this.mIsDownloadCancelled = true;
        }

        boolean isDownloadCancelled() {
            return this.mIsDownloadCancelled;
        }
    }

    private void dealWithException(DownloadStatus status) {
        switch(status) {
            case NETWORK_ABSENT:
                notifyDownloadStatusChange(status);
                break;
            case SPACE_NOT_ENOUGH:
                notifyDownloadStatusChange(status);
                break;
            case URL_INVALID:
                notifyDownloadStatusChange(status);
                break;
            case IO_ERROR:
                notifyDownloadStatusChange(status);
                break;
            case PAUSED_BY_USER:
                break;
            case FILE_PATH_EMPTY:
                notifyDownloadStatusChange(status);
                break;
            case BEYOND_REDIRECT_COUNT_ERROR:
                notifyDownloadStatusChange(status);
                break;
            case HTTP_RESPONSE_ERROR:
                notifyDownloadStatusChange(status);
                break;
        }
    }

    private void notifyDownloadStatusChange(DownloadStatus status) {

        switch(status) {
            case NETWORK_ABSENT:
                if(mListener != null) {
                    mListener.onDownloadError("network absent!");
                }
                removeDownloadTaskAndFile(mRecord.id,
                        mRecord.localPath);
                break;
            case SPACE_NOT_ENOUGH:
                if(mListener != null) {
                    mListener.onDownloadError("storage space is not enough!");
                }
                removeDownloadTaskAndFile(mRecord.id,
                        mRecord.localPath);
                break;
            case URL_INVALID:
                if(mListener != null) {
                    mListener.onDownloadError("url invalid!");
                }
                removeDownloadTaskAndFile(mRecord.id,
                        mRecord.localPath);
                break;
            case IO_ERROR:
                if(mListener != null) {
                    mListener.onDownloadError("io error!");
                }
                removeDownloadTaskAndFile(mRecord.id,
                        mRecord.localPath);
                break;
            case FILE_PATH_EMPTY:
                if(mListener != null) {
                    mListener.onDownloadError("download file path is empty!");
                }
                removeDownloadTaskAndFile(mRecord.id,
                        mRecord.localPath);
                break;
            case BEYOND_REDIRECT_COUNT_ERROR:
                if(mListener != null) {
                    mListener.onDownloadError("beyond redirect max count!");
                }
                removeDownloadTaskAndFile(mRecord.id,
                        mRecord.localPath);
                break;
            case HTTP_RESPONSE_ERROR:
                if(mListener != null) {
                    mListener.onDownloadError("http response error!");
                }
                removeDownloadTaskAndFile(mRecord.id,
                        mRecord.localPath);
                break;
            case COMPLETED:
                if(mListener != null) {
                    mListener.onDownloadSuccess();
                }
                removeDownloadTask(mRecord.id);
                break;
            case IN_PROGRESS:
                if(mListener != null) {
                    mListener.onProgressChanged((int) (mDownloadedSize * 100 / mTotalSize));
                }
                break;
        }
    }

    private void removeDownloadTaskAndFile(long id, String filePath) {
        removeDownloadTask(id);
        deleteDestinationFile(filePath);
    }

    private void deleteDestinationFile(String filePath) {
        File file = new File(filePath);
        if(file.isFile() && file.exists()) {
            file.delete();
        }
    }

    private void removeDownloadTask(long id) {
        mDownloadTaskThreadMap.remove(id);
        mDownloadRecordMap.remove(id);
    }

    interface DownloadListener {
        void onProgressChanged(int progress);
        void onDownloadSuccess();
        void onDownloadError(String errMessage);
    }

    public long startDownload(String url, String downloadPath) {
        return startDownload(url, downloadPath, null);
    }

    public long startDownload(String url, String downloadPath, DownloadListener callback) {
        DownloadRecord record = buildRecord(url, downloadPath);
        mRecord = record;

        if(url == null) {
            dealWithException(DownloadStatus.URL_INVALID);
        }

        if(downloadPath == null) {
            dealWithException(DownloadStatus.FILE_PATH_EMPTY);
        }

        if(callback != null) {
            mListener = callback;
        }

        mDownloadRecordMap.put(record.id, record);

        DownloadThread downloadThread = new DownloadThread(record);
        mDownloadTaskThreadMap.put(record.id, downloadThread);
        mThreadPool.execute(downloadThread);

        return record.id;
    }

    private DownloadRecord buildRecord(String url, String downloadPath) {
        DownloadRecord record = new DownloadRecord();
        record.id = System.currentTimeMillis();
        record.url = url;
        record.localPath = downloadPath;

        return record;
    }

    private class DownloadThread extends AbstractDownloadThread {

        private DownloadThread(DownloadRecord downloadRecord) {
            super(downloadRecord);
        }

        @Override
        protected File download() throws DownloadException {
            return writeNetworkStreamToFile(mDownloadRecord, this);
        }
    }

    private File writeNetworkStreamToFile(DownloadRecord record, AbstractDownloadThread thread)
                    throws DownloadException{
        InputStream input = null;
        File file = null;
        RandomAccessFile randomAccessFile = null;

        mClient = new HttpClient();
        try {
            mClient.request(record.url);
            input = mClient.getInput();
            mTotalSize = mClient.getContentLength();

            file = new File(record.localPath);
            if (!file.getParentFile().exists()) {
                if (file.getParentFile().mkdirs()) {
                    //mLogger.i("download path not exit,create it path = " + file.getParentFile());
                } else {
                    throw new IOException("create download file path failed");
                }
            }
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }

            // 准备和定位RandomAccessFile
            randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(0);

            int length;
            byte[] buffer = new byte[DownloadConstants.BUFFER_SIZE];
            while(!thread.isDownloadCancelled() && (length = input.read(buffer)) != -1) {
                mDownloadedSize += length;
                randomAccessFile.write(buffer, 0, length);
                notifyDownloadStatusChange(DownloadStatus.IN_PROGRESS);
            }

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            dealWithException(DownloadStatus.NETWORK_ABSENT);
            return null;
        } catch (DownloadException e) {
            e.printStackTrace();
            dealWithException(e.getStatus());
            return null;
        } finally {
            if(input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
