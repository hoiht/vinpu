import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IUserTrackingTime } from 'app/shared/model/user-tracking-time.model';
import { UserTrackingTimeService } from './user-tracking-time.service';
import { UserTrackingTimeDeleteDialogComponent } from './user-tracking-time-delete-dialog.component';

@Component({
  selector: 'jhi-user-tracking-time',
  templateUrl: './user-tracking-time.component.html',
})
export class UserTrackingTimeComponent implements OnInit, OnDestroy {
  userTrackingTimes?: IUserTrackingTime[];
  eventSubscriber?: Subscription;
  currentSearch: string;

  constructor(
    protected userTrackingTimeService: UserTrackingTimeService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch =
      this.activatedRoute.snapshot && this.activatedRoute.snapshot.queryParams['search']
        ? this.activatedRoute.snapshot.queryParams['search']
        : '';
  }

  loadAll(): void {
    if (this.currentSearch) {
      this.userTrackingTimeService
        .search({
          query: this.currentSearch,
        })
        .subscribe((res: HttpResponse<IUserTrackingTime[]>) => (this.userTrackingTimes = res.body || []));
      return;
    }

    this.userTrackingTimeService.query().subscribe((res: HttpResponse<IUserTrackingTime[]>) => (this.userTrackingTimes = res.body || []));
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInUserTrackingTimes();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IUserTrackingTime): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInUserTrackingTimes(): void {
    this.eventSubscriber = this.eventManager.subscribe('userTrackingTimeListModification', () => this.loadAll());
  }

  delete(userTrackingTime: IUserTrackingTime): void {
    const modalRef = this.modalService.open(UserTrackingTimeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.userTrackingTime = userTrackingTime;
  }
}
