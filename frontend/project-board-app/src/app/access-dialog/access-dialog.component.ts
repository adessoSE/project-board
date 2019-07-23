import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material';

@Component({
  selector: 'app-access-dialog',
  templateUrl: './access-dialog.component.html',
  styleUrls: ['./access-dialog.component.scss']
})

export class AccessDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<AccessDialogComponent>,) {}

   ngOnInit() {
     }

   openFAQ(){
   }

}
