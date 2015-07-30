package remarema.client.filerepository;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Die Klasse besitzt alle notwendigen Informationen zu einem File.
 * Dazu gehört der Name des Files und das Änderungsdatum. <code>directory</code> wird benötigt, um zwischen Files und
 * Verzeichnissen unterscheiden zu können.
 * 
 * @author Rebecca van Langelaan
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FileInfo {

	private String name;
	private long lastModified;
	private boolean directory;
	private String checksum;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public boolean isInList(List<FileInfo> fileinfoList) {
		for (FileInfo e : fileinfoList) {
			if (getName().equals(e.getName())) {
				return true;
			}
		}
		return false;
	}

}
