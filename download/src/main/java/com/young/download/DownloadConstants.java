package com.young.download;

class DownloadConstants {
    /**
     * 连接超时
     */
    static final int CONNECT_TIME_OUT = 10000;
    /**
     * 读取超时
     */
    static final int READ_TIME_OUT = 10000;
    /**
     * 缓存区大小
     */
    static final int BUFFER_SIZE = 4 * 1024;
    /**
     * 最大重定向次数
     */
    static final int REDIRECT_COUNT_MAX = 5;
    /**
     * 线程池大小
     */
    static final int THREAD_POOL_SIZE = 1;
}
