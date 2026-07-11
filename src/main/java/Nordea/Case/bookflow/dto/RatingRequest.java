package Nordea.Case.bookflow.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RatingRequest(
		@NotNull @Positive Long memberId,
		@NotNull @Positive Long bookId,
		@NotNull @Min(1) @Max(5) Integer score,
		String feedback) {
}