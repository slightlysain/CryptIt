package slightlysain;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.picker.FilePicker;
import net.rim.device.api.util.StringProvider;

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class CryptItScreen extends MainScreen implements
		FilePicker.Listener {

	public static String NO_CRYPT_FILE = "Please select crypt file or open attachment.";
	public static String NO_IV = "Please enter IV.";
	public static String READY = "Ready for decryption.";

	private CryptIt app;
	private CryptItModel model;
	private CryptItController controller;
	private RichTextField textField;
	private MenuItem selectInputIV;
	private MenuItem selectCryptFileItem;
	private MenuItem selectDecrypt;
	private String selected;

	/**
	 * Creates a new CryptItScreen object
	 */
	public CryptItScreen(CryptItModel model) {
		this.model = model;
		model.setScreen(this);
		// Set the displayed title of the screen
		setTitle("Crypt It");
		textField = new RichTextField();
		add(textField);
		textField.setText(NO_CRYPT_FILE);
	}

	public void setController(CryptItController controller) {
		this.controller = controller;
	}

	protected void makeMenu(Menu menu, int context) {
		selectInputIV = makeMenuItem("Enter IV", 0x00010000, 1,
				new CommandHandler() {
					public void execute(ReadOnlyCommandMetadata metadata,
							Object context) {
						controller.onSelectInputIV();
					}
				});
		menu.add(selectInputIV);
		selectCryptFileItem = makeMenuItem("Select Crypt File", 0x00010001, 1,
				new CommandHandler() {
					public void execute(ReadOnlyCommandMetadata metadata,
							Object context) {
						controller.onSelectCryptFile();
					}
				});
		menu.add(selectCryptFileItem);
		selectDecrypt = makeMenuItem("Decrypt", 0x00010002, 1,
				new CommandHandler() {
					public void execute(ReadOnlyCommandMetadata metadata,
							Object context) {
						controller.onDecryptSelect();
					}
				});
		menu.add(selectDecrypt);
		super.makeMenu(menu, context);
	}

	private MenuItem makeMenuItem(String name, int ordinal, int priority,
			CommandHandler handler) {
		MenuItem menuItem = new MenuItem(new StringProvider(name), ordinal,
				priority);
		menuItem.setCommand(new Command(handler));
		return menuItem;
	}

	public void showFilePicker(String title) {
		// TODO:add filter
		FilePicker picker = FilePicker.getInstance();
		picker.setTitle(title);
		picker.setView(FilePicker.VIEW_ALL);
		picker.setListener(this);
		picker.show();
	}

	public String getSelectedFile() {
		return selected;
	}

	public void selectionDone(String selected) {
		this.selected = selected;
	}

	public void setScreenText(String txt) {
		textField.setText(txt);
	}

	public void onModelUpdate() {
		if (model.isDecrypted()) {
			String decryptedTxt = model.getDecryptedText();
			setScreenText(decryptedTxt);
		} else if (model.isReadyToDecrypt()) {
			setScreenText(READY);
		} else {
			if (null == model.getCryptFile()
					&& null == model.getEncryptedData()) {
				setScreenText(NO_CRYPT_FILE);
			} else if (!model.isIvSet()) {
				setScreenText(NO_IV);
			} else {
				setScreenText("Unknown error.");
			}
		}
	}

	public void setModel(CryptItModel cryptItModel) {
		this.model = cryptItModel;
	}

}
