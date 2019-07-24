import { Component, OnInit, HostListener } from '@angular/core';
import { MatDialogRef } from '@angular/material';
import { environment} from 'src/environments/environment';

@Component({
  selector: 'app-support-dialog',
  templateUrl: './support-dialog.component.html',
  styleUrls: ['./support-dialog.component.scss']
})

export class SupportDialogComponent implements OnInit {
  supportEmail: string;
  mobile: boolean;

  constructor(public dialogRef: MatDialogRef<SupportDialogComponent>) {}

   ngOnInit() {
    this.supportEmail = environment.supportEmail;
    this.mobile = document.body.clientWidth < 992;

     }

  mailToSupport() {
    window.location.href = `mailTo:${this.supportEmail}`;
  }

  @HostListener('window:resize')
      onResize(): void {
         this.mobile = document.body.clientWidth < 992;
      }
}
