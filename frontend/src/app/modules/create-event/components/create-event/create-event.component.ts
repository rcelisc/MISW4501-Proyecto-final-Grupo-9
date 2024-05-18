import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CreateEventService } from '../../../../services/create-event.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MaterialModule } from '../../../../shared/material.module';

@Component({
  selector: 'app-create-event',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule],
  providers: [DatePipe],
  templateUrl: './create-event.component.html',
  styleUrl: './create-event.component.scss'
})
export class CreateEventComponent {
  createEventForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private createEventService: CreateEventService,
    private snackBar: MatSnackBar,
    private router: Router,
    private datePipe: DatePipe
  ){
    this.createEventForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      event_date: ['', Validators.required],
      duration : ['', [Validators.required, Validators.pattern(/^\d+$/)]],
      location: ['', Validators.required],
      category: ['', Validators.required],
      fee: ['', [Validators.required, Validators.pattern(/^\d+$/)]]
    });
  }

  onSubmit(): void {
    // Trigger validation for all form fields
    this.createEventForm.markAllAsTouched();

    if (!this.createEventForm.valid) {
      this.snackBar.open('Por favor, complete los campos requeridos.', 'Cerrar', {
        duration: 3000,
        panelClass: ['snack-bar-error']
      });
      return;
    }

    const eventDateControl = this.createEventForm.get('event_date');
    
    if (!eventDateControl) {
      this.snackBar.open('Error en la fecha del evento.', 'Cerrar', { duration: 3000 });
      return;
    }

    const eventDateValue = eventDateControl.value ? eventDateControl.value : new Date(); // Provide a default date if null
    const formattedDate = this.datePipe.transform(eventDateValue, 'yyyy-MM-ddTHH:mm:ss');

    if (!formattedDate) {
        this.snackBar.open('Error al formatear la fecha.', 'Cerrar', { duration: 3000 });
        return; // Handle case where date could not be formatted
    }
      const formData = {
        ...this.createEventForm.value,
        event_date: formattedDate  // Use formatted date
      };

    this.createEventService.createEvent(formData).subscribe({
      next: (response) => {
        this.snackBar.open('Evento creado exitosamente', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/event-list']);
      },
      error: (error) => {
        this.snackBar.open('Error al crear el evento', 'Cerrar', { duration: 3000 });
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/organizer-dashboard']);
  }
}
