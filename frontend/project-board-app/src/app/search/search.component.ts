import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {
  @Input()
  infoTooltip = '';
  @Input()
  placeholder = '';
  @Input()
  infoTooltipHeader = '';

  @Output()
  search = new EventEmitter<string>();

  searchText = '';

  constructor() { }

  ngOnInit() {
  }

  trigger() {
    this.search.emit(this.searchText);
  }
}
