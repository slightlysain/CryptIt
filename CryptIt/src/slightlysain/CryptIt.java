package slightlysain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.content.ContentHandlerException;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.crypto.BlockDecryptor;
import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.InitializationVector;
import net.rim.device.api.crypto.PKCS5UnformatterEngine;
import net.rim.device.api.crypto.TripleDESCBCDecryptorEngine;
import net.rim.device.api.crypto.TripleDESKey;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.io.MalformedURIException;
import net.rim.device.api.io.messaging.MessagingException;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.util.DataBuffer;

/**
 * This class extends the UiApplication class, providing a graphical user
 * interface.
 */
public class CryptIt extends UiApplication {
	private CryptItScreen screen;
	private CryptItModel model;
	private CryptItController controller;
	private CryptItDecryptor decryptor;
	private static CryptItAttachmentHandler cryptAttachmentHandler;
	private static CryptContentHandler cryptContentHandler;
	private static CryptIt cryptItApp;

	/**
	 * Entry point for application
	 * 
	 * @param args
	 *            Command line arguments (not used)
	 */
	public static void main(String[] args) {
		if (args != null && args.length > 0) {
			if (args[0].equals("startup")) {
				register();
			}
		} else {
			cryptItApp = new CryptIt();
			cryptItApp.enterEventDispatcher();
		}
	}

	public static void register() {
		registerAttachmentHandler();
		registerContentHandler();
	}

	private static void registerAttachmentHandler() {
		ApplicationDescriptor appdesc = ApplicationDescriptor
				.currentApplicationDescriptor();
		cryptAttachmentHandler = new CryptItAttachmentHandler(appdesc);
		cryptAttachmentHandler.register();
	}

	private static void registerContentHandler() {
		cryptContentHandler = new CryptContentHandler();
		try {
			cryptContentHandler.register();
		} catch (SecurityException e) {
			alert(e);
		} catch (IllegalArgumentException e) {
			alert(e);
		} catch (ContentHandlerException e) {
			alert(e);
		} catch (ClassNotFoundException e) {
			alert(e);
		}
	}

	public static void alert(Exception e) {
		System.out.println("=============" + e.getMessage());

		Dialog.alert("!Alert " + e.getMessage());
	}

	/**
	 * Creates a new CryptIt object
	 */
	public CryptIt() {
		model = new CryptItModel();
		model.init();
		screen = new CryptItScreen(model);
		decryptor = new CryptItDecryptor(model);
		controller = new CryptItController(model, screen, decryptor);
		try {
			new IPCReceiver(controller);
		} catch (IllegalArgumentException e) {
			alert(e);
		} catch (MessagingException e) {
			alert(e);
		} catch (MalformedURIException e) {
			alert(e);
		}
		pushScreen(screen);
	}

	public CryptItController getController() {
		return controller;
	}

}
