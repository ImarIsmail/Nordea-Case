package Nordea.Case.bookflow.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReturnLoanRequest(
		@NotNull @Positive Long memberId) {
}