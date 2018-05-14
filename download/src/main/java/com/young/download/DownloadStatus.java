package com.young.download;

enum DownloadStatus {
    /**
     * 下载任务已创建，还未启动
     */
    READY,

    /**
     * 网络异常
     */
    NETWORK_ABSENT,

    /**
     * 存储空间不足
     */
    SPACE_NOT_ENOUGH,

    /**
     * url格式错误
     */
    URL_INVALID,

    /**
     * IO错误
     */
    IO_ERROR,

    /**
     * 下载过程中
     */
    IN_PROGRESS,

    /**
     * 下载完成
     */
    COMPLETED,

    /**
     * md5错误
     */
    MD5_ERROR,

    /**
     * 用户停止下载线程
     */
    PAUSED_BY_USER,

    /**
     *
     */
    FILE_PATH_EMPTY,

    /**
     * 超过最大重定向次数
     */
    BEYOND_REDIRECT_COUNT_ERROR,

    /**
     * http response error
     */
    HTTP_RESPONSE_ERROR,
}
