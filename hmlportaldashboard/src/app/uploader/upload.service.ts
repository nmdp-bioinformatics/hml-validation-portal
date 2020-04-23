import { Injectable } from '@angular/core';
import {Option} from '../validators/option';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UploadService {

  SERVER_URL = 'http://localhost:8090/hml/validation';

  selectedOption: Option ;

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type':  'application/xml',
      'Authorization': 'jwt-token'
    }),
    params: new HttpParams( {

    }),
    reportProgress: true,
    observe: 'events'
  };

  constructor(private httpClient: HttpClient) { }

  public setSelection(input) {
    this.selectedOption = input;
  }

  public upload(formData) {

    console.log(this.selectedOption.name);
    if (this.selectedOption.name === "hml gateway validator")
      this.httpOptions.params.set("hmlgateway","true");
    if (this.selectedOption.name === "miring validator")
      this.httpOptions.params.set("miring","true");

    return this.httpClient.post<any>(this.SERVER_URL, formData, {
      params: this.httpOptions.params,
      reportProgress: true,
      observe: 'events'
    }).subscribe(
      (res) => console.log(res),
      (err) => console.log(err));
  }
}
