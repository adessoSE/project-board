import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { Employee} from '../_services/employee.service';

@Component({
  selector: 'app-faq',
  templateUrl: './faq.component.html',
  styleUrls: ['./faq.component.scss']
})
export class FaqComponent implements OnInit {

  isUserBoss = false;
  destroy$ = new Subject<void>();

  user: Employee;

  constructor(private route: ActivatedRoute, ) {}

  ngOnInit(): void {

    this.route.data
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => {
        this.user = data.user;
      });
  }

}
