package remarema.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import remarema.client.filerepository.FileInfo;
import remarema.client.filerepository.FileRepository;

/**
 * 
 * @author Rebecca van Langelaan
 * 
 */

public class Client {

	private static final Level DEBUG = Level.FINE;
	private static final Logger log = Main.getLogger();
	private FileRepository repository;
	Server server;

	/**
	 * Konstruktor. Benötigt ein {@link Server}-Objekt und das Root-Verzeichnis.
	 * 
	 * @param server
	 * @param rootDirectory
	 */
	public Client(Server server, File rootDirectory) {
		this.server = server;
		this.repository = new FileRepository(rootDirectory);
	}

	/**
	 * Die Methode <code>listFiles</code> speichert alle Verzeichnisse und Dateien aus dem lokalen FileRepository in
	 * eine Liste.
	 * 
	 * @param directory
	 * @return Eine Liste von <code>FileInfo</code>-Objekten.
	 */
	public List<FileInfo> listFiles(String directory) {
		if (repository.makeFileFromPath(directory).exists()) {
			return repository.listFiles(directory);
		}
		return Collections.emptyList();
	}

	/**
	 * Löscht ein Verzeichnis oder eine Datei anhand des Pfades.
	 * 
	 * @param path
	 */
	public void remove(String path) {
		removeFile(repository.getFile(path));
	}

	/**
	 * Wird die Methode aufgerufen wird überprüft, ob es sich um ein Verzeichnis handelt. Wenn ja, wird die Methode
	 * {@link Client#removeDirectory(File)} aufgerufen. Wenn nicht, dann wird das File gelöscht.
	 * 
	 * @param file
	 */
	private void removeFile(File file) {
		if (file.isDirectory()) {
			removeDirectory(file);
		} else {
			file.delete();
		}
	}

	/**
	 * Soll ein Verzeichnis gelöscht werden, wird die folgende Methode benötigt.
	 * Es werden alle Dateien, die sich im Verzeichnis befinden in ein Array gespeichert.
	 * Anhand einer for-Each-Schleife werden alle Files gelöscht. Anschließend wird das Verzeichnis selbst gelöscht.
	 * 
	 * @param directory
	 */
	private void removeDirectory(File directory) {
		File[] directoryContents = directory.listFiles();
		for (File file : directoryContents) {
			removeFile(file);
		}
		directory.delete();
	}

	/**
	 * Die Methode erstellt ein File anhand des als Parameter übergebenen Pfades.
	 * Danach wird die Methode {@link #makeParentDirectory(File)} aufgerufen.
	 * 
	 * @param path
	 * @return Zurückgegeben wird ein FileOutputStream.
	 * @throws IOException
	 */
	public OutputStream createOutputStream(String path) throws IOException {
		File file = repository.makeFileFromPath(path);
		makeParentDirectory(file);
		return new FileOutputStream(file);
	}

	/**
	 * Die Methode schließt einen OutputStream.
	 * 
	 * @param outputStream
	 */
	private void closeOutputStream(OutputStream outputStream) {
		try {
			outputStream.close();
		} catch (IOException e) {
			String msg = "OutputStream konnte nicht geschlossen werden";
			throw new RuntimeException(msg);
		}
	}

	/**
	 * Es wird überprüft, das Elternverzeichnis der Datei bereits existiert. Trifft dies nicht zu, wird das Verzeichnis
	 * neu angelegt.
	 * 
	 * @param file
	 */
	private void makeParentDirectory(File file) {
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
	}

	/**
	 * Diese Methode löscht veraltete Dateien. Man vergleicht die Dateiliste des Servers mit der Dateiliste des Clients.
	 * Befinden sich in der Client-Liste Verzeichnisse oder Dateien, die in der Server-Liste nicht vorhanden sind,
	 * werden diese gelöscht.
	 * 
	 * @param serverFiles
	 * @param clientFiles
	 */
	private void removeObsoleteFiles(List<FileInfo> serverFiles, List<FileInfo> clientFiles) {
		for (FileInfo clientFile : clientFiles) {
			if (!clientFile.isInList(serverFiles)) {
				remove(clientFile.getName());
			}
		}
	}

