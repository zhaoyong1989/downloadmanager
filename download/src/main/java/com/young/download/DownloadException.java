package com.young.download;

class DownloadException extends Exception {
    private DownloadStatus status;

    public DownloadException(DownloadStatus status) {
        this.status = status;
    }

    public DownloadException() {
        super();
    }

    DownloadStatus getStatus() {
        return this.status;
    }
}
