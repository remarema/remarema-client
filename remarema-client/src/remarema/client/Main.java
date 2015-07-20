package remarema.client;

import java.util.Collection;

import org.apache.cxf.jaxrs.client.WebClient;

import remarema.p2p.FileInfo;

/**
 * 
 * @author Rebecca van Langelaan
 * 
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WebClient client = WebClient.create("http://a86008d7:58080/remarema-p2p/rest");
		client.path("/repository/filelist/");
		client.type("application/xml").accept("application/xml");
		Collection<? extends FileInfo> fileList = client.getCollection(FileInfo.class);
		for (FileInfo liste : fileList) {
			System.out.println(liste.getName() + "| " + liste.getLastModified() + "| " + liste.isDirectory());
		}
	}

}