	/**
	 * Diese Methode wird in der {@link Main}-Klasse aufgerufen, um die Dateien|Verzeichnisse von zwei Clients zu
	 * vergleichen.
	 * 
	 * @throws IOException
	 */
	public void synchronize() throws IOException {
		synchronizeDirectory(".");
	}

	/**
	 * Zuerst wird die Liste der Files aus dem <code>RestRepository</code> ausgelesen.
	 * Danach wird die Liste des Clients ausgelesen.
	 * Veraltete Verzeichnisse und Dateien am Client werden gelöscht.
	 * Danach wird, je nachdem ob es sich um ein Verzeichnis oder um eine Datei handelt, das jeweilige synchronisiert.
	 * 
	 * @param directory
	 * @throws IOException
	 */
	public void synchronizeDirectory(String directory) throws IOException {
		List<FileInfo> serverFiles = server.listFiles(directory);
		List<FileInfo> clientFiles = listFiles(directory);

		removeObsoleteFiles(serverFiles, clientFiles);

		for (FileInfo serverFile : serverFiles) {
			if (serverFile.isDirectory()) {
				synchronizeDirectory(serverFile.getName());
			} else {
				synchronizeFile(serverFile);
			}
		}
	}

	/**
	 * Diese Methode synchronisiert Files. Es wird zu Beginn die Methode {@link #isFileUpToDate(FileInfo)} aufgerufen.
	 * Wird <b>false</b> zurückgegeben, wird der Filename ausgelesen, um diesen an einen OutputStream übergeben zu
	 * können.
	 * Danach wird die Methode {@link Server#retrieveFile(String, OutputStream)} aufgerufen, um das entsprechende File
	 * zu synchronisieren.
	 * 
	 * @param fileInfo
	 * @throws IOException
	 * 
	 */
	private void synchronizeFile(FileInfo fileInfo) throws IOException {
		if (!isFileUpToDate(fileInfo)) {
			long lastModified = fileInfo.getLastModified();
			String fileName = fileInfo.getName();
			log.info(fileInfo.getName() + " wird synchronisiert.");

			OutputStream outputStream = createOutputStream(fileInfo.getName() + ".part");
			try {
				server.retrieveFile(fileName, outputStream);
			} finally {
				closeOutputStream(outputStream);
				File temp = repository.getFile(fileInfo.getName() + ".part");
				File currentFile = repository.makeFileFromPath(fileInfo.getName());
				if (currentFile.exists()) {
					removeFile(currentFile);
				}
				temp.setLastModified(lastModified);
				temp.renameTo(currentFile);

			}
		} else {
			if (log.isLoggable(DEBUG)) {
				log.log(DEBUG, fileInfo.getName() + " ist aktuell. Datei wird nicht synchronisiert.");
			}
		}
	}

	/**
	 * Bei der Methode wird ein <code>FileInfo</code>-Objekt als Parameter übergeben.
	 * Es wird ein neues File-Objekt erstellt. Existiert dieses File bereits im Verzeichnis, müssen
	 * das Änderungsdatum der beiden zu synchronisierenden Files miteinander verglichen werden.
	 * 
	 * @param fileInfo
	 * @return
	 *         true - wenn das Änderungsdatum übereinstimmt. </br>
	 *         false - wenn das Änderungsdatum unterschiedlich ist.
	 */
	public boolean isFileUpToDate(FileInfo fileInfo) {
		File file = repository.makeFileFromPath(fileInfo.getName());
		if (file.exists()) {
			if (fileInfo.getLastModified() == file.lastModified()) {
				return true;
			}
		}
		return false;
	}

}
