import { Component, OnInit } from '@angular/core';
import {UploadService} from '../uploader/upload.service';
import {Options} from './options';
import {Option} from './option';

@Component({
  selector: 'app-validators',
  templateUrl: './validators.component.html',
  styleUrls: ['./validators.component.scss']
})
export class ValidatorsComponent implements OnInit {

  options = Options;
  selectedOption: Option;

  constructor(private uploadService: UploadService) { }

  onSelect(choice: Option, event: any): void {
    if (event.target.checked)
    {
      this.selectedOption = choice;
    }
    else
    {
      this.selectedOption = null;
    }
    this.uploadService.setSelection(this.selectedOption);
  }

  ngOnInit() {
  }

}
