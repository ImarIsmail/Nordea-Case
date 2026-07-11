package Nordea.Case.bookflow.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("loans")
public class Loan {

	@Id
	private Long id;

	@Column("book_id")
	private Long bookId;

	@Column("member_id")
	private Long memberId;

	@Column("checked_out_at")
	private LocalDateTime checkedOutAt;

	@Column("returned_at")
	private LocalDateTime returnedAt;

	private LoanStatus status;

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

	public LocalDateTime getCheckedOutAt() {
		return checkedOutAt;
	}

	public void setCheckedOutAt(LocalDateTime checkedOutAt) {
		this.checkedOutAt = checkedOutAt;
	}

	public LocalDateTime getReturnedAt() {
		return returnedAt;
	}

	public void setReturnedAt(LocalDateTime returnedAt) {
		this.returnedAt = returnedAt;
	}

	public LoanStatus getStatus() {
		return status;
	}

	public void setStatus(LoanStatus status) {
		this.status = status;
	}
}