package remarema.client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.Before;
import org.junit.Test;

import remarema.client.filerepository.FileRepository;

public class ClientTest {

	private Server server;
	private FileRepository repository;

	@Before
	public void setup() {
		server = mock(Server.class);
		repository = mock(FileRepository.class);
	}

	@Test
	public void test() throws NoSuchAlgorithmException, IOException {
		when(repository.makeFileFromPath(".")).thenReturn(new File("thisfileDoesNotExist"));
		Client client = new Client(server, repository);
		client.synchronize();
		verify(server).listFiles("aaaa");
	}

}
