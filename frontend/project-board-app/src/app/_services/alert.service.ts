import { Injectable } from '@angular/core';
import { NavigationStart, Router } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Injectable()
export class AlertService {
  private subject = new Subject<any>();
  private keepAfterNavigationChange = false;

  destroy$ = new Subject<void>();

  constructor(private router: Router) {
    // clear alert message on route change
    router.events
      .pipe(takeUntil(this.destroy$))
      .subscribe(event => {
        if (event instanceof NavigationStart) {
          if (this.keepAfterNavigationChange) {
            // only keep for a single location change
            this.keepAfterNavigationChange = false;
          } else {
            // clear alert
            this.clearAlert();
          }
        }
      });
  }

  clearAlert() {
    this.subject.next();
  }

  success(message: string, keepAfterNavigationChange = false) {
    this.keepAfterNavigationChange = keepAfterNavigationChange;
    this.subject.next({type: 'success', text: message});
  }

  error(message: string, keepAfterNavigationChange = false) {
    this.keepAfterNavigationChange = keepAfterNavigationChange;
    this.subject.next({type: 'error', text: message});
  }

  info(message: string, keepAfterNavigationChange = false) {
    this.keepAfterNavigationChange = keepAfterNavigationChange;
    this.subject.next({type: 'info', text: message});
  }

  warning(message: string, keepAfterNavigationChange = false) {
    this.keepAfterNavigationChange = keepAfterNavigationChange;
    this.subject.next({type: 'warning', text: message});
  }

  getMessage(): Observable<any> {
    return this.subject.asObservable();
  }
}
