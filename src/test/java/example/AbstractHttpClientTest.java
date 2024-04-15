package example;

import static org.mockserver.model.HttpRequest.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockserver.configuration.Configuration;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.ConnectionOptions;
import org.mockserver.model.HttpResponse;
import org.slf4j.event.Level;

public abstract class AbstractHttpClientTest {
	protected final static String URL = "http://localhost:1080";
	protected final static String PATH = "/api/v1/test";

	protected static ClientAndServer mockServer;

	@BeforeAll
	static void beforeAll() {
		Configuration configuration = new Configuration();
		configuration.logLevel(Level.TRACE);

		mockServer = ClientAndServer.startClientAndServer(configuration, 1080);
	}

	@AfterAll
	static void afterAll() {
		mockServer.stop();
	}

	protected void givenResponse200WithCloseSocket() {
		mockServer.when(
			request(URL)
				.withPath(PATH)
		).respond(HttpResponse.response()
			.withConnectionOptions(ConnectionOptions.connectionOptions().withCloseSocket(true))
			.withStatusCode(200)
		);
	}
}
