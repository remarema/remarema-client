package remarema.client;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * 
 * @author Rebecca van Langelaan
 * 
 */
public class Main {

	private static final String HOSTNAME = "http://localhost:8080/remarema-p2p/rest";
	final static String DIRECTORY = "C:\\Users\\Rebecca vanLangelaan\\Desktop\\ARZ\\Client";
	private static final String LOGGER_NAME = "remarema-client";

	/**
	 * @param args
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		// legt den kleinsten log level fest
		Level minimumLogLevel = Level.FINE;

		// Mindestlevel an unseren Client Logger weitergeben.
		Logger logger = getLogger();
		logger.setLevel(minimumLogLevel);

		// Mindestlevel an alle Log-Handler weitergeben.
		Logger root = Logger.getLogger("");
		Handler[] handlers = root.getHandlers();
		for (Handler handler : handlers) {
			handler.setLevel(minimumLogLevel);
		}

		Server server = new Server(HOSTNAME);
		Client client = new Client(server, new File(DIRECTORY));
		client.synchronize();
	}

	/**
	 * Zentraler Logger für alle Klassen der Client Bibliothek
	 * 
	 * @return Logger
	 */
	public static Logger getLogger() {
		return Logger.getLogger(LOGGER_NAME);
	}

}

