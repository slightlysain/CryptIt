package slightlysain;

import java.io.InputStream;

import net.rim.blackberry.api.mail.AttachmentHandler;
import net.rim.blackberry.api.mail.AttachmentHandlerManager;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.SupportedAttachmentPart;
import net.rim.device.api.io.MalformedURIException;
import net.rim.device.api.io.messaging.MessagingException;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationManagerException;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.DataBuffer;

public class CryptItAttachmentHandler implements AttachmentHandler {
	private static String FILE_EXT = ".crypt";
	String stuff = "NONE";
	private ApplicationDescriptor appDesc;
	private String appName;
	private IPCSender sender = null;

	public CryptItAttachmentHandler(ApplicationDescriptor app) {
		this.appDesc = new ApplicationDescriptor(app, app.getModuleName(),
				new String[0]);
	}

	private void createIPC() {
		//TODO: convert to use singleton.?
		//does not create new fireandforgetsender if it exists
		try {
			this.sender = new IPCSender(appDesc);
		} catch (IllegalArgumentException e) {
			debug(e);
		} catch (MalformedURIException e) {
			debug(e);
		} catch (MessagingException e) {
			debug(e);
		} catch (Exception e) {
			debug(e);
		}
	}

	public void register() {
		AttachmentHandlerManager manager = AttachmentHandlerManager
				.getInstance();
		manager.addAttachmentHandler(this);
	}

	public String menuString() {
		return "Decrypt File";
	}

	public void run(Message msg, SupportedAttachmentPart attachment) {
		System.out
				.println("=================RUN-ATTACHMENT=====================");
		//attachment.getSize()
		if(!hasContent(attachment)) {
			//download content
		}
		
		Object o = attachment.getContent();
		byte[] content = (byte[]) o;
		System.out
		.println("=================CON:" + content.length + "=====================");
		System.out
		.println("=================Content:" + o + "=====================");
		System.out
		.println("=================SIZE:" + attachment.getSize() + "=====================");
		
		runApplication();
		createIPC();
		String s = new String((byte[])attachment.getContent());
		send(s);
	}
	
	private boolean hasContent(SupportedAttachmentPart attachment) {
		byte[] content = (byte[]) attachment.getContent();
		return content.length > 0;
	}

	private void send(String s) {
		try {
			sender.send(s);
		} catch (MessagingException e) {
			Dialog.alert("Message exception when sending IPC");
		}
	}

	private void runApplication() {
		try {
			ApplicationManager.getApplicationManager().runApplication(appDesc);
		} catch (ApplicationManagerException e) {
			Dialog.alert("HasApp problem");
		}
	}

	public boolean supports(String attachmentname) {
		return attachmentname.endsWith(FILE_EXT);
	}

	private void debug(Exception e) {
		System.out.println("===================" + e.getMessage()
				+ "===================");
	}

}
