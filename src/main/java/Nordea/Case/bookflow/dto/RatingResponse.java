package Nordea.Case.bookflow.dto;

import java.time.LocalDateTime;

public record RatingResponse(
		Long ratingId,
		Long bookId,
		Long memberId,
		Integer score,
		String feedback,
		LocalDateTime ratedAt,
		double averageScore,
		long ratingCount) {
}