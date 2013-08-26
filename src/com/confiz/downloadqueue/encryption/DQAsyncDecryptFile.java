/*
 * Property    : Confiz Solutions
 * Created by  : Arslan Anwar
 * Updated by  : Arslan Anwar
 * 
 */

package com.confiz.downloadqueue.encryption;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.confiz.downloadqueue.utils.DQAppUtils;
import com.confiz.downloadqueue.utils.DQDebugHelper;
import com.confiz.downloadqueue.utils.DQErrors;
import com.confiz.downloadqueue.utils.DQExternalStorageHandler;
import com.example.downloadqueue.R;


/**
 * The Class DQAsyncDecryptFile.
 */
public class DQAsyncDecryptFile extends AsyncTask<Void, Integer, Void> {

    /** The tag. */
    private final String TAG = "DQAsyncDecryptFile";

    /** The source address. */
    private String mSourceAddress = null;

    /** The destination address. */
    private String mDestinationAddress = null;

    /** The context. */
    private Context mContext = null;

    /** The dialog. */
    private Dialog dialog = null;

    /** The txt_dec_detail. */
    private TextView txt_dec_detail = null;

    /** The progress bar. */
    private ProgressBar progressBar = null;

    /** The force stop. */
    public boolean forceStop = false;

    /**
     * This variable selects the message to display in case of download failure
     * <ul>
     * <li><b>0: </b>No message, downloading is successful</li>
     * <li><b>1: </b>MESSAGE_EXTERNAL_STORAGE_INSUFFICIENT_SPACE_VAULT</li>
     * </ul>
     * .
     */
    public int whichMessageToDisplay = 0;

    /** The file size. */
    private long fileSize = -1;

    /** The show dialog. */
    boolean showDialog = true;

    /** The procgress of file. */
    private int procgressOfFile = 0;

    /** The handler. */
    Handler handler = new Handler() {

	@Override
	public void handleMessage(Message msg) {

	    // get the bundle and extract data by key
	    final Bundle b = msg.getData();
	    DQAsyncDecryptFile.this.procgressOfFile = b.getInt("parogressValue");
	    if (DQAsyncDecryptFile.this.progressBar != null) {
		DQAsyncDecryptFile.this.progressBar
			.setProgress(DQAsyncDecryptFile.this.procgressOfFile);
		if (DQAsyncDecryptFile.this.txt_dec_detail != null) {
		    DQAsyncDecryptFile.this.txt_dec_detail
			    .setText(DQAsyncDecryptFile.this.procgressOfFile
				    + "% ");
		}
	    }
	}
    };

    /**
     * Instantiates a new async decrypt file.
     */
    public DQAsyncDecryptFile() {

    }

    /**
     * Instantiates a new async decrypt file.
     * 
     * @param ctx
     *            the ctx
     * @param sourceAddress
     *            the source address
     * @param destinationAddress
     *            the destination address
     */
    public DQAsyncDecryptFile(Context ctx, String sourceAddress,
	    String destinationAddress) {

	this.mContext = ctx;
	this.mSourceAddress = sourceAddress;
	this.mDestinationAddress = destinationAddress;
    }

