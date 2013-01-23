package slightlysain;

import net.rim.device.api.io.MalformedURIException;
import net.rim.device.api.io.URI;
import net.rim.device.api.io.messaging.ByteMessage;
import net.rim.device.api.io.messaging.Context;
import net.rim.device.api.io.messaging.DestinationFactory;
import net.rim.device.api.io.messaging.FireAndForgetDestination;
import net.rim.device.api.io.messaging.MessageFailureException;
import net.rim.device.api.io.messaging.MessageModificationException;
import net.rim.device.api.io.messaging.MessagingException;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.ui.component.Dialog;

public class IPCSender {
	private Context con;
	private String contextName;
	private URI uri;
	private FireAndForgetDestination fireAndForgetDest;
	private String SEND_PATH = "local://CryptIt/" + IPCReceiver.PATH;

	public IPCSender(ApplicationDescriptor appDesc)
			throws IllegalArgumentException, MalformedURIException,
			MessagingException {
		contextName = appDesc.getModuleName();
		con = new Context(contextName);
		uri = URI.create(SEND_PATH);
	
	}

	private void createFireAndForget() throws MessagingException {
		fireAndForgetDest = (FireAndForgetDestination) DestinationFactory
				.getSenderDestination(contextName, uri);
		if (null == fireAndForgetDest) {
			fireAndForgetDest = DestinationFactory
					.createFireAndForgetDestination(con, uri);
		}
	}

	public void send(String messageTxt) throws MessagingException {
		createFireAndForget();
		ByteMessage message = fireAndForgetDest.createByteMessage();
		try {
			message.setBytePayload(messageTxt.getBytes());
		} catch (MessageModificationException e1) {
			Dialog.alert("Could notmodify message");
		}
		try {
			fireAndForgetDest.sendNoResponse(message);
		} catch (MessageFailureException e) {
			Dialog.alert("could not send message");
		}
	}

	private void debug(String msg) {
		System.out.println("===================" + msg + "===================");
	}
}
