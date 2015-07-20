package remarema.client;

import java.util.Collection;

import javax.xml.ws.WebServiceClient;

import org.apache.cxf.jaxrs.client.WebClient;

import remarema.p2p.FileInfo;

@WebServiceClient
public class Main {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WebClient client = WebClient.create("http://a86008d7:58080/remarema-p2p/rest");
		client.path("/repository/filelist/");
		client.type("application/xml").accept("application/xml");
		Collection<? extends FileInfo> x = client.getCollection(FileInfo.class);
		for (FileInfo liste : x) {
			System.out.println(liste.getName() + "| " + liste.getLastModified() + "| " + liste.isDirectory());
		}
	}



}