    /**
     * Force cancel.
     */
    public void forceCancel() {

	this.forceStop = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {

	if (this.showDialog == true) {
	    try {

		this.dialog = new Dialog(this.mContext);
		this.dialog.getWindow();
		this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.dialog.setContentView(R.layout.dec_dailog_ui);

		this.txt_dec_detail = (TextView) this.dialog
			.findViewById(R.id.dec_txt_detail);
		this.progressBar = (ProgressBar) this.dialog
			.findViewById(R.id.dec_progress_bar);

		if (this.txt_dec_detail != null) {
		    this.txt_dec_detail.setText(0 + "% ");
		}
		if (this.progressBar != null) {
		    this.progressBar.setProgress(0);
		}
		this.dialog.setCancelable(false);
		this.dialog.show();
	    } catch (final Exception exception) {
		DQDebugHelper.printAndTrackException(this.TAG, exception);
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Void doInBackground(Void... vod) {

	if (DQAppUtils.isSaved(this.mDestinationAddress)) {
	    return null;
	}
	try {
	    final long availableSpace = DQExternalStorageHandler
		    .getExternalStorageAvailableSpace();
	    final long lenghtOfFile = new File(this.mSourceAddress).length();
	    this.setFileSize(lenghtOfFile);
	    if (availableSpace < lenghtOfFile) {
		this.whichMessageToDisplay = 1;
		return null;
	    }
	    final File desFile = new File(this.mDestinationAddress);
	    if (desFile.exists()) {
		desFile.createNewFile();
	    }
	    new DQEncryptionAndDownloadManager(this.mContext)
		    .fixedSizedDecryption(this.mSourceAddress,
			    this.mDestinationAddress, this);
	} catch (final Exception exception) {
	    DQDebugHelper.printException(this.mContext, exception);
	}

	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(Void vod) {

	try {
	    if (this.showDialog == true && this.dialog != null
		    && this.dialog.isShowing()) {
		this.dialog.hide();
		this.dialog.dismiss();
	    }

	    if (this.whichMessageToDisplay == 0) {
		this.executeEncryptedFile();
	    } else {

		switch (this.whichMessageToDisplay) {
		case 1:
		    DQAppUtils.showInsufficientSpaceDailog(
			    this.mContext,
			    this.mContext
				    .getString(R.string.error_msg_external_storage_insufficient_space_vault),
			    this.mContext
				    .getString(R.string.dlg_title_insufficient_space),
			    this.getFileSize());
		    break;
		case 2:
		    DQAppUtils.showDialogMessage(this.mContext, mContext
			    .getString(DQErrors.VERY_OLD_FILE_NOT_DECRYPTED
				    .value()));

		    break;
		default:
		    break;
		}
		DQAppUtils.deleteHiddenFolder();
	    }
	} catch (final Exception exception) {
	    DQDebugHelper.printAndTrackException(this.TAG, exception);
	}

    }

    /**
     * This is a custom function that updates the progress in its asynctask.
     * 
     * @param progressPercentage
     *            : 0 <= x <= 100
     * @return: void
     */
    public void updateProgress(int progressPercentage) {

	if (progressPercentage < 0) {
	    progressPercentage = 0;
	} else if (progressPercentage > 100) {
	    progressPercentage = 100;
	}
	final Message msg = new Message();
	final Bundle bundle = new Bundle();
	bundle.putInt("parogressValue", progressPercentage);
	msg.setData(bundle);
	this.handler.sendMessage(msg);
    }

    /**
     * Gets the file size.
     * 
     * @return the file size
     */
    public long getFileSize() {

	return this.fileSize;
    }

    /**
     * Sets the file size.
     * 
     * @param fileSize
     *            the new file size
     */
    public void setFileSize(long fileSize) {

	this.fileSize = fileSize;
    }

    /**
     * Execute encrypted file.
     */
    public void executeEncryptedFile() {

	try {
	    String localPath = this.mDestinationAddress;
	    if (localPath == null
		    && DQAppUtils.isSaved(this.mDestinationAddress) == false) {
		localPath = null;
	    }

	    if (localPath == null) {
		DQAppUtils.showDialogMessage(this.mContext,
			"Unable to play video. Unkonw error");
		DQDebugHelper.printData(this.TAG, "LocalPath is null");
		return;
	    }

	    Uri path = null;
	    final File file = new File(localPath);
	    try {
		path = Uri.fromFile(file);
	    } catch (final Exception exception) {
		DQDebugHelper.printAndTrackException(this.mContext, exception);
	    }

	    final Intent intent = new Intent(Intent.ACTION_VIEW);
	    intent.setDataAndType(path, "video/*");
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

	    final List<ResolveInfo> intents = this.mContext.getPackageManager()
		    .queryIntentActivities(intent,
			    PackageManager.MATCH_DEFAULT_ONLY);

	    if (intents == null || intents.size() == 0) {
		final AlertDialog alert = new AlertDialog.Builder(this.mContext)
			.setMessage(
				this.mContext
					.getString(R.string.error_msg_pdf_reader_not_found))
			.setPositiveButton(
				this.mContext.getString(R.string.btn_ok), null)
			.create();
		alert.show();

	    } else {
		try {
		    this.mContext.startActivity(intent);
		} catch (final ActivityNotFoundException exception) {
		    final AlertDialog alert = new AlertDialog.Builder(
			    this.mContext)
			    .setMessage(
				    this.mContext
					    .getString(R.string.error_msg_encountered_an_error))
			    .setPositiveButton(
				    this.mContext.getString(R.string.btn_ok),
				    null).create();
		    alert.show();
		}
	    }
	} catch (final Exception exception) {
	    DQDebugHelper.printAndTrackException(this.mContext, exception);
	}
    }
}