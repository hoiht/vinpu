import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'user-tracking-time',
        loadChildren: () => import('./user-tracking-time/user-tracking-time.module').then(m => m.VinpuUserTrackingTimeModule),
      },
      {
        path: 'order',
        loadChildren: () => import('./order/order.module').then(m => m.VinpuOrderModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class VinpuEntityModule {}
