package Nordea.Case.bookflow.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("ratings")
public class Rating {

	@Id
	private Long id;

	@Column("book_id")
	private Long bookId;

	@Column("member_id")
	private Long memberId;

	private Integer score;

	private String feedback;

	@Column("rated_at")
	private LocalDateTime ratedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public LocalDateTime getRatedAt() {
		return ratedAt;
	}

	public void setRatedAt(LocalDateTime ratedAt) {
		this.ratedAt = ratedAt;
	}
}