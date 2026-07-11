package Nordea.Case.bookflow.dto;

public record BookRatingSummary(
		Long bookId,
		String title,
		double averageScore,
		long ratingCount,
		Integer memberScore) {
}