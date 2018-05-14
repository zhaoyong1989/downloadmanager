package com.young.download;

class DownloadRecord {
    /**
     * 记录id
     */
    public long id;

    /**
     * 记录状态
     */
    public int status;

    /**
     * 下载url
     */
    public String url;

    /**
     * 用户指定的名字
     */
    public String fileName;

    /**
     * 文件大小
     */
    public long totalSize;

    /**
     * 已下载大小
     */
    public long downloadSize;

    /**
     * 本地存放路径
     */
    public String localPath;

    /**
     * 下载完成时间
     */
    public String downloadedTime;

    /**
     * 下载文件的md5值
     */
    public String md5;
}
