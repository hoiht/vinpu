import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IUserTrackingTime, UserTrackingTime } from 'app/shared/model/user-tracking-time.model';
import { UserTrackingTimeService } from './user-tracking-time.service';
import { UserTrackingTimeComponent } from './user-tracking-time.component';
import { UserTrackingTimeDetailComponent } from './user-tracking-time-detail.component';
import { UserTrackingTimeUpdateComponent } from './user-tracking-time-update.component';

@Injectable({ providedIn: 'root' })
export class UserTrackingTimeResolve implements Resolve<IUserTrackingTime> {
  constructor(private service: UserTrackingTimeService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUserTrackingTime> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((userTrackingTime: HttpResponse<UserTrackingTime>) => {
          if (userTrackingTime.body) {
            return of(userTrackingTime.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new UserTrackingTime());
  }
}

export const userTrackingTimeRoute: Routes = [
  {
    path: '',
    component: UserTrackingTimeComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'vinpuApp.userTrackingTime.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UserTrackingTimeDetailComponent,
    resolve: {
      userTrackingTime: UserTrackingTimeResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'vinpuApp.userTrackingTime.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserTrackingTimeUpdateComponent,
    resolve: {
      userTrackingTime: UserTrackingTimeResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'vinpuApp.userTrackingTime.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserTrackingTimeUpdateComponent,
    resolve: {
      userTrackingTime: UserTrackingTimeResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'vinpuApp.userTrackingTime.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
