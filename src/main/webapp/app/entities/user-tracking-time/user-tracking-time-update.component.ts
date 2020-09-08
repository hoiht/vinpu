import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IUserTrackingTime, UserTrackingTime } from 'app/shared/model/user-tracking-time.model';
import { UserTrackingTimeService } from './user-tracking-time.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';

@Component({
  selector: 'jhi-user-tracking-time-update',
  templateUrl: './user-tracking-time-update.component.html',
})
export class UserTrackingTimeUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    startTime: [null, [Validators.required]],
    endTime: [null, [Validators.required]],
    duration: [],
    role: [],
    userId: [null, Validators.required],
  });

  constructor(
    protected userTrackingTimeService: UserTrackingTimeService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userTrackingTime }) => {
      if (!userTrackingTime.id) {
        const today = moment().startOf('day');
        userTrackingTime.startTime = today;
        userTrackingTime.endTime = today;
      }

      this.updateForm(userTrackingTime);

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));
    });
  }

  updateForm(userTrackingTime: IUserTrackingTime): void {
    this.editForm.patchValue({
      id: userTrackingTime.id,
      startTime: userTrackingTime.startTime ? userTrackingTime.startTime.format(DATE_TIME_FORMAT) : null,
      endTime: userTrackingTime.endTime ? userTrackingTime.endTime.format(DATE_TIME_FORMAT) : null,
      duration: userTrackingTime.duration,
      role: userTrackingTime.role,
      userId: userTrackingTime.userId,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userTrackingTime = this.createFromForm();
    if (userTrackingTime.id !== undefined) {
      this.subscribeToSaveResponse(this.userTrackingTimeService.update(userTrackingTime));
    } else {
      this.subscribeToSaveResponse(this.userTrackingTimeService.create(userTrackingTime));
    }
  }

  private createFromForm(): IUserTrackingTime {
    return {
      ...new UserTrackingTime(),
      id: this.editForm.get(['id'])!.value,
      startTime: this.editForm.get(['startTime'])!.value ? moment(this.editForm.get(['startTime'])!.value, DATE_TIME_FORMAT) : undefined,
      endTime: this.editForm.get(['endTime'])!.value ? moment(this.editForm.get(['endTime'])!.value, DATE_TIME_FORMAT) : undefined,
      duration: this.editForm.get(['duration'])!.value,
      role: this.editForm.get(['role'])!.value,
      userId: this.editForm.get(['userId'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserTrackingTime>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: IUser): any {
    return item.id;
  }
}
