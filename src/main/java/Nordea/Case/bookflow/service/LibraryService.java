package Nordea.Case.bookflow.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Nordea.Case.bookflow.domain.Book;
import Nordea.Case.bookflow.domain.Loan;
import Nordea.Case.bookflow.domain.LoanStatus;
import Nordea.Case.bookflow.domain.Member;
import Nordea.Case.bookflow.domain.Rating;
import Nordea.Case.bookflow.dto.BookRatingSummary;
import Nordea.Case.bookflow.dto.LoanRequest;
import Nordea.Case.bookflow.dto.LoanResponse;
import Nordea.Case.bookflow.dto.RatingRequest;
import Nordea.Case.bookflow.dto.RatingResponse;
import Nordea.Case.bookflow.dto.ReturnLoanRequest;
import Nordea.Case.bookflow.exception.BadRequestException;
import Nordea.Case.bookflow.exception.NotFoundException;
import Nordea.Case.bookflow.repository.BookRepository;
import Nordea.Case.bookflow.repository.LoanRepository;
import Nordea.Case.bookflow.repository.MemberRepository;
import Nordea.Case.bookflow.repository.RatingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class LibraryService {

	private final BookRepository bookRepository;
	private final MemberRepository memberRepository;
	private final LoanRepository loanRepository;
	private final RatingRepository ratingRepository;

	public LibraryService(BookRepository bookRepository, MemberRepository memberRepository, LoanRepository loanRepository,
			RatingRepository ratingRepository) {
		this.bookRepository = bookRepository;
		this.memberRepository = memberRepository;
		this.loanRepository = loanRepository;
		this.ratingRepository = ratingRepository;
	}

	@Transactional
	public Mono<LoanResponse> loanBook(LoanRequest request) {
		// Validates then decrements inventory then creates the loan.
		return validateMemberAndBook(request.memberId(), request.bookId())
				.flatMap(tuple -> {
					Member member = tuple.member();
					Book book = tuple.book();

					if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
						return Mono.error(new BadRequestException("No copies of the book are currently available."));
					}

					return loanRepository.findByMemberIdAndBookIdAndReturnedAtIsNull(member.getId(), book.getId())
							.flatMap(existing -> Mono.<LoanResponse>error(
									new BadRequestException("This member already has an active loan for this book.")))
							.switchIfEmpty(Mono.defer(() -> {
								book.setAvailableCopies(book.getAvailableCopies() - 1);
								Loan loan = new Loan();
								loan.setBookId(book.getId());
								loan.setMemberId(member.getId());
								loan.setCheckedOutAt(LocalDateTime.now());
								loan.setStatus(LoanStatus.ACTIVE);

								return bookRepository.save(book)
										.then(loanRepository.save(loan))
										.map(savedLoan -> toLoanResponse(savedLoan, book));
							}));
				});
	}

	// Load the loan, then use its book reference to restore inventory and close the loan.
	@Transactional
	public Mono<LoanResponse> returnBook(Long loanId, ReturnLoanRequest request) {
		return loanRepository.findById(loanId)
				.switchIfEmpty(Mono.error(new NotFoundException("Loan not found.")))
				.flatMap(loan -> {
					if (!loan.getMemberId().equals(request.memberId())) {
						return Mono.error(new BadRequestException("This loan does not belong to the supplied member."));
					}
					if (loan.getReturnedAt() != null) {
						return Mono.error(new BadRequestException("This loan has already been returned."));
					}

					return bookRepository.findById(loan.getBookId())
							.switchIfEmpty(Mono.error(new NotFoundException("Book not found.")))
							.flatMap(book -> {
								book.setAvailableCopies(book.getAvailableCopies() + 1);
								loan.setReturnedAt(LocalDateTime.now());
								loan.setStatus(LoanStatus.RETURNED);
								return bookRepository.save(book)
										.then(loanRepository.save(loan))
										.map(savedLoan -> toLoanResponse(savedLoan, book));
							});
				});
	}

	// Ratings are upserted per member/book pair so the latest score wins/persists.
	@Transactional
	public Mono<RatingResponse> rateBook(RatingRequest request) {
		return validateMemberAndBook(request.memberId(), request.bookId())
				.flatMap(tuple -> {
					Book book = tuple.book();
					return ratingRepository.findByMemberIdAndBookId(request.memberId(), request.bookId())
							.defaultIfEmpty(new Rating())
							.flatMap(existingRating -> {
								existingRating.setBookId(book.getId());
								existingRating.setMemberId(request.memberId());
								existingRating.setScore(request.score());
								existingRating.setFeedback(request.feedback());
								existingRating.setRatedAt(LocalDateTime.now());
								return ratingRepository.save(existingRating);
							})
							.flatMap(savedRating -> summarizeRatings(book.getId(), request.memberId())
									.map(summary -> new RatingResponse(
											savedRating.getId(),
											savedRating.getBookId(),
											savedRating.getMemberId(),
											savedRating.getScore(),
											savedRating.getFeedback(),
											savedRating.getRatedAt(),
											summary.averageScore(),
											summary.ratingCount())));
				});
	}

	// Fetch the member, then expand each active loan with its book details for the response.
	public Flux<LoanResponse> getActiveLoans(Long memberId) {
		
		return memberRepository.findById(memberId)
				.switchIfEmpty(Mono.error(new NotFoundException("Member not found.")))
				.flatMapMany(member -> loanRepository.findByMemberIdAndReturnedAtIsNull(member.getId())
						.flatMap(loan -> bookRepository.findById(loan.getBookId())
								.map(book -> toLoanResponse(loan, book))));
	}

	public Mono<BookRatingSummary> getRatingSummary(Long bookId, Long memberId) {
		return summarizeRatings(bookId, memberId);
	}

	// Shared lookup for the member/book pair used by loan and rating.
	private Mono<EntityBundle> validateMemberAndBook(Long memberId, Long bookId) {
		return Mono.zip(
				memberRepository.findById(memberId)
						.switchIfEmpty(Mono.error(new NotFoundException("Member not found."))),
				bookRepository.findById(bookId)
						.switchIfEmpty(Mono.error(new NotFoundException("Book not found."))))
				.map(tuple -> new EntityBundle(tuple.getT1(), tuple.getT2()));
	}

	// Combine the book row, all ratings, and the current member's rating into one summary.
	private Mono<BookRatingSummary> summarizeRatings(Long bookId, Long memberId) {		
		Mono<Book> bookMono = bookRepository.findById(bookId)
				.switchIfEmpty(Mono.error(new NotFoundException("Book not found.")));
		Mono<java.util.List<Rating>> ratingsMono = ratingRepository.findByBookId(bookId).collectList();
		Mono<Rating> memberRatingMono = memberId == null ? Mono.just(new Rating())
				: ratingRepository.findByMemberIdAndBookId(memberId, bookId).defaultIfEmpty(new Rating());

		return Mono.zip(bookMono, ratingsMono.defaultIfEmpty(java.util.List.of()), memberRatingMono)
				.map(tuple -> {
					Book book = tuple.getT1();
					java.util.List<Rating> ratings = tuple.getT2();
					Rating memberRating = tuple.getT3();
					double average = ratings.stream().mapToInt(Rating::getScore).average().orElse(0.0);
					Integer memberScore = memberRating.getScore();
					return new BookRatingSummary(book.getId(), book.getTitle(), average, ratings.size(), memberScore);
				});
	}

	// Merge loan fields with the book title and current inventory for the API payload.
	private LoanResponse toLoanResponse(Loan loan, Book book) {
		return new LoanResponse(loan.getId(), book.getId(), book.getTitle(), loan.getMemberId(), loan.getCheckedOutAt(),
				loan.getReturnedAt(), loan.getStatus(), book.getAvailableCopies());
	}

	// Input validation helper.
	private record EntityBundle(Member member, Book book) {
	}
}