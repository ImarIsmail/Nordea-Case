package Nordea.Case.bookflow.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import Nordea.Case.bookflow.domain.Loan;

public interface LoanRepository extends ReactiveCrudRepository<Loan, Long> {

	Flux<Loan> findByMemberIdAndReturnedAtIsNull(Long memberId);

	Mono<Loan> findByMemberIdAndBookIdAndReturnedAtIsNull(Long memberId, Long bookId);
}