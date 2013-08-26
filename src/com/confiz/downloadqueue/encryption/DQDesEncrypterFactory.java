/**
 * 
 */

package com.confiz.downloadqueue.encryption;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.confiz.downloadqueue.utils.DQDebugHelper;


/**
 * A factory for creating DQDesEncrypter objects.
 * 
 * @author Muhammad Arslan Anwar
 */
public abstract class DQDesEncrypterFactory {


	/**
	 * Gets the des encrypter.
	 * 
	 * @param context
	 *            the context
	 * @return the des encrypter
	 */
	public static DQDesEncrypter getDesEncrypter(Context context) {

		/*
		 * get String DRMSalt from assets>DrmSalt.properties file
		 */
		final Properties properties = DQDesEncrypterFactory.getDrmSaltProperties(context);
		final String passPhrase = properties.getProperty("DRMSalt");
		return new DQDesEncrypter(context, passPhrase);
	}


	/**
	 * Method to get Properties of DrmSalt.
	 * 
	 * @param context
	 *            the context
	 * @return Properties of file DrmSalt.properties which is located in assets
	 *         folder
	 */
	private static Properties getDrmSaltProperties(Context context) {

		final Resources resources = context.getResources();
		final AssetManager assetManager = resources.getAssets();
		final Properties properties = new Properties();
		InputStream inputStream;
		try {
			inputStream = assetManager.open("DrmSalt.properties");
			properties.load(inputStream);
		} catch (final IOException exception) {
			DQDebugHelper.printAndTrackException(context, exception);
		}
		return properties;
	}
}
