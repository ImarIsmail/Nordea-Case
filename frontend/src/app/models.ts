export interface LoanResponse {
  loanId: number;
  bookId: number;
  bookTitle: string;
  memberId: number;
  checkedOutAt: string;
  returnedAt: string | null;
  status: 'ACTIVE' | 'RETURNED';
  availableCopies: number;
}

export interface BookRatingSummary {
  bookId: number;
  title: string;
  averageScore: number;
  ratingCount: number;
  memberScore: number | null;
}

export interface LoanDashboardItem {
  loan: LoanResponse;
  summary: BookRatingSummary;
}