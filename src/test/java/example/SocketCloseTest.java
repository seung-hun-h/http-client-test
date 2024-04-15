package example;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.apache.hc.client5.http.fluent.Executor;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.NoHttpResponseException;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.OkHttpClient;

public class SocketCloseTest extends AbstractHttpClientTest {

	@BeforeEach
	void setUp() {
		givenResponse200WithCloseSocket();
	}

	@Test
	void okHttpClient() throws IOException {
		okhttp3.Request request = new okhttp3.Request.Builder()
			.url(URL + PATH)
			.build();

		OkHttpClient okHttpClient = new OkHttpClient();

		okHttpClient.newCall(request).execute().close();
		assertDoesNotThrow(() -> okHttpClient.newCall(request).execute().close());
	}

	@Test
	void jdkHttpClient() throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder(URI.create(URL + PATH))
			.GET()
			.build();

		try (HttpClient client = HttpClient.newBuilder()
			.build()) {

			client.send(request, HttpResponse.BodyHandlers.ofString());

			HttpResponse<String> result = assertDoesNotThrow(() -> client.send(request, HttpResponse.BodyHandlers.ofString()));

			System.out.println("result = " + result);
		}
	}

	@Test
	void apacheHttpClient() throws IOException {
		// given
		CloseableHttpClient closeableHttpClient = HttpClientBuilder.create()
			.setConnectionManager(
				PoolingHttpClientConnectionManagerBuilder.create()
					.useSystemProperties()
					.setMaxConnPerRoute(1)
					.setMaxConnTotal(1)
					.setDefaultSocketConfig(
						SocketConfig.custom()
							.setSoTimeout(Timeout.ofSeconds(1L))
							.build()
					)
					.build()
			)
			.useSystemProperties()
			.evictExpiredConnections()
			.evictIdleConnections(TimeValue.ofMinutes(1L))
			.build();

		Executor executor = Executor.newInstance(closeableHttpClient);

		executor.execute(Request.create(Method.GET.name(), URL + PATH)
			.bodyStream(InputStream.nullInputStream()));

		// when & then
		assertThrows(NoHttpResponseException.class, () -> executor.execute(Request.create(Method.GET.name(), URL + PATH)
			.bodyStream(InputStream.nullInputStream())));
	}
}
