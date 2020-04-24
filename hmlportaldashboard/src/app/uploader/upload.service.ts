import { Injectable } from '@angular/core';
import {Option} from '../validators/option';
import {HttpClient, HttpHeaders, HttpParams, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UploadService {

  SERVER_URL = 'http://localhost:8090/hml/validation';

  selectedOption: Option ;



  constructor(private httpClient: HttpClient) { }

  public setSelection(input) {
    this.selectedOption = input;
  }

  public upload(formData) {
  let headers = new HttpHeaders();
    headers = headers.set("Content-Type", "application/xml");
      headers = headers.set("hmlgateway", "false");
      headers = headers.set("miring", "false");
    headers = headers.set("glstringValid", "false");

    console.log(this.selectedOption.name);
    if (this.selectedOption.name === "hml gateway validator")
    {
      headers = headers.set('hmlgateway','true');

    }
    if (this.selectedOption.name === "miring validator")
    {
      headers = headers.set('miring','true');
    }
    if (this.selectedOption.name === "glstring allele validator")
    {
      headers = headers.set('glstringValid','true');
    }
    resp : Response;
    console.log("hmlgateway = " + headers.get('hmlgateway'));
    console.log("miring = " + headers.get('miring'));
    console.log("glstring = " + headers.get('glstringValid'));
    return this.httpClient.post(this.SERVER_URL, formData, {
      headers: headers,
      reportProgress: true,
      observe: 'events'
    }).  subscribe(
      (response) =>  console.log("Response - " + response),
      (error) => console.log("Errors - " + error));
  }
}
