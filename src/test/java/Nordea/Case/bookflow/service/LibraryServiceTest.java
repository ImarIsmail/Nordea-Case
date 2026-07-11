package Nordea.Case.bookflow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import Nordea.Case.bookflow.domain.Book;
import Nordea.Case.bookflow.domain.Loan;
import Nordea.Case.bookflow.domain.LoanStatus;
import Nordea.Case.bookflow.domain.Member;
import Nordea.Case.bookflow.dto.LoanRequest;
import Nordea.Case.bookflow.repository.BookRepository;
import Nordea.Case.bookflow.repository.LoanRepository;
import Nordea.Case.bookflow.repository.MemberRepository;
import Nordea.Case.bookflow.repository.RatingRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private LoanRepository loanRepository;

	@Mock
	private RatingRepository ratingRepository;

	@InjectMocks
	private LibraryService libraryService;

	@Test
	void loanBookDecrementsAvailableCopies() {
		Book book = new Book();
		book.setId(10L);
		book.setTitle("Test Book");
		book.setAvailableCopies(2);

		Member member = new Member();
		member.setId(5L);

		Loan savedLoan = new Loan();
		savedLoan.setId(99L);
		savedLoan.setBookId(10L);
		savedLoan.setMemberId(5L);
		savedLoan.setCheckedOutAt(LocalDateTime.now());
		savedLoan.setStatus(LoanStatus.ACTIVE);

		when(memberRepository.findById(5L)).thenReturn(Mono.just(member));
		when(bookRepository.findById(10L)).thenReturn(Mono.just(book));
		when(loanRepository.findByMemberIdAndBookIdAndReturnedAtIsNull(5L, 10L)).thenReturn(Mono.empty());
		when(bookRepository.save(book)).thenReturn(Mono.just(book));
		when(loanRepository.save(org.mockito.ArgumentMatchers.any(Loan.class))).thenReturn(Mono.just(savedLoan));

		StepVerifier.create(libraryService.loanBook(new LoanRequest(5L, 10L)))
				.assertNext(response -> {
					assertThat(response.loanId()).isEqualTo(99L);
					assertThat(response.availableCopies()).isEqualTo(1);
					assertThat(response.status()).isEqualTo(LoanStatus.ACTIVE);
				})
				.verifyComplete();

		verify(bookRepository).save(book);
	}
}