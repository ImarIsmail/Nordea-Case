package Nordea.Case.bookflow.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LibraryControllerIntegrationTest {

	@LocalServerPort
	int port;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void canCreateLoanAndFetchSummary() {
		WebTestClient client = webTestClient.mutate().baseUrl("http://localhost:" + port).build();

		Map<String, Object> loanRequest = Map.of("memberId", 2, "bookId", 2);

		client.post()
				.uri("/api/loans")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(loanRequest)
				.exchange()
				.expectStatus().isCreated()
				.expectBody()
				.jsonPath("$.bookId").isEqualTo(2)
				.jsonPath("$.status").isEqualTo("ACTIVE");

		client.get()
				.uri("/api/books/1/ratings-summary?memberId=1")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.bookId").isEqualTo(1)
				.jsonPath("$.ratingCount").isEqualTo(2);
	}
}