import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUserTrackingTime } from 'app/shared/model/user-tracking-time.model';

@Component({
  selector: 'jhi-user-tracking-time-detail',
  templateUrl: './user-tracking-time-detail.component.html',
})
export class UserTrackingTimeDetailComponent implements OnInit {
  userTrackingTime: IUserTrackingTime | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userTrackingTime }) => (this.userTrackingTime = userTrackingTime));
  }

  previousState(): void {
    window.history.back();
  }
}
