package Nordea.Case.bookflow.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import Nordea.Case.bookflow.dto.BookRatingSummary;
import Nordea.Case.bookflow.dto.LoanRequest;
import Nordea.Case.bookflow.dto.LoanResponse;
import Nordea.Case.bookflow.dto.RatingRequest;
import Nordea.Case.bookflow.dto.RatingResponse;
import Nordea.Case.bookflow.dto.ReturnLoanRequest;
import Nordea.Case.bookflow.service.LibraryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
// Thin WebFlux controller that exposes the API endpoints and forwards requests to the service layer.
public class LibraryController {

	private final LibraryService libraryService;

	public LibraryController(LibraryService libraryService) {
		this.libraryService = libraryService;
	}

	@PostMapping("/loans")
	public Mono<ResponseEntity<LoanResponse>> loanBook(@Valid @RequestBody LoanRequest request) {
		// In a real application the member id would come from the authenticated principal instead of the request body/HTTP headers.
		return libraryService.loanBook(request).map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
	}

	@PostMapping("/loans/{loanId}/return")
	public Mono<ResponseEntity<LoanResponse>> returnBook(@PathVariable Long loanId,
			@Valid @RequestBody ReturnLoanRequest request) {
		return libraryService.returnBook(loanId, request).map(ResponseEntity::ok);
	}

	@PostMapping("/ratings")
	public Mono<ResponseEntity<RatingResponse>> rateBook(@Valid @RequestBody RatingRequest request) {
		return libraryService.rateBook(request).map(ResponseEntity::ok);
	}

	@GetMapping("/members/{memberId}/loans")
	public Flux<LoanResponse> getCurrentLoans(@PathVariable Long memberId) {
		return libraryService.getActiveLoans(memberId);
	}

	@GetMapping("/books/{bookId}/ratings-summary")
	public Mono<BookRatingSummary> getRatingSummary(@PathVariable Long bookId,
			@RequestParam(required = false) Long memberId) {
		return libraryService.getRatingSummary(bookId, memberId);
	}
}