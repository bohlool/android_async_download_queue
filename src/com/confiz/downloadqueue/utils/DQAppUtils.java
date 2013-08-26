/**
 * 
 */
package com.confiz.downloadqueue.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.confiz.downloadqueue.encryption.DQAppFolders;
import com.example.downloadqueue.R;
import com.google.gson.Gson;

/**
 * @author Confiz
 * 
 */
public class DQAppUtils {
    private static final String TAG = "DQAppUtils.java";

    public static JSONObject converToBundle(Object object) {
	Gson gson = new Gson();
	JSONObject result = null;
	try {
	    result = new JSONObject(gson.toJson(object));
	    result.put("className", object.getClass().getName());
	} catch (JSONException exception) {
	    exception.printStackTrace();
	}
	return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object createFromBundle(String jsonObject) {
	Gson gson = new Gson();
	try {
	    JSONObject json = new JSONObject(jsonObject);
	    Class cls = Class.forName(json.getString("className"));

	    return gson.fromJson(jsonObject, cls);
	} catch (JSONException exception) {
	    exception.printStackTrace();
	} catch (ClassNotFoundException exception) {
	    exception.printStackTrace();
	}
	return null;

    }

    /** The Constant SECOND. */
    public static final long SECOND = 1000;

    /** The Constant MINT. */
    public static final long MINT = 60 * SECOND;

    /** The Constant HOUR. */
    public static final long HOUR = 60 * MINT;

    /** The Constant DAY. */
    public static final long DAY = 24 * HOUR;

    /**
     * Gets the app build date.
     * 
     * @param context
     *            the context
     * @return the app build date
     */
    public static String getAppBuildDate(Context context) {

	String date = null;
	try {
	    final ApplicationInfo ai = context.getPackageManager()
		    .getApplicationInfo(context.getPackageName(), 0);
	    final ZipFile zf = new ZipFile(ai.sourceDir);
	    final ZipEntry ze = zf.getEntry("classes.dex");
	    final long time = ze.getTime();
	    date = convertDateToString(new java.util.Date(time));

	} catch (final Exception exception) {
	    DQDebugHelper.printAndTrackException(TAG, exception);
	}
	return date;
    }

    /**
     * Convert date to string.
     * 
     * @param sDate
     *            the s date
     * @return the string
     */
    public static String convertDateToString(Date sDate) {

	final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM");
	final String strDate = sdf.format(sDate);
	return strDate;
    }

    /**
     * Convert to m d5.
     * 
     * @param stringToConvert
     *            the string to convert
     * @return the string
     */
    public static String convertToMD5(String stringToConvert) {

	String result = null;
	try {
	    byte[] hash;

	    try {
		hash = MessageDigest.getInstance("MD5").digest(
			stringToConvert.getBytes("UTF-8"));
	    } catch (final NoSuchAlgorithmException exception) {
		throw new RuntimeException("Huh, MD5 should be supported?",
			exception);
	    } catch (final UnsupportedEncodingException exception) {
		throw new RuntimeException("Huh, UTF-8 should be supported?",
			exception);
	    }

	    final StringBuilder hex = new StringBuilder(hash.length * 2);
	    for (final byte b : hash) {
		if ((b & 0xFF) < 0x10) {
		    hex.append("0");
		}
		hex.append(Integer.toHexString(b & 0xFF));
	    }
	    result = hex.toString();
	} catch (final Exception exception) {
	    DQDebugHelper.printAndTrackException(TAG, exception);
	}
	return result;
    }

    /**
     * Gets the current time.
     * 
     * @return the current time
     */
    public static String getCurrentTime() {

	return "" + System.currentTimeMillis();
    }

    /**
     * Show dialog message.
     * 
     * @param context
     *            the context
     * @param message
     *            the message
     */
    public static void showDialogMessage(Context context, String message) {

	try {
	    if (context != null) {
		final AlertDialog alert = new AlertDialog.Builder(context)
			.setMessage(message)
			.setPositiveButton(context.getString(R.string.btn_ok),
				null).create();
		alert.show();

	    }
	} catch (final Exception exception) {
	    DQDebugHelper.printAndTrackException(context, exception);
	}
    }

    /**
     * Show dialog message.
     * 
     * @param context
     *            the context
     * @param message
     *            the message
     * @param title
     *            the title
     */
    public static void showDialogMessage(Context context, String message,
	    String title) {

	try {
	    if (context != null) {
		final AlertDialog alert = new AlertDialog.Builder(context)
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton(context.getString(R.string.btn_ok),
				null).create();
		alert.show();

	    }
	} catch (final Exception exception) {
	    DQDebugHelper.printAndTrackException(context, exception);
	}
    }

    /**
     * Show dialog message.
     * 
     * @param context
     *            the context
     * @param message
     *            the message
     * @param title
     *            the title
     */
    public static void showDialogMessage(Context context,
	    DialogInterface.OnClickListener clickHandler, String message,
	    String title) {

	try {
	    if (context != null) {
		final AlertDialog alert = new AlertDialog.Builder(context)
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton(context.getString(R.string.btn_ok),
				clickHandler).create();
		alert.show();

	    }
	} catch (final Exception exception) {
	    DQDebugHelper.printAndTrackException(context, exception);
	}
    }

    /**
     * Show insufficient space dailog.
     * 
     * @param context
     *            the context
     * @param message
     *            the message
     * @param title
     *            the title
     * @param fileSize
     *            the file size
     */
    public static void showInsufficientSpaceDailog(final Context context,
	    String message, String title, long fileSize) {

	try {
	    final Context caller = context;

	    final DecimalFormat df = new DecimalFormat("0.0");
	    final double SizeOfFile = (double) (fileSize / DQExternalStorageHandler.SIZE_KB)
		    / (double) DQExternalStorageHandler.SIZE_KB;
	    final double AvailableSpace = (double) (DQExternalStorageHandler
		    .getExternalStorageAvailableSpace() / DQExternalStorageHandler.SIZE_KB)
		    / (double) DQExternalStorageHandler.SIZE_KB;
	    final double RequiredSpace = (double) ((fileSize - DQExternalStorageHandler
		    .getExternalStorageAvailableSpace()) / DQExternalStorageHandler.SIZE_KB)
		    / (double) DQExternalStorageHandler.SIZE_KB;

	    final Dialog dialog = new Dialog(caller);
	    dialog.getWindow();
	    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    dialog.setContentView(R.layout.dialog_insufficient_space);

	    final Button btnOk = (Button) dialog
		    .findViewById(R.id.insufficient_space_ok_btn);
	    final TextView downloadSize = (TextView) dialog
		    .findViewById(R.id.download_size);
	    final TextView availableSpace = (TextView) dialog
		    .findViewById(R.id.available_size);
	    final TextView requiredSpace = (TextView) dialog
		    .findViewById(R.id.required_Size);
	    final TextView dlgTitle = (TextView) dialog
		    .findViewById(R.id.insufficient_space_title);
	    final TextView dlgMessage = (TextView) dialog
		    .findViewById(R.id.insufficient_space_message);

	    dlgTitle.setText(title);
	    dlgMessage.setText(message);
	    downloadSize.setText(" " + df.format(SizeOfFile) + " MB ");
	    availableSpace.setText(" " + df.format(AvailableSpace) + " MB ");
	    requiredSpace.setText(" " + df.format(RequiredSpace) + " MB");

	    btnOk.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View view) {

		    dialog.dismiss();
		    dialog.cancel();
		}
	    });
	    dialog.show();

	} catch (final Exception exception) {

	    DQDebugHelper.printAndTrackException(TAG, exception);
	}

    }

    /**
     * Save image.
     * 
     * @param bitmap
     *            the bitmap
     * @param path
     *            the path
     * @param fileName
     *            the file name
     * @param context
     *            the context
     * @return the string
     */
    public static String saveImage(Bitmap bitmap, String path, String fileName,
	    Context context) {

	return saveImage(bitmap, path, fileName, context, false);
    }

    /**
     * Save image.
     * 
     * @param bitmap
     *            : its the bitmap data to be written to the file
     * @param path
     *            : should start and end with a slash e.g. /path/
     * @param fileName
     *            : should be a string with extension without any slashes e.g.
     *            fileName.ext
     * @param context
     *            : context is cascading from the action-starting-activity to
     *            this function
     * @param replace
     *            the recycle
     * @return the string
     * @returns fileName: file on which the file was written
     */
    public static String saveImage(Bitmap bitmap, String path, String fileName,
	    Context context, boolean replace) {

	OutputStream outStream = null;

	File file = null;
	try {
	    file = new File(path, "");
	    file.mkdirs();
	    File imgFile = new File(path + fileName);
	    if (new File(path + fileName).exists() == true) {
		imgFile.delete();
	    }
	    if (bitmap == null) {
		return null;
	    }

	    file = new File(path, fileName);
	    outStream = new FileOutputStream(file);

	    if (fileName.endsWith(".png") || fileName.endsWith(".PNG")) {
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
	    } else if (fileName.endsWith(".jpg") || fileName.endsWith(".JPG")) {
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
	    } else {
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
	    }

	    outStream.flush();
	    outStream.close();
	} catch (final FileNotFoundException exception) {
	    DQDebugHelper.printAndTrackException(TAG, exception);
	} catch (final Exception exception) {
	    DQDebugHelper.printAndTrackException(TAG, exception);
	}
	return file.toString();
    }

    /**
     * Does file exist.
     * 
     * @param filePath
     *            the file path
     * @return true, if successful
     */
    public static boolean doesFileExist(String filePath) {

	String rootDir = null;
	InputStream input = null;
	try {
	    rootDir = Environment.getExternalStorageDirectory().toString();
	    input = new FileInputStream(rootDir + filePath);

	    final int data = input.read();
	    while (data != -1) {
		input.close();
		return true;
	    }
	    input.close();
	} catch (final FileNotFoundException exception) {
	    DQDebugHelper.printAndTrackException(TAG, exception);
	    return false;
	} catch (final IOException exception) {
	    DQDebugHelper.printAndTrackException(TAG, exception);
	    return false;
	} catch (final Exception exception) {
	    DQDebugHelper.printAndTrackException(TAG, exception);
	    return false;
	}
	input = null;
	return false;
    }

    public static String formatPrice(double price) {

	try {
	    DecimalFormat df = new DecimalFormat("$###0.00");
	    return df.format(price);
	} catch (final Exception exception) {
	    DQDebugHelper.printAndTrackException(TAG, exception);
	}
	return "" + price;
    }

    /**
     * Gets the rounded corner bitmap.
     * 
     * @param bitmap
     *            the bitmap
     * @param pixels
     *            the pixels
     * @param context
     *            the context
     * @return the rounded corner bitmap
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels,
	    Context context) {

	Bitmap output = null;
	try {
	    if (bitmap == null || bitmap.isRecycled()) {
		return null;
	    }
	    output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
		    Config.ARGB_8888);
	    final Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();

	    final Rect rect = new Rect(0, 0, bitmap.getWidth(),
		    bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final float roundPx = pixels;

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setAlpha(0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    paint.setAlpha(255);
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	    if (output == null || output.isRecycled()) {
		return bitmap;
	    }
	    if (bitmap != null && bitmap.isRecycled() == false) {
		bitmap.recycle();
	    }
	    return output;
	} catch (final OutOfMemoryError e) {
	    DQDebugHelper.printAndTrackError(TAG, e);
	} catch (final Exception exception) {
	    DQDebugHelper.printAndTrackException(TAG, exception);
	}
	if (output != null && output.isRecycled() == false) {
	    output.recycle();
	}
	return bitmap;
    }

    // decodes image and scales it to reduce memory consumption
    /**
     * Decode file.
     * 
     * @param f
     *            the f
     * @return the bitmap
     */
    public static Bitmap decodeFile(File f) {

	try {
	    // decode image size
	    final BitmapFactory.Options o = new BitmapFactory.Options();
	    o.inJustDecodeBounds = true;
	    BitmapFactory.decodeStream(new FileInputStream(f), null, o);

	    // Find the correct scale value. It should be the power of 2.
	    final int REQUIRED_SIZE = 100;
	    int width_tmp = o.outWidth, height_tmp = o.outHeight;
	    int scale = 1;
	    while (true) {
		if (width_tmp / 2 < REQUIRED_SIZE
			|| height_tmp / 2 < REQUIRED_SIZE) {
		    break;
		}
		width_tmp /= 2;
		height_tmp /= 2;
		scale *= 2;
	    }

	    // decode with inSampleSize
	    final BitmapFactory.Options o2 = new BitmapFactory.Options();
	    o2.inSampleSize = scale;
	    return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	} catch (final FileNotFoundException exception) {
	    DQDebugHelper.printAndTrackException(TAG, exception);
	}
	return null;
    }

    /**
     * Checks if is saved.
     * 
     * @param filePath
     *            the file path
     * @return true, if is saved
     */
    public static boolean isSaved(String filePath) {

	boolean isFileSaved = false;
	try {
	    final File file = new File(filePath);
	    isFileSaved = file.exists();
	} catch (final Exception exception) {

	    DQDebugHelper.printAndTrackException(TAG, exception);
	}

	return isFileSaved;
    }

    /**
     * Delete hidden folder.
     * 
     * @return true, if successful
     */
    public static boolean deleteHiddenFolder() {

	final File directory = new File(Environment
		.getExternalStorageDirectory().toString()
		+ DQAppFolders.getHiddenFolderPath());

	if (!directory.exists()) {
	    return true;
	}
	if (!directory.isDirectory()) {
	    return false;
	}
	final String[] list = directory.list();

	if (list != null) {
	    for (final String element : list) {
		final File entry = new File(directory, element);
		if (entry.isDirectory()) {
		    if (!removeDirectory(entry)) {
			return false;
		    }
		} else {
		    if (!entry.delete()) {
			return false;
		    }
		}
	    }
	}
	return directory.delete();
    }

    /**
     * Gets the checks if is hidden folder present.
     * 
     * @return the checks if is hidden folder present
     */
    public static boolean getIsHiddenFolderPresent() {

	final File directory = new File(Environment
		.getExternalStorageDirectory().toString()
		+ DQAppFolders.getHiddenFolderPath());

	if (directory.exists()) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Removes the directory.
     * 
     * @param directory
     *            the directory
     * @return true, if successful
     */
    public static boolean removeDirectory(String directory) {

	if (directory != null && directory.length() > 0) {
	    return removeDirectory(new File(directory));
	}
	return false;
    }

    /**
     * Removes the directory.
     * 
     * @param directory
     *            the directory
     * @return true, if successful
     */
    public static boolean removeDirectory(File directory) {

	if (directory == null) {
	    return false;
	}
	if (!directory.exists()) {
	    return true;
	}
	if (directory.isDirectory()) {
	    final String[] list = directory.list();
	    // Some JVMs return null for File.list() when the
	    // directory is empty.
	    if (list != null) {
		for (final String element : list) {
		    final File entry = new File(directory, element);
		    if (entry.isDirectory()) {
			if (!removeDirectory(entry)) {
			    return false;
			}
		    } else {
			if (!entry.delete()) {
			    return false;
			}
		    }
		}
	    }
	}
	return directory.delete();
    }

    /**
     * Gets the directory files list.
     * 
     * @param directory
     *            the directory
     * @return the directory files list
     */
    public static HashMap<String, File> getDirectoryFilesList(String directory) {

	return getDirectoryFilesList(new File(directory));
    }

    /**
     * Gets the directory files list.
     * 
     * @param directory
     *            the directory
     * @return the directory files list
     */
    public static HashMap<String, File> getDirectoryFilesList(File directory) {

	final HashMap<String, File> directoryList = new HashMap<String, File>();

	if (directory == null) {
	    return directoryList;
	}
	if (!directory.exists()) {
	    return directoryList;
	}
	if (directory.isDirectory()) {
	    final String[] list = directory.list();
	    // Some JVMs return null for File.list() when the
	    // directory is empty.
	    if (list != null) {
		for (final String element : list) {
		    final File entry = new File(directory, element);
		    if (!entry.isDirectory()) {
			String name = entry.getName();
			if (name.length() > 0 && name.contains(".")) {
			    name = (String) name.subSequence(0,
				    name.lastIndexOf('.'));
			    directoryList.put(name, entry);
			}
		    }
		}
	    }
	}
	return directoryList;
    }

    /**
     * Show toast.
     * 
     * @param appContext
     *            the app context
     * @param message
     *            the message
     */
    public static void showToast(Context appContext, String message) {

	Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Gets the network type.
     * 
     * @param ctx
     *            the ctx
     * @return the network type
     */
    public String getNetworkType(Context ctx) {

	final ConnectivityManager cm = (ConnectivityManager) ctx
		.getSystemService(Context.CONNECTIVITY_SERVICE);
	// int networkType = cm.getActiveNetworkInfo().getType();
	if (cm != null) {
	    final String networkTypeName = cm.getActiveNetworkInfo()
		    .getTypeName();

	    final TelephonyManager tm = (TelephonyManager) ctx
		    .getSystemService(Context.TELEPHONY_SERVICE);
	    final int telNetworkType = tm.getNetworkType();

	    return networkTypeName + " - " + telNetworkType;
	}
	return "";
    }

    // public static void playAudioVideo(final LCVideo LCVideo,
    // final boolean isWatchInstant, final Context activity) {
    // if (LCVideo.getFileFolderName() == LtdFolders.AUDIO) {
    // LTDPlayerFactory.startLTDAudioPlayer(LCVideo, activity);
    // return;
    // }
    // String localPath = null;
    // File file = null;
    // try {
    //
    // if (isWatchInstant == false) {
    //
    // if (LCVideo.isCanEncrypt()) {
    // localPath = LCVideo.getHiddenFilePath();
    // } else {
    // localPath = LCVideo.getFilePath();
    // }
    //
    // file = new File(localPath);
    //
    // } else {
    //
    // localPath = SMUtility.createFileUrl(
    // LCVideo.getFileNameServer(), activity,
    // LCVideo).toString();
    // localPath = localPath.replace("https", "http");
    //
    // }
    //
    // final Intent intent = new Intent();
    // intent.setAction(android.content.Intent.ACTION_VIEW);
    //
    // if (isWatchInstant == true) {
    //
    // final String tempLocalPath = new String(localPath);
    //
    // BGWorkerDefiner workToDone = new BGWorkerDefiner() {
    //
    // @Override
    // public Object performInBackground() {
    // return tempLocalPath;
    // // return getShortURL(tempLocalPath);
    // }
    //
    // @Override
    // public void finalResult() {
    // }
    //
    // @Override
    // public void callback(Object result) {
    //
    // String shortPath = (String) result;
    // String finalPath = new String(tempLocalPath);
    // if (shortPath != null && shortPath.length() > 0) {
    // finalPath = shortPath;
    // }
    // intent.setDataAndType(Uri.parse(finalPath), "video/*");
    // LTDMediaApplication.G_TRACKER
    // .trackLiveStreamingEvent(LCVideo);
    // executeIntentForplayVideo(intent, finalPath,
    // LCVideo, activity);
    // }
    // };
    //
    // new BGWorkerExecuter(workToDone, activity, "Processing...",
    // true).execute();
    //
    // } else {
    // intent.setDataAndType(Uri.fromFile(file), "video/*");
    // executeIntentForplayVideo(intent, localPath, LCVideo,
    // activity);
    // }
    // } catch (Exception exception) {
    // DQDebugHelper.printAndTrackException(TAG , exception);
    // }
    // }
    //
    // // We are using this method to exectue only video intents. Audio intent
    // will
    // // be handled in LTDPlayerFactory
    // private static void executeIntentForplayVideo(Intent intent,
    // String localPath, LCVideo LCVideo, final Context context) {
    // try {
    // List<ResolveInfo> intents = context.getPackageManager()
    // .queryIntentActivities(intent,
    // PackageManager.MATCH_DEFAULT_ONLY);
    //
    // if (intents != null && intents.size() > 0) {
    // context.startActivity(intent);
    // } else {
    //
    // Intent videoIntent = new Intent(context,
    // CommonVideoPlayerActivity.class);
    // videoIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
    // | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
    // videoIntent.putExtra("localPath", localPath);
    // videoIntent.putExtra("title", LCVideo.getTitle());
    // CommonVideoPlayerActivity.LCVideo = LCVideo;
    // context.startActivity(videoIntent);
    // }
    // } catch (Exception exception) {
    // DQDebugHelper.printAndTrackException(TAG , exception);
    // }
    // }
    //
    // public static String[] getLTDIds(Context context) {
    // String[] ltdIdList = null;
    // SMUtility smUtility = SMUtility.getInstance(context);
    // SharedPreferences pref = smUtility.getSharedPreferences();
    // String ltdIds = pref.getString(SMConstants.LIST_OF_USER_NAMES, null);
    // if (ltdIds != null) {
    // ltdIdList = ltdIds.split(",");
    // }
    // return ltdIdList;
    //
    // }
    //
    // public static void saveLTDIdToIDList(Context context, String userName) {
    // String[] ltdIdList = null;
    // SMUtility smUtility = SMUtility.getInstance(context);
    // SharedPreferences pref = smUtility.getSharedPreferences();
    // String ltdIds = pref.getString(SMConstants.LIST_OF_USER_NAMES, "");
    // if (ltdIds != null) {
    // ltdIdList = ltdIds.split(",");
    // boolean flag = false;
    // for (String string : ltdIdList) {
    // if (string.equalsIgnoreCase(userName) == true) {
    // flag = true;
    // break;
    // }
    // }
    // if (flag == false || ltdIds == null) {
    // if (ltdIds == null || ltdIds.length() <= 0) {
    // smUtility.savePerefference(SMConstants.LIST_OF_USER_NAMES,
    // userName);
    // } else {
    // smUtility.savePerefference(SMConstants.LIST_OF_USER_NAMES,
    // ltdIds + "," + userName);
    // }
    // }
    // }
    //
    // }

    /**
     * Format size.
     * 
     * @param expectedBytes
     *            the expected bytes
     * @return the string
     */
    public static String formateSize(long expectedBytes) {

	String result = null;
	if (expectedBytes < 1024) {
	    result = String.format("%d B", expectedBytes);
	} else if (expectedBytes < 1024 * 1024) {
	    final float value = (float) expectedBytes / 1024;
	    result = String.format("%1.2f KB", value);
	} else if (expectedBytes < 1024 * 1024 * 1024) {
	    final float value = (float) expectedBytes / (1024 * 1024);
	    result = String.format("%1.2f MB", value);
	} else {
	    final float value = (float) expectedBytes / (1024 * 1024 * 1024);
	    result = String.format("%1.2f GB", value);
	}
	return result;
    }

    /**
     * Formate size.
     * 
     * @param expectedBytes
     *            the expected bytes
     * @return the string
     */
    public static String formateSizeInMB(long expectedBytes) {

	String result = null;
	if (expectedBytes >= 1024 * 1024 * 1024) {
	    final float value = (float) expectedBytes / (1024 * 1024 * 1024);
	    result = String.format("%1.2f GB", value);

	} else {
	    final float value = (float) expectedBytes / (1024 * 1024);
	    result = String.format("%1.2f MB", value);
	}
	return result;
    }

    /**
     * Sets the list view height based on children.
     * 
     * @param listView
     *            the new list view height based on children
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {

	final ListAdapter listAdapter = listView.getAdapter();
	if (listAdapter == null) {
	    // pre-condition
	    return;
	}

	int totalHeight = 0;
	for (int i = 0; i < listAdapter.getCount(); i++) {
	    final View listItem = listAdapter.getView(i, null, listView);
	    if (listItem instanceof ViewGroup) {
		listItem.setLayoutParams(new LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    }
	    listItem.measure(0, 0);
	    totalHeight += listItem.getMeasuredHeight();
	}

	final ViewGroup.LayoutParams params = listView.getLayoutParams();
	params.height = totalHeight + listView.getDividerHeight()
		* (listAdapter.getCount() - 1);
	listView.setLayoutParams(params);
    }

    /**
     * Check for updates message.
     * 
     * @param context
     *            the context
     */
    public static void checkForUpdatesMessage(final Context context) {

	// try {
	//
	// long updateType =
	// sharedPreferences.getLong(VersionUpgrade.UPDATE_AVAILABLE, -1);
	// final String urlLTDMedia =
	// sharedPreferences.getString(VersionUpgrade.UPDATE_URL, "");
	//
	// if (updateType == VersionUpgradeActivity.FORCE_UPDATE) {
	// Intent intent = new Intent(context, VersionUpgradeActivity.class);
	// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// context.startActivity(intent);
	// } else if (updateType == VersionUpgradeActivity.NORMAL_UPDATE) {
	// if (context != null) {
	//
	// AlertDialog.Builder builder = new AlertDialog.Builder(context);
	// builder.setMessage(context.getString(R.string.msg_normal_updates_are_available))
	// .setCancelable(false)
	// .setPositiveButton("Update", new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int id) {
	// dialog.cancel();
	// dialog.dismiss();
	// Intent intent = new Intent(Intent.ACTION_VIEW);
	// intent.setData(Uri.parse(urlLTDMedia));
	// context.startActivity(intent);
	//
	// }
	// }).setNegativeButton("Ignore", new DialogInterface.OnClickListener()
	// {
	// public void onClick(DialogInterface dialog, int id) {
	// /*
	// * Ignore normal updates for three days.
	// */
	// long threeDaysTime = 3 * 24 * 60 * 60 * 1000;
	// long currentTime = System.currentTimeMillis();
	// long updateIgnoreTime = currentTime + threeDaysTime;
	// savePerefference(VersionUpgrade.UPDATE_IGNORE_TIME,
	// updateIgnoreTime);
	// dialog.cancel();
	// }
	// });
	//
	// AlertDialog alert = builder.create();
	// alert.show();
	//
	// }
	// } else if (updateType ==
	// VersionUpgradeActivity.NETWORK_NOT_AVAILABLE) {
	// if (context != null) {
	//
	// AlertDialog.Builder builder = new AlertDialog.Builder(context);
	// builder.setMessage(context.getString(R.string.error_msg_network_not_reachable))
	// .setCancelable(false)
	// .setPositiveButton(context.getString(R.string.btn_try_again),
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int id) {
	// dialog.cancel();
	// dialog.dismiss();
	// AsyncVersionUpgradeTask asyncVersionUpgradeTask = new
	// AsyncVersionUpgradeTask(
	// context);
	// asyncVersionUpgradeTask.execute();
	//
	// }
	// })
	// .setNegativeButton(context.getString(R.string.btn_nevermind),
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int id) {
	// dialog.cancel();
	// dialog.dismiss();
	// }
	// });
	//
	// AlertDialog alert = builder.create();
	// alert.show();
	//
	// }
	// } else {
	// if (context != null) {
	//
	// AlertDialog.Builder builder = new AlertDialog.Builder(context);
	// builder.setMessage(context.getString(R.string.msg_no_updates_available))
	// .setCancelable(false)
	// .setPositiveButton(context.getString(R.string.btn_ok),
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int id) {
	// dialog.cancel();
	// dialog.dismiss();
	// }
	// });
	//
	// AlertDialog alert = builder.create();
	// alert.show();
	//
	// }
	// }
	// } catch (Exception exception) {
	// DQDebugHelper.printAndTrackException(TAG , exception);
	// }

    }

    /**
     * Conver string to date.
     * 
     * @param stringDate
     *            the string date
     * @return the date
     */
    public static Date converStringToDate(String stringDate) {

	try {
	    final SimpleDateFormat dateFormat = new SimpleDateFormat(
		    "yyyy-MM-dd'T'HH:mm:ssZ");// ("yyyy-mm-ddTHH:mm:ss zzzz"");
	    final Date date = dateFormat.parse(stringDate);
	    return date;
	} catch (final Exception exception) {
	    DQDebugHelper.printAndTrackException(TAG, exception);
	}
	return null;
    }

    /**
     * Convert date to simple string.
     * 
     * @param sDate
     *            the s date
     * @return the string
     */
    public static String convertDateToSimpleString(Date sDate) {

	try {
	    final SimpleDateFormat dateFormat = new SimpleDateFormat(
		    "MMM dd, yyyy");
	    final String strDate = dateFormat.format(sDate);
	    return strDate;
	} catch (final Exception exception) {
	    DQDebugHelper.printAndTrackException(TAG, exception);
	}
	return null;
    }

    /**
     * Gets the full time estimations.
     * 
     * @param statTime
     *            the stat time
     * @return the full time estimations
     */
    public static String getFullTimeEstimations(long statTime) {

	String timeRemainingStr = "";
	try {

	    final long timeRemaining = System.currentTimeMillis() - statTime;

	    if (timeRemaining < MINT) {
		timeRemainingStr = String.format("%d seconds", timeRemaining
			/ SECOND);
	    } else if (timeRemaining < HOUR) {
		long time = timeRemaining;
		final int mints = (int) (timeRemaining / MINT);
		time = timeRemaining - mints * MINT;
		final int seconds = (int) (time / SECOND);
		timeRemainingStr = String.format("%d minutes, %d seconds",
			timeRemaining / MINT, seconds);
	    } else if (timeRemaining < DAY) {
		long time = timeRemaining;
		final int hours = (int) (timeRemaining / HOUR);
		time = timeRemaining - hours * HOUR;
		final int minutes = (int) (time / MINT);
		timeRemainingStr = String.format("%d hours, %d minutes",
			timeRemaining / HOUR, minutes);
	    } else {
		long time = timeRemaining;
		final int days = (int) (timeRemaining / DAY);
		time = timeRemaining - days * DAY;
		final int hours = (int) (time / HOUR);
		time = time - hours * HOUR;
		final int mints = (int) (time / MINT);
		timeRemainingStr = String.format(
			"%d Days, %d hours, %d minutes", days, hours, mints);
	    }
	} catch (final Exception exception) {
	    DQDebugHelper.printAndTrackException(TAG, exception);
	}
	return timeRemainingStr;
    }

    /**
     * Gets the theater time estimations.
     * 
     * @param date
     *            the date
     * @return the theater time estimations
     */
    public static String getTheaterTimeEstimations(Date date) {

	final long statTime = date.getTime();
	// 0 - 10: "Just now"
	// 10 - 60: minutes
	// 60 - 120: "1 hour ago"
	// 120min - 24 hours: hours
	// 24 hours - 48 hours: "1 day ago"
	// 48 hours - 1 week: days
	// 1 week - 1 year: dates
	String timeRemainingStr = "";
	try {

	    final long timeRemaining = System.currentTimeMillis() - statTime;

	    if (timeRemaining <= MINT * 10) {
		timeRemainingStr = "Just now";
	    } else if (timeRemaining <= HOUR) {
		final int mints = (int) (timeRemaining / MINT);
		timeRemainingStr = String.format("%d minutes ago", mints);
	    } else if (timeRemaining <= HOUR * 2) {
		timeRemainingStr = String.format("%d hour ago", 1);
	    } else if (timeRemaining <= HOUR * 24) {
		final int hours = (int) (timeRemaining / HOUR);
		timeRemainingStr = String.format("%d hours ago", hours);
	    } else if (timeRemaining <= DAY * 2) {
		timeRemainingStr = String.format("%d day ago", 1);
	    } else if (timeRemaining <= DAY * 7) {
		final int days = (int) (timeRemaining / DAY);
		timeRemainingStr = String.format("%d days ago", days);
	    } else {
		timeRemainingStr = convertDateToSimpleString(date);
	    }
	} catch (final Exception exception) {
	    DQDebugHelper.printAndTrackException(TAG, exception);
	}
	return timeRemainingStr;
    }

    /**
     * Format time.
     * 
     * @param mills
     *            the mills
     * @return the string
     */
    public static String formatTime(long mills) {

	try {

	    final int seconds = (int) (mills / 1000) % 60;
	    final int minutes = (int) (mills / (1000 * 60) % 60);
	    final int hours = (int) (mills / (1000 * 60 * 60) % 24);

	    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	} catch (final Exception exception) {
	    DQDebugHelper.printAndTrackException(TAG, exception);
	}
	return "00:00:00";
    }

    public static void updateButtonSatate(View view, boolean enable) {

	view.setEnabled(enable);
	view.setClickable(enable);
    }

}
