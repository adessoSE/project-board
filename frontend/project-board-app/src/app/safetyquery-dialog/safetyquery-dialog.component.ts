import { Component} from '@angular/core';
import { MatDialogRef } from '@angular/material';
import { NavigationStart, Router } from '@angular/router';

@Component({
  selector: 'app-safetyquery-dialog',
  templateUrl: './safetyquery-dialog.component.html',
  styleUrls: ['./safetyquery-dialog.component.scss']
})
export class SafetyqueryDialogComponent{

  constructor(
    public dialogRef: MatDialogRef<SafetyqueryDialogComponent>,
    private router: Router) {
    router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        this.dialogRef.close();
      }
    });
  }
}