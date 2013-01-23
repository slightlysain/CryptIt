package slightlysain;

import java.io.IOException;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.PasswordEditField;

public class CryptItController {
	private CryptItModel model;
	private CryptItScreen screen;
	private CryptItDecryptor decryptor;


	public CryptItController(CryptItModel model, CryptItScreen screen, CryptItDecryptor decryptor) {
		super();
		this.model = model;
		this.screen = screen;
		screen.setController(this);
		this.decryptor = decryptor;
		decryptor.setController(this);
	}


	public void onSelectInputIV() {
		Dialog dia = new Dialog(Dialog.D_OK_CANCEL, "", 0, null, 0);
		PasswordEditField passField = new PasswordEditField("IV:", "");
		dia.add(passField);
		int result = dia.doModal();
		if (Dialog.D_OK == result) {
			String password = passField.getText();
			try {
				model.setIvB64(password);
				model.setPersistIv(model.getIV());  // include in setIV or at appliucation termination
				//model.savePersistentIV;
			} catch (IOException e) {
				Dialog.alert("Problem with IV.");
			}
		}
	}
	

	public void onCryptFileOpen(String filename) {
		model.setEncryptedData(null);
		model.setCryptFile(filename);
		if (model.isReadyToDecrypt()) {
			decryptor.performDecryption();
		}
	}
	
	public void onCryptAttachmentOpen(byte[] data) {
		model.setCryptFile(null);
		model.setEncryptedData(data);
		if (model.isReadyToDecrypt()) {
			decryptor.performDecryption();
		}
	}
	
	public void onSelectCryptFile() {
		screen.showFilePicker("Choose crypt file");
		String cryptFile = screen.getSelectedFile();
		if (null != cryptFile) {
			model.setEncryptedData(null);
			model.setCryptFile(cryptFile);
		}
	}
	
	public void onDecryptSelect() {
		decryptor.performDecryption();
	}
	
}
