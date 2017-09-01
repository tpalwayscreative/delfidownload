package com.learn2crack.filedownload.download;

/**
 * Created by PC on 9/1/2017.
 */

public interface OnAttachmentDownloadListener {
    void onAttachmentDownloadedSuccess();
    void onAttachmentDownloadedError();
    void onAttachmentDownloadedFinished();
    void onAttachmentDownloadUpdate(int percent);
}
