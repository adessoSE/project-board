import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Host, Inject, OnDestroy } from '@angular/core';
import { DateAdapter, MAT_DATE_FORMATS, MatCalendar, MatDateFormats } from '@angular/material';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

/** Custom header component for datepicker. */
@Component({
  selector: 'app-datepicker-header-component',
  styleUrls: ['./datepicker-header.component.scss'],
  templateUrl: './datepicker-header.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DatepickerHeaderComponent<D> implements OnDestroy {
  private destroyed = new Subject<void>();

  constructor(@Host() private calendar: MatCalendar<D>,
              private dateAdapter: DateAdapter<D>,
              @Inject(MAT_DATE_FORMATS) private dateFormats: MatDateFormats,
              cdr: ChangeDetectorRef) {
    calendar.stateChanges
      .pipe(takeUntil(this.destroyed))
      .subscribe(() => cdr.markForCheck());
  }

  ngOnDestroy(): void {
    this.destroyed.next();
    this.destroyed.complete();
  }

  get periodLabel(): string {
    return this.dateAdapter
      .format(this.calendar.activeDate, this.dateFormats.display.monthYearLabel)
      .toLocaleUpperCase();
  }

  previousMonthClicked(): void {
    this.calendar.activeDate = this.dateAdapter.addCalendarMonths(this.calendar.activeDate, -1);
  }

  nextMonthClicked(): void {
    this.calendar.activeDate = this.dateAdapter.addCalendarMonths(this.calendar.activeDate, 1);
  }
}
