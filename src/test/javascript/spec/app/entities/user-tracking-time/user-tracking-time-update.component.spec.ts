import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { VinpuTestModule } from '../../../test.module';
import { UserTrackingTimeUpdateComponent } from 'app/entities/user-tracking-time/user-tracking-time-update.component';
import { UserTrackingTimeService } from 'app/entities/user-tracking-time/user-tracking-time.service';
import { UserTrackingTime } from 'app/shared/model/user-tracking-time.model';

describe('Component Tests', () => {
  describe('UserTrackingTime Management Update Component', () => {
    let comp: UserTrackingTimeUpdateComponent;
    let fixture: ComponentFixture<UserTrackingTimeUpdateComponent>;
    let service: UserTrackingTimeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [VinpuTestModule],
        declarations: [UserTrackingTimeUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(UserTrackingTimeUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(UserTrackingTimeUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(UserTrackingTimeService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new UserTrackingTime(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new UserTrackingTime();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
