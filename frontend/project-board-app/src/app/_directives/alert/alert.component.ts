import { Component, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AlertService } from '../../_services/alert.service';

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.scss']
})

export class AlertComponent implements OnInit {
  message: any;

  destroy$ = new Subject<void>();

  constructor(private alertService: AlertService) {
  }

  ngOnInit(): void {
    this.alertService.getMessage()
      .pipe(takeUntil(this.destroy$))
      .subscribe(message => {
        this.message = message;
      });
  }

  clearAlert(): void {
    this.alertService.clearAlert();
  }

}
