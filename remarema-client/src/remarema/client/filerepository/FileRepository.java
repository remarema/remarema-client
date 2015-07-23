package remarema.client.filerepository;

import java.io.File;
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

	public FileInfo createFileInfoFromFile(File file) {
		FileInfo info = new FileInfo();
		info.setName(createRelativeFileName(file));
		info.setLastModified(file.lastModified());
		info.setDirectory(file.isDirectory());

		return info;
	}

	private String createRelativeFileName(File file) {
		String rootURI = rootDirectory.toURI().toString();
		String fileURI = file.toURI().toString();
		String name = fileURI.substring(rootURI.length());
		return name;
	}

	public File getFile(String path) {
		File file = makeFileFromPath(path);
		if (file.isFile()) {
			return file;
		}
		String msg = "path not a valid file: " + path;
		throw new IllegalArgumentException(msg);
	}

	public File getRootDirectory() {
		return rootDirectory;
	}

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

	public List<FileInfo> listFiles(String directory) {
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
