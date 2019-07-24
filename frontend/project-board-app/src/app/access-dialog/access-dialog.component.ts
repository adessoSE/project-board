import { Component, OnInit, HostListener } from '@angular/core';
import { MatDialogRef } from '@angular/material';

@Component({
  selector: 'app-access-dialog',
  templateUrl: './access-dialog.component.html',
  styleUrls: ['./access-dialog.component.scss']
})

export class AccessDialogComponent implements OnInit {
  mobile: boolean;

  constructor(public dialogRef: MatDialogRef<AccessDialogComponent>,) {}

   ngOnInit() {
       this.mobile = document.body.clientWidth < 992;
   }


   @HostListener('window:resize')
    onResize(): void {
       this.mobile = document.body.clientWidth < 992;
    }

}
