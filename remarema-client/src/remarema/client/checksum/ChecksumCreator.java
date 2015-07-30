package remarema.client.checksum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class ChecksumCreator {

	private final File file;

	public ChecksumCreator(File file) {
		this.file = file;
	}

	public String createChecksum() throws NoSuchAlgorithmException, FileNotFoundException, IOException {
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

}
