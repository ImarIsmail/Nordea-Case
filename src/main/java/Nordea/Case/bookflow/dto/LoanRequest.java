package Nordea.Case.bookflow.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LoanRequest(
		@NotNull @Positive Long memberId,
		@NotNull @Positive Long bookId) {
}