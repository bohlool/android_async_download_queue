/*
 * Property    : Confiz Solutions
 * Created by  : Arslan Anwar
 * Updated by  : Arslan Anwar
 * 
 */

package com.confiz.downloadqueue.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;

import android.content.Context;

import com.confiz.downloadqueue.utils.DQDebugHelper;


/**
 * The Class DQDesEncrypter.
 * 
 * @author Muhammad Arslan Anwar	
 */
public class DQDesEncrypter {

    /**
     * <b>ALGO_NAME is the name of the Algorithm. IT is PBEWithMD5AndDES.</b>
     * <ul>
     * Components of Algorithm name.
     * <li>PBE: Password based encryption.</li>
     * <li>MD5: A standard of Message digest.</li>
     * <li>DES: Digital Encryption Scheme.</li>
     * </ul>
     */
    private static final String ALGO_NAME = "PBEWithMD5AndDES";

    /**
     * <b> Algorithm Name:</b><br />
     * <ul>
     * <li>Algorithm:<br />
     * DES: The Digital Encryption Standard as described in FIPS PUB 46-2.DES
     * has a default keySize of 56 bits</li><br />
     * <li>Mode:<br />
     * CBC: Cipher Block Chaining Mode, as defined in FIPS PUB 81.</li><br />
     * <li>Padding:<br />
     * PKCS5Padding: The padding scheme described in: RSA
     * Laboratories,"PKCS #5: Password-Based Encryption Standard," version 1.5,
     * November 1993.</li><br />
     * </ul>
     */
    private static final String TRANSFORMATION = "DES/CBC/PKCS5Padding";

    /** The Constant BUFF_SIZE. */
    public static final int BUFF_SIZE = 1024;

    /**
     * Cipher to encrypt data.
     */
    private Cipher mEcipher = null;

    /** Cipher to decrypt data. */
    private Cipher mDcipher = null;

    /** Buffer used to transport the bytes from one stream to another. */
    private final byte[] mBuffer = new byte[DQDesEncrypter.BUFF_SIZE];

    /**
     * Create an 8-byte initialization vector, used as a salt.
     */
    private final byte[] iv = new byte[] { (byte) 0x8E, (byte) 0x12,
	    (byte) 0x39, (byte) 0x9C, (byte) 0x07, (byte) 0x72, (byte) 0x6F,
	    (byte) 0x5A };

    /** The context. */
    private Context mContext = null;

