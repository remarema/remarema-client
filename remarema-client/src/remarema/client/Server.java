package remarema.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.cxf.jaxrs.client.WebClient;

import remarema.client.filerepository.FileInfo;

/**
 * 
 * @author Rebecca van Langelaan
 * 
 */
public class Server {

	WebClient client;
	private String hostname;

	public Server(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * Die Methode {@link #listFiles(String)} lest aus einem Verzeichnis alle Dateien aus.
	 * Es wird der ein REST Webservice aufgerufen. Zuerst wird ein Webclient erstellt. Diesem muss der Hostname
	 * mitübergeben werden.
	 * Der Aufruf von {@link WebClient#getCollection(Class)} bewirkt, dass man eine Liste aller Files zurückbekommt.
	 * Danach werden die in der Collection gespeicherten Objekte in eine List vom Typ FileInfo abgelegt.
	 * 
	 * @param directory
	 * @return Liste aller Files aus dem gewünschten Verzeichnis
	 */
	public List<FileInfo> listFiles(String directory) {
		client = WebClient.create(hostname);
		client.path("/repository/" + directory);
		client.type("application/xml").accept("application/xml");
		Collection<? extends FileInfo> fileList = client.getCollection(FileInfo.class);
		List<FileInfo> liste = new ArrayList<>();
		for (FileInfo x : fileList) {
			FileInfo info = new FileInfo();
			info.setName(x.getName());
			info.setLastModified(x.getLastModified());
			info.setDirectory(x.isDirectory());
			info.setChecksum(x.getChecksum());
			liste.add(info);
		}
		return liste;
	}


	/**
	 * Diese Methode dient zur Synchronisation von ausgewählten Files.
	 * Es wird zu Beginn ein Webclient angelegt, dem der Hostname mitübergeben wird. Danach wird der Pfad gesetzt.
	 * Danach holt sich der Aufruf von {@link WebClient#get(Class)} einen InputStream.
	 * Im <code>try</code>-Block wird ein neuer Buffer mit 1024 Byte erstellt. Danach wird der inputStream gelesen und
	 * die gelesenen Bytes werden in den Buffer abgelegt. Die Anzahl der gelesenen Bytes wird in die Variable
	 * <code>len</code> gespeichert.
	 * Solange <code>len</code> ungleich <b>-1</b> ist, wird in den OutputStream geschrieben.
	 * Ist <code>len</code> gleich <b>-1</b>, so gibt es keine weiteren Bytes aus dem InputStream zum auslesen.
	 * Tritt beim Lesen oder Schreiben ein Fehler auf, wird eine {@link RuntimeException} geworfen.
	 * Zum Schluss wird noch der InputStream geschlossen.
	 * 
	 * @param filename
	 * @param outputStream
	 * @throws IOException
	 */
	public void retrieveFile(String filename, OutputStream outputStream) throws IOException {
		client = WebClient.create(hostname);
		client.path("/repository/" + filename);
		InputStream inputStream = client.get(InputStream.class);

		try {
			byte[] buffer = new byte[1024];
			int len = inputStream.read(buffer);
			while (len != -1) {
				outputStream.write(buffer, 0, len);
				len = inputStream.read(buffer);
			}
			outputStream.flush();
		} catch (IOException e) {
			throw new RuntimeException("Beim Kopieren der Datei ist ein Fehler aufgetreten", e);
		} finally {
			inputStream.close();
		}
	}



}
