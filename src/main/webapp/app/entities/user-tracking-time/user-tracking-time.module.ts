import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { VinpuSharedModule } from 'app/shared/shared.module';
import { UserTrackingTimeComponent } from './user-tracking-time.component';
import { UserTrackingTimeDetailComponent } from './user-tracking-time-detail.component';
import { UserTrackingTimeUpdateComponent } from './user-tracking-time-update.component';
import { UserTrackingTimeDeleteDialogComponent } from './user-tracking-time-delete-dialog.component';
import { userTrackingTimeRoute } from './user-tracking-time.route';

@NgModule({
  imports: [VinpuSharedModule, RouterModule.forChild(userTrackingTimeRoute)],
  declarations: [
    UserTrackingTimeComponent,
    UserTrackingTimeDetailComponent,
    UserTrackingTimeUpdateComponent,
    UserTrackingTimeDeleteDialogComponent,
  ],
  entryComponents: [UserTrackingTimeDeleteDialogComponent],
})
export class VinpuUserTrackingTimeModule {}
