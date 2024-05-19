import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CreateEventService } from '../../../../services/create-event.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MaterialModule } from '../../../../material.module';
import { TranslateService, TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-create-event',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule, TranslateModule],
  providers: [DatePipe],
  templateUrl: './create-event.component.html',
  styleUrls: ['./create-event.component.scss']
})
export class CreateEventComponent {
  createEventForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private createEventService: CreateEventService,
    private snackBar: MatSnackBar,
    private router: Router,
    private datePipe: DatePipe,
    private translate: TranslateService
  ) {
    this.createEventForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      event_date: ['', Validators.required],
      duration: ['', [Validators.required, Validators.pattern(/^\d+$/)]],
      location: ['', Validators.required],
      category: ['', Validators.required],
      fee: ['', [Validators.required, Validators.pattern(/^\d+$/)]]
    });
  }

  switchLanguage(language: string) {
    this.translate.use(language);
  }

  onSubmit(): void {
    // Trigger validation for all form fields
    this.createEventForm.markAllAsTouched();

    if (!this.createEventForm.valid) {
      this.snackBar.open(this.translate.instant('createEventValidationError'), 'Cerrar', {
        duration: 3000,
        panelClass: ['snack-bar-error']
      });
      return;
    }

    const eventDateControl = this.createEventForm.get('event_date');

    if (!eventDateControl) {
      this.snackBar.open(this.translate.instant('createEventDateError'), 'Cerrar', { duration: 3000 });
      return;
    }

    const eventDateValue = eventDateControl.value ? eventDateControl.value : new Date(); // Provide a default date if null
    const formattedDate = this.datePipe.transform(eventDateValue, 'yyyy-MM-ddTHH:mm:ss');

    if (!formattedDate) {
      this.snackBar.open(this.translate.instant('createEventDateFormatError'), 'Cerrar', { duration: 3000 });
      return; // Handle case where date could not be formatted
    }

    const formData = {
      ...this.createEventForm.value,
      event_date: formattedDate  // Use formatted date
    };

    this.createEventService.createEvent(formData).subscribe({
      next: (response) => {
        this.snackBar.open(this.translate.instant('createEventSuccess'), 'Cerrar', { duration: 3000 });
        this.router.navigate(['/organizer-dashboard']);
      },
      error: (error) => {
        this.snackBar.open(this.translate.instant('createEventError'), 'Cerrar', { duration: 3000 });
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/organizer-dashboard']);
  }
}
