import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { VinpuTestModule } from '../../../test.module';
import { UserTrackingTimeComponent } from 'app/entities/user-tracking-time/user-tracking-time.component';
import { UserTrackingTimeService } from 'app/entities/user-tracking-time/user-tracking-time.service';
import { UserTrackingTime } from 'app/shared/model/user-tracking-time.model';

describe('Component Tests', () => {
  describe('UserTrackingTime Management Component', () => {
    let comp: UserTrackingTimeComponent;
    let fixture: ComponentFixture<UserTrackingTimeComponent>;
    let service: UserTrackingTimeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [VinpuTestModule],
        declarations: [UserTrackingTimeComponent],
      })
        .overrideTemplate(UserTrackingTimeComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(UserTrackingTimeComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(UserTrackingTimeService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new UserTrackingTime(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.userTrackingTimes && comp.userTrackingTimes[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
