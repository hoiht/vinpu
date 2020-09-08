import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, Search } from 'app/shared/util/request-util';
import { IUserTrackingTime } from 'app/shared/model/user-tracking-time.model';

type EntityResponseType = HttpResponse<IUserTrackingTime>;
type EntityArrayResponseType = HttpResponse<IUserTrackingTime[]>;

@Injectable({ providedIn: 'root' })
export class UserTrackingTimeService {
  public resourceUrl = SERVER_API_URL + 'api/user-tracking-times';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/user-tracking-times';

  constructor(protected http: HttpClient) {}

  create(userTrackingTime: IUserTrackingTime): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userTrackingTime);
    return this.http
      .post<IUserTrackingTime>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(userTrackingTime: IUserTrackingTime): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userTrackingTime);
    return this.http
      .put<IUserTrackingTime>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IUserTrackingTime>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IUserTrackingTime[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IUserTrackingTime[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  protected convertDateFromClient(userTrackingTime: IUserTrackingTime): IUserTrackingTime {
    const copy: IUserTrackingTime = Object.assign({}, userTrackingTime, {
      startTime: userTrackingTime.startTime && userTrackingTime.startTime.isValid() ? userTrackingTime.startTime.toJSON() : undefined,
      endTime: userTrackingTime.endTime && userTrackingTime.endTime.isValid() ? userTrackingTime.endTime.toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.startTime = res.body.startTime ? moment(res.body.startTime) : undefined;
      res.body.endTime = res.body.endTime ? moment(res.body.endTime) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((userTrackingTime: IUserTrackingTime) => {
        userTrackingTime.startTime = userTrackingTime.startTime ? moment(userTrackingTime.startTime) : undefined;
        userTrackingTime.endTime = userTrackingTime.endTime ? moment(userTrackingTime.endTime) : undefined;
      });
    }
    return res;
  }
}
