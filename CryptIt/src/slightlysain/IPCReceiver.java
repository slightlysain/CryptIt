package slightlysain;

import net.rim.device.api.io.MalformedURIException;
import net.rim.device.api.io.URI;
import net.rim.device.api.io.messaging.ByteMessage;
import net.rim.device.api.io.messaging.Destination;
import net.rim.device.api.io.messaging.DestinationFactory;
import net.rim.device.api.io.messaging.InboundDestinationConfiguration;
import net.rim.device.api.io.messaging.InboundDestinationConfigurationFactory;
import net.rim.device.api.io.messaging.Message;
import net.rim.device.api.io.messaging.MessageFailureException;
import net.rim.device.api.io.messaging.MessageListener;
import net.rim.device.api.io.messaging.MessagingException;
import net.rim.device.api.io.messaging.NonBlockingReceiverDestination;
import net.rim.device.api.io.messaging.NonBlockingSenderDestination;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;

public final class IPCReceiver {

	private static final boolean AUTO_START = false;
	public static String PATH = "email_attachment";
	private NonBlockingReceiverDestination nonBlockSendDest;
	private IPCMessageListener iPCListener;

	public IPCReceiver(CryptItController controller)
			throws IllegalArgumentException, MessagingException,
			MalformedURIException {

		URI uri = URI.create("local:///" + PATH);
		nonBlockSendDest = (NonBlockingReceiverDestination) DestinationFactory
				.getReceiverDestination(uri);

		if (null == nonBlockSendDest) {
			InboundDestinationConfiguration inDestConfig = InboundDestinationConfigurationFactory
					.createIPCConfiguration(AUTO_START, true, false);
			iPCListener = new IPCMessageListener(controller);
			nonBlockSendDest = DestinationFactory
					.createNonBlockingReceiverDestination(inDestConfig, uri,
							iPCListener);
		}
	}

	private final static class IPCMessageListener implements MessageListener {
		private CryptItController controller;

		public IPCMessageListener(CryptItController controller) {
			this.controller = controller;
		}

		public void onMessage(Destination destination, final Message message) {
			Application.getApplication().invokeLater(new Runnable() {
				public void run() {
					if (message instanceof ByteMessage) {
						ByteMessage byteMsg = (ByteMessage) message;
						byte[] data = byteMsg.getBytePayload();
						controller.onCryptAttachmentOpen(data);
					}
					//Dialog.alert("Message received" + contentTxt);
				}
			});
		}

		public void onMessageCancelled(Destination destination,
				int cancelledMessageId) {
			Application.getApplication().invokeLater(new Runnable() {
				public void run() {
					Dialog.alert("Cancelled");
				}
			});

		}

		public void onMessageFailed(Destination destination,
				MessageFailureException exception) {
			Application.getApplication().invokeLater(new Runnable() {
				public void run() {
					Dialog.alert("Message Failed");
				}
			});

		}

	}

}
