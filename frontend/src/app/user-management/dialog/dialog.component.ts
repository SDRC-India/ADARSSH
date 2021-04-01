import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material';

@Component({
  selector: 'rmncha-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.scss']
})
export class DialogComponent implements OnInit {
  
  form: FormGroup;
  constructor(private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<DialogComponent>) { }

  ngOnInit() {
    this.form = this.formBuilder.group({
      filename: ''
    })
  }

  submit(form) {
    this.dialogRef.close(`${form.value.filename}`);
  }

}
