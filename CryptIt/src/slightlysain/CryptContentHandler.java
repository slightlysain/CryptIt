package slightlysain;

import javax.microedition.content.ContentHandler;
import javax.microedition.content.ContentHandlerException;
import javax.microedition.content.ContentHandlerServer;
import javax.microedition.content.Invocation;
import javax.microedition.content.Registry;
import javax.microedition.content.RequestListener;

import net.rim.device.api.content.DefaultContentHandlerRegistry;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.ui.component.Dialog;

public class CryptContentHandler implements RequestListener {
	// private CryptIt theApp;
	private String classname;
	private String[] types = { "application/cryptit" };
	private String[] suffixes = { ".crypt" };
	private String[] actions = { ContentHandler.ACTION_OPEN };
	private String handlerName = "Crypt File Handler";

	public CryptContentHandler() {
		classname = this.getClass().getName();
	}

	public void register() throws SecurityException, IllegalArgumentException,
			ContentHandlerException, ClassNotFoundException {
		Registry registry = Registry.getRegistry(classname);
		// is classname ok
		registry.register(classname, types, suffixes, actions, null, classname,
				null);
		ContentHandlerServer server = Registry.getServer(classname);
		server.setListener(this);
		DefaultContentHandlerRegistry defConHandReg = DefaultContentHandlerRegistry
				.getDefaultContentHandlerRegistry(registry);
		ApplicationDescriptor currentDescriptor = ApplicationDescriptor
				.currentApplicationDescriptor();
		ApplicationDescriptor descriptor = new ApplicationDescriptor(
				currentDescriptor, handlerName, null);
		defConHandReg.setApplicationDescriptor(descriptor, classname);

	}

	public void unregister() throws ContentHandlerException {

		final ContentHandlerServer contentHandlerServer = Registry
				.getServer(classname);
		contentHandlerServer.setListener(null);

		Registry registry = Registry.getRegistry(classname);
		registry.unregister(classname);
	}

	public void invocationRequestNotify(ContentHandlerServer server) {

		// Retrieve Invocation from the content handler server
		Invocation invoc = server.getRequest(false);

		if (invoc == null) {
			return; // Nothing to do
		}

		int invocationStatus = invoc.getStatus();

		try {
			Registry registry = Registry.getRegistry(classname);
			DefaultContentHandlerRegistry defaultRegistry = DefaultContentHandlerRegistry
					.getDefaultContentHandlerRegistry(registry);
			ApplicationDescriptor descriptor = defaultRegistry
					.getApplicationDescriptor(server.getID());

			// Dialog.alert(descriptor.getName() + " invoked for: "
			// + invoc.getURL());

			String fileURL = invoc.getURL();
			CryptIt theApp = (CryptIt) Application.getApplication();
			CryptItController controller = theApp.getController();
			controller.onCryptFileOpen(fileURL);	
			invocationStatus = Invocation.OK;
		} finally {
			server.finish(invoc, invocationStatus);
		}
	}

}
