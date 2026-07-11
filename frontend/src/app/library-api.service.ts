import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { BookRatingSummary, LoanResponse } from './models';

@Injectable({ providedIn: 'root' })
export class LibraryApiService {
  private readonly http = inject(HttpClient);

  getActiveLoans(memberId: number): Observable<LoanResponse[]> {
    return this.http.get<LoanResponse[]>(`/api/members/${memberId}/loans`);
  }

  getRatingSummary(bookId: number, memberId: number): Observable<BookRatingSummary> {
    const params = new HttpParams().set('memberId', memberId.toString());
    return this.http.get<BookRatingSummary>(`/api/books/${bookId}/ratings-summary`, { params });
  }
}