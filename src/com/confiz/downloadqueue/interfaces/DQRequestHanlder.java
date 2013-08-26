/*
 * Property    : Confiz Solutions
 * Created by  : Arslan Anwar
 * Updated by  : Arslan Anwar
 * 
 */

package com.confiz.downloadqueue.interfaces;

import com.confiz.downloadqueue.model.DQRequest;
import com.confiz.downloadqueue.utils.DQErrors;


/**
 * The Interface DQRequestHanlder.
 */
public interface DQRequestHanlder {

    /**
     * On download start.
     */
    public void onDownloadStart();

    /**
     * Update progress.
     * 
     * @param progress
     *            the progress
     */
    public void updateProgress(int progress);

    /**
     * On error occurred.
     * 
     * @param errorNo
     *            the error no
     */
    public void onErrorOccurred(DQErrors errorNo);

    /**
     * On complete.
     */
    public void onComplete();

    /**
     * Update downloading estimates.
     * 
     * @param details
     *            the details
     */
    public void updateDownloadingEstimates(String details[]);

    /**
     * Gets the downloading requester.
     * 
     * @return the downloading requester
     */
    public DQRequest getDownloadingRequester();
}
