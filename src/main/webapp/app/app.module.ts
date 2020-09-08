import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { VinpuSharedModule } from 'app/shared/shared.module';
import { VinpuCoreModule } from 'app/core/core.module';
import { VinpuAppRoutingModule } from './app-routing.module';
import { VinpuHomeModule } from './home/home.module';
import { VinpuEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ActiveMenuDirective } from './layouts/navbar/active-menu.directive';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    VinpuSharedModule,
    VinpuCoreModule,
    VinpuHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    VinpuEntityModule,
    VinpuAppRoutingModule,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, ActiveMenuDirective, FooterComponent],
  bootstrap: [MainComponent],
})
export class VinpuAppModule {}
