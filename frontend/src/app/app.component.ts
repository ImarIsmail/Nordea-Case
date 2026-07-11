import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ReactiveFormsModule, FormControl, Validators } from '@angular/forms';
import { catchError, forkJoin, finalize, map, of, switchMap } from 'rxjs';
import { LibraryApiService } from './library-api.service';
import { LoanDashboardItem } from './models';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './app.template.html',
  styleUrl : './app.style.css'
})
export class AppComponent implements OnInit {
  private readonly api = inject(LibraryApiService);

  readonly memberIdControl = new FormControl<number>(1, {
    nonNullable: true,
    validators: [Validators.required, Validators.min(1)]
  });
  loading = false;
  error = '';
  items: LoanDashboardItem[] = [];

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    const parsedMemberId = Number(this.memberIdControl.value);

    if (!Number.isFinite(parsedMemberId) || parsedMemberId < 1) {
      this.error = 'Enter a valid member ID greater than 0.';
      this.items = [];
      return;
    }

    this.loading = true;
    this.error = '';

    this.api.getActiveLoans(parsedMemberId).pipe(
      switchMap((loans) => {
        if (loans.length === 0) {
          return of([] as LoanDashboardItem[]);
        }

        return forkJoin(
          loans.map((loan) =>
            this.api.getRatingSummary(loan.bookId, parsedMemberId).pipe(
              map((summary) => ({ loan, summary }))
            )
          )
        );
      }),
      catchError((error) => {
        this.error = this.extractMessage(error);
        return of([] as LoanDashboardItem[]);
      }),
      finalize(() => {
        this.loading = false;
      })
    ).subscribe((items) => {
      this.items = items;
    });
  }

  trackByLoan(_: number, item: LoanDashboardItem): number {
    return item.loan.loanId;
  }

  private extractMessage(error: unknown): string {
    if (typeof error === 'object' && error && 'error' in error) {
      const response = error as { error?: { message?: string } };
      return response.error?.message ?? 'Unable to load dashboard data.';
    }

    return 'Unable to load dashboard data.';
  }
}