package remarema.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.cxf.jaxrs.client.WebClient;

import remarema.client.filerepository.FileInfo;


public class Server {

	WebClient client;
	private String hostname;

	public Server(String hostname) {
		this.hostname = hostname;
	}

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
			liste.add(info);
		}
		return liste;
	}


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
