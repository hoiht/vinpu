import { Moment } from 'moment';

export interface IOrder {
  id?: number;
  from?: number;
  service?: string;
  time?: Moment;
}

export class Order implements IOrder {
  constructor(public id?: number, public from?: number, public service?: string, public time?: Moment) {}
}