    /**
     * Private constructor of the class.
     * 
     * @param context
     *            the context
     * @param passPhrase
     *            the pass phrase
     */
    public DQDesEncrypter(Context context, String passPhrase) {

	this.mContext = context;
	AlgorithmParameterSpec paramSpec = null;
	SecretKey key = null;
	paramSpec = new IvParameterSpec(this.iv);
	try {

	    final int iterationCount = 19;
	    final KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(),
		    this.iv, iterationCount);
	    key = SecretKeyFactory.getInstance(DQDesEncrypter.ALGO_NAME)
		    .generateSecret(keySpec);
	    this.mEcipher = Cipher.getInstance(DQDesEncrypter.TRANSFORMATION);
	    this.mDcipher = Cipher.getInstance(DQDesEncrypter.TRANSFORMATION);

	    // CBC requires an initialization vector
	    this.mEcipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
	    this.mDcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
	} catch (final InvalidKeySpecException exception) {
	    DQDebugHelper.printAndTrackException(context, exception);
	} catch (final java.security.InvalidAlgorithmParameterException exception) {
	    DQDebugHelper.printAndTrackException(context, exception);
	} catch (final javax.crypto.NoSuchPaddingException exception) {
	    DQDebugHelper.printAndTrackException(context, exception);
	} catch (final java.security.NoSuchAlgorithmException exception) {
	    DQDebugHelper.printAndTrackException(context, exception);
	} catch (final java.security.InvalidKeyException exception) {
	    DQDebugHelper.printAndTrackException(context, exception);
	}
    }

    /**
     * Encrypt.
     * 
     * @param in
     *            Name of the input file. Which is plain data
     * @param out
     *            Name of output file which would be an encrypted data.
     */
    public void encrypt(String in, String out) {

	try {
	    this.encrypt(new FileInputStream(in), new FileOutputStream(out));
	} catch (final FileNotFoundException exception) {
	    DQDebugHelper.printAndTrackException(this.mContext, exception);
	} catch (final Exception exception) {
	    DQDebugHelper.printAndTrackException(this.mContext, exception);
	}
    }

    /**
     * Decrypt.
     * 
     * @param in
     *            Name of the input file which is an encrypted file
     * @param out
     *            Name of file which would contain decrypted plain text.
     * @param asyncDecryptFile
     *            the async decrypt file
     */
    public void decrypt(String in, String out, DQAsyncDecryptFile asyncDecryptFile) {

	try {
	    final int lengthOfFile = (int) new File(in).length();
	    this.decrypt(new FileInputStream(in), new FileOutputStream(out),
		    asyncDecryptFile, lengthOfFile);
	} catch (final Exception exception) {
	    DQDebugHelper.printAndTrackException(this.mContext, exception);
	}

    }

    /**
     * Encrypt.
     * 
     * @param in
     *            Input stream containing plain text.
     * @param out
     *            output stream which would contain encrypted data.
     */
    public void encrypt(InputStream in, OutputStream out) {

	try {
	    // Bytes written to out will be encrypted
	    out = new CipherOutputStream(out, this.mEcipher);

	    // Read in the cleartext bytes and write to out to encrypt
	    int numRead = 0;
	    while ((numRead = in.read(this.mBuffer)) >= 0) {
		out.write(this.mBuffer, 0, numRead);
	    }
	    out.close();
	} catch (final java.io.IOException exception) {
	    DQDebugHelper.printAndTrackException(this.mContext, exception);
	}
    }

    /**
     * Decrypt.
     * 
     * @param in
     *            Input stream containing encrypted data.
     * @param out
     *            Output stream which would contain decrypted plain data.
     * @param asyncDecryptFile
     *            the async decrypt file
     * @param lengthOfFile
     *            the length of file
     */
    public void decrypt(InputStream in, OutputStream out,
	    DQAsyncDecryptFile asyncDecryptFile, int lengthOfFile) {

	try {
	    // Bytes read from in will be decrypted
	    in = new CipherInputStream(in, this.mDcipher);
	    // Read in the decrypted bytes and write the cleartext to out
	    int numRead = 0;
	    int total = 0;
	    while ((numRead = in.read(this.mBuffer)) >= 0) {
		out.write(this.mBuffer, 0, numRead);
		total += numRead;
		asyncDecryptFile.updateProgress((total * 100 / lengthOfFile));
	    }
	    out.close();
	} catch (final java.io.IOException exception) {
	    DQDebugHelper.printAndTrackException(this.mContext, exception);
	} finally {
	    asyncDecryptFile = null;
	}
    }

    /**
     * This method encrypts a byte array.
     * 
     * @param array
     *            array of bytes to be encrypted.
     * @return encrypted byte array.
     */
    public byte[] eByteArray(byte[] array) {

	try {
	    return this.mEcipher.doFinal(array);
	} catch (final IllegalBlockSizeException exception) {
	    DQDebugHelper.printAndTrackException(this.mContext, exception);
	} catch (final BadPaddingException exception) {
	    DQDebugHelper.printAndTrackException(this.mContext, exception);
	}
	return null;
    }

    /**
     * This method decrypts a byte array.
     * 
     * @param array
     *            array of bytes to be decrypted.
     * @return decrypted byte array.
     */
    public byte[] dByteArray(byte[] array) {

	try {
	    return this.mDcipher.doFinal(array);
	} catch (final IllegalBlockSizeException exception) {
	    DQDebugHelper.printAndTrackException(this.mContext, exception);
	} catch (final BadPaddingException exception) {
	    DQDebugHelper.printAndTrackException(this.mContext, exception);
	}
	return null;
    }
}