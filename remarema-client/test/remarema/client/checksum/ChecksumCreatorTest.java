package remarema.client.checksum;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;


public class ChecksumCreatorTest {
	
	private ChecksumCreator checksumCreator;

	@Test
	public void testCreateChecksum() throws NoSuchAlgorithmException, FileNotFoundException, IOException {
		File file = File.createTempFile(getClass().getSimpleName(), "tmp");
		checksumCreator = new ChecksumCreator(file);
		String checksum = checksumCreator.createChecksum();
		assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", checksum);
	}

	@Test
	public void testChecksum() throws IOException, NoSuchAlgorithmException {
		File file = File.createTempFile("test", "tmp");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write("test test test");
		bw.close();

		checksumCreator = new ChecksumCreator(file);
		String checksum = checksumCreator.createChecksum();
		assertNotSame("da39a3ee5e6b4b0d3255bfef95601890afd80709", checksum);
		// assertEquals("a715f46d41e2149a4ec4f2c0772ef5254222dd83", checksum);
	}
}
