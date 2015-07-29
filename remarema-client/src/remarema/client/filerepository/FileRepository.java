package remarema.client.filerepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Rebecca van Langelaan
 * 
 */
public class FileRepository {

	private File rootDirectory;

	public FileRepository(File rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	/**
	 * Diese Methode dient dazu, ein File in ein <code>FileInfo</code>-Objekt umzuwandeln.
	 * Dazu wird bei dem Objekt der Name und das Änderungsdatum gespeichert. Außerdem wird mit
	 * {@link FileInfo#setDirectory(boolean)} angegeben, ob es sich um ein Verzeichnis handelt. Bei <b>false</b> handelt
	 * es
	 * sich um eine Datei.
	 * 
	 * @param file
	 * @return Zurückgegeben wird ein Objekt vom Typ <code>FileInfo</code>.
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public FileInfo createFileInfoFromFile(File file) throws NoSuchAlgorithmException, IOException {
		FileInfo info = new FileInfo();
		info.setName(createRelativeFileName(file));
		info.setLastModified(file.lastModified());
		info.setDirectory(file.isDirectory());
		if (!file.isDirectory()) {
			info.setChecksum(createChecksum(file));
		}
		return info;
	}

	public String createChecksum(File file) throws NoSuchAlgorithmException, FileNotFoundException, IOException {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		FileInputStream fis = new FileInputStream(file);
		byte[] dataBytes = new byte[1024];
		int nread = 0;
		do {
			nread = fis.read(dataBytes);
			if (nread > 0) {
				md.update(dataBytes, 0, nread);
			}
		} while (nread != -1);
		fis.close();

		byte[] mdbytes = md.digest();

		// convert the byte to hex format
		return formatChecksumToHex(mdbytes);
	}

	private String formatChecksumToHex(byte[] mdbytes) {
		String result = "";
		for (int i = 0; i < mdbytes.length; i++) {
			result += Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	/**
	 * Diese Methode erzeugt anhand des übergebenen Files einen relativen und systemunabhängigen Pfad.
	 * 
	 * @param file
	 * @return Es wird der relative Filename einer Datei zurückgegeben.
	 */
	private String createRelativeFileName(File file) {
		String rootURI = rootDirectory.toURI().toString();
		String fileURI = file.toURI().toString();
		String name = fileURI.substring(rootURI.length());
		return name;
	}

	/**
	 * Die Methode <code>getFile(path)</code> ruft zuerst die Methode {@link FileRepository#makeFileFromPath(String)}
	 * auf.
	 * Es wird überprüft, ob es sich um ein File handelt. Wenn ja wird dieses zurückgegeben. Andernfalls wird eine
	 * {@link IllegalArgumentException} geworfen.
	 * 
	 * @param path
	 * @throws IllegalArgumentException
	 * @return Wenn es sich um ein File handelt, wird dieses zurückgegeben, andernfalls wird eine Exception geworfen.
	 */
	public File getFile(String path) {
		File file = makeFileFromPath(path);
		if (file.isFile()) {
			return file;
		}
		String msg = "path not a valid file: " + path;
		throw new IllegalArgumentException(msg);
	}

	/**
	 * Die Methode liefert das Wurzelverzeichnis zurück.
	 * 
	 * @return das Wurzelverzeichnis
	 */
	public File getRootDirectory() {
		return rootDirectory;
	}

	/**
	 * Diese Methode erzeugt aus einem relativen Pfad ein {@link File}-Objekt, welches auf ein Unterverzeichnis
	 * innerhalb des Repositories zeigt.
	 * 
	 * @param path
	 * @throws IllegalArgumentException
	 *             Wird geworfen, wenn der Pfad kein Unterverzeichnis innerhalb des Repositories ist.
	 * @return Ein {@link File}-Objekt, welches ein Unterverzeichnis im Repository darstellt.
	 */
	public File getSubdirectory(String path) {
		File subdirectory = makeFileFromPath(path);
		if (subdirectory.isDirectory()) {
			return subdirectory;
		}
		String msg = "path not a valid directory: " + subdirectory;
		throw new IllegalArgumentException(msg);
	}

	/**
	 * Die Methode legt ein neues File an. Der Pfad für dieses File muss mitübergeben werden.
	 * 
	 * @param path
	 * @return
	 */
	public File makeFileFromPath(String path) {
		return new File(rootDirectory, path);
	}

	/**
	 * Die Methode <listFiles(directory)</code> dient dazu, alle Files eines Verzeichnisses bzw. deren
	 * Unterverzeichnisse in eine Liste zu speichern.
	 * 
	 * @param directory
	 * @return Liste von <code>FileInfo</code>-Objekten
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public List<FileInfo> listFiles(String directory) throws NoSuchAlgorithmException, IOException {
		File subdirectory = getSubdirectory(directory);
		File[] sourceFiles = subdirectory.listFiles();
		List<FileInfo> fileList = new ArrayList<>();
		for (File file : sourceFiles) {
			if (!file.getName().endsWith(".part")) {
				fileList.add(createFileInfoFromFile(file));
			}
		}
		return fileList;
	}
}
