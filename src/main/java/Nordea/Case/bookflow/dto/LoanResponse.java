package Nordea.Case.bookflow.dto;

import java.time.LocalDateTime;

import Nordea.Case.bookflow.domain.LoanStatus;

public record LoanResponse(
		Long loanId,
		Long bookId,
		String bookTitle,
		Long memberId,
		LocalDateTime checkedOutAt,
		LocalDateTime returnedAt,
		LoanStatus status,
		Integer availableCopies) {
}