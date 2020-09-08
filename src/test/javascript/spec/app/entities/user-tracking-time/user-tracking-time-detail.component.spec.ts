import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { VinpuTestModule } from '../../../test.module';
import { UserTrackingTimeDetailComponent } from 'app/entities/user-tracking-time/user-tracking-time-detail.component';
import { UserTrackingTime } from 'app/shared/model/user-tracking-time.model';

describe('Component Tests', () => {
  describe('UserTrackingTime Management Detail Component', () => {
    let comp: UserTrackingTimeDetailComponent;
    let fixture: ComponentFixture<UserTrackingTimeDetailComponent>;
    const route = ({ data: of({ userTrackingTime: new UserTrackingTime(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [VinpuTestModule],
        declarations: [UserTrackingTimeDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(UserTrackingTimeDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(UserTrackingTimeDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load userTrackingTime on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.userTrackingTime).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
