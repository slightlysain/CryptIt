package slightlysain;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import net.rim.device.api.crypto.BlockDecryptor;
import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.PKCS5UnformatterEngine;
import net.rim.device.api.crypto.TripleDESDecryptorEngine;
import net.rim.device.api.crypto.TripleDESKey;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

public class CryptItModel {
	private byte[] iV = new byte[24];
	private boolean isIVSet = false;;
	private String cryptFile;
	private String decrypted = null;
	private byte[] encryptedData;
	private PersistentObject persist = null;
	private static long IV_KEY = "eke.joshua.CryptIt".hashCode();
	private CryptItScreen screen;

	public CryptItModel() {

	}

	public void init() {
		initPersist();
	}

	public void setEncryptedData(byte[] d) {
		encryptedData = d;
		onModelChanged();
	}

	public byte[] getEncryptedData() {
		return encryptedData;
	}

	public void setScreen(CryptItScreen screen) {
		this.screen = screen;
	}

	public void setIv(byte[] by) {
		iV = by;
		isIVSet = true;
		onModelChanged();
	}

	private void onModelChanged() {
		if (null != screen) {
			screen.onModelUpdate();
		}
	}

	public boolean isIvSet() {
		return isIVSet;
	}

	private void initPersist() {
		if (null == persist) {
			persist = PersistentStore.getPersistentObject(IV_KEY);
		}
		byte[] iv = getPersistIv();
		if (null != iv) {
			setIv(iv);
		}
	}

	public byte[] getPersistIv() {
		byte[] data = (byte[]) persist.getContents();
		if (null != data) {
			return data;
		}
		return null;
	}

	public void setPersistIv(byte[] iv) {
		persist.setContents(iv);
		persist.commit();
	}

	public void setIvB64(String iv) throws IOException {
		setIv(Base64InputStream.decode(iv));
	}

	public void setCryptFile(String cf) {
		cryptFile = cf;
		onModelChanged();
	}

	public String getCryptFile() {
		return cryptFile;
	}

	public byte[] getIV() {
		return iV;
	}

	public void setDecryptedText(String txt) {
		decrypted = txt;
		onModelChanged();
	}

	public String getDecryptedText() {
		return decrypted;
	}
	
	public boolean isDecrypted() {
		return null != decrypted;
	}

	public boolean isReadyToDecrypt() {
		return (isIvSet() && ((null != getCryptFile()) || null != getEncryptedData()));
	}

}
