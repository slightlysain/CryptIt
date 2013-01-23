package slightlysain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.crypto.AESCBCDecryptorEngine;
import net.rim.device.api.crypto.AESKey;
import net.rim.device.api.crypto.BlockDecryptor;
import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.InitializationVector;
import net.rim.device.api.crypto.PKCS5UnformatterEngine;
import net.rim.device.api.crypto.TripleDESCBCDecryptorEngine;
import net.rim.device.api.crypto.TripleDESKey;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.DataBuffer;

public class CryptItDecryptor {
	private CryptItModel model;
	private CryptItController controller;

	public CryptItDecryptor(CryptItModel model) {
		this.model = model;
	}

	public void setController(CryptItController controller) {
		this.controller = controller;
	}

	private InputStream openFile() throws IOException {
		FileConnection fconn = (FileConnection) Connector.open(model
				.getCryptFile());
		if (!fconn.exists()) {
			throw new IOException("File does not exist"); // TODO:lookup
															// specific
															// exception
		}
		return fconn.openInputStream();
	}

	private BlockDecryptor createDecryptor(AESKey key,
			InputStream inputStream) throws CryptoTokenException,
			CryptoUnsupportedOperationException {
		InitializationVector iv = new InitializationVector(new byte[16]);
		AESCBCDecryptorEngine decryptorEngine = new AESCBCDecryptorEngine(
				key, iv);
		PKCS5UnformatterEngine unformatterEngine = new PKCS5UnformatterEngine(
				decryptorEngine);
		BlockDecryptor decryptor = new BlockDecryptor(unformatterEngine,
				inputStream);
		return decryptor;
	}

	private String decryptStream(BlockDecryptor decryptor) throws IOException {
		DataBuffer dataBuffer = new DataBuffer();
		byte[] temp = new byte[100];
		int readCount;
		while (-1 != (readCount = decryptor.read(temp))) {
			dataBuffer.write(temp, 0, readCount);
		}
		return new String(dataBuffer.getArray());
	}

	public void performDecryption() {
		if (!model.isIvSet()) {
			Dialog.alert("IV not set");
			return;
		} else if (null == model.getCryptFile()
				&& null == model.getEncryptedData()) {
			Dialog.alert("No encrypted data");
			return;
		}
		if (model.getIV().length < 24) {
			Dialog.alert("Invalid IV");
			return;
		}

		String decryptedText = "error";

		try {
			InputStream encrytpedInputStream = null;
			if (null != model.getCryptFile()) {
				encrytpedInputStream = openFile();
			} else if (null != model.getEncryptedData()) {
				encrytpedInputStream = new ByteArrayInputStream(
						model.getEncryptedData());
			} else {
				Dialog.alert("error decrypting data no data or file");
				return;
			}
			Base64InputStream inputStream = new Base64InputStream(
					encrytpedInputStream);
			AESKey key = new AESKey(model.getIV());
			BlockDecryptor decryptor = createDecryptor(key, inputStream);
			decryptedText = decryptStream(decryptor);
		} catch (IOException e) {
			Dialog.alert("IOException" + e);
			return;
		} catch (CryptoTokenException e) {
			Dialog.alert("CryptoTokenException" + e);
			return;
		} catch (CryptoUnsupportedOperationException e) {
			Dialog.alert("CryptoUnsupportedOperationException" + e);
			return;
		}
		model.setDecryptedText(decryptedText);
		// screen.setScreenText(decryptedText);
	}

}
