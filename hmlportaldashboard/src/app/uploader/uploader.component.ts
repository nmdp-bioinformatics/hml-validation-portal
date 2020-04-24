import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {UploadService} from './upload.service';
import {ValidatorsComponent} from '../validators/validators.component';

@Component({
  selector: 'app-uploader',
  templateUrl: './uploader.component.html',
  styleUrls: ['./uploader.component.scss']
})
export class UploaderComponent implements OnInit {

  uploadForm: FormGroup;
  private fileName;
  constructor(private uploaderService: UploadService, private formBuilder: FormBuilder) {}

  ngOnInit() {
    this.uploadForm = this.formBuilder.group({
      profile: ['']
    });
  }

  onSubmit() {
    const fileReader = new FileReader();
    const file = this.uploadForm.get('profile').value;
    this.fileName = file.name;
    console.log("Filename = "+this.fileName);
    fileReader.readAsText(file);
    fileReader.onload = (e) => {
      console.log("reader = " + fileReader.result);
    }
    this.uploaderService.upload(file);
  }

  onFileSelect(event) {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      this.uploadForm.get('profile').setValue(file);
    }
  }
}
