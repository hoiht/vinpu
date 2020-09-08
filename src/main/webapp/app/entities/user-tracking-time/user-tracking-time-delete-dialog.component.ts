import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IUserTrackingTime } from 'app/shared/model/user-tracking-time.model';
import { UserTrackingTimeService } from './user-tracking-time.service';

@Component({
  templateUrl: './user-tracking-time-delete-dialog.component.html',
})
export class UserTrackingTimeDeleteDialogComponent {
  userTrackingTime?: IUserTrackingTime;

  constructor(
    protected userTrackingTimeService: UserTrackingTimeService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userTrackingTimeService.delete(id).subscribe(() => {
      this.eventManager.broadcast('userTrackingTimeListModification');
      this.activeModal.close();
    });
  }
}
