import { Moment } from 'moment';

export interface IUserTrackingTime {
  id?: number;
  startTime?: Moment;
  endTime?: Moment;
  duration?: number;
  role?: string;
  userLogin?: string;
  userId?: number;
}

export class UserTrackingTime implements IUserTrackingTime {
  constructor(
    public id?: number,
    public startTime?: Moment,
    public endTime?: Moment,
    public duration?: number,
    public role?: string,
    public userLogin?: string,
    public userId?: number
  ) {}
}
