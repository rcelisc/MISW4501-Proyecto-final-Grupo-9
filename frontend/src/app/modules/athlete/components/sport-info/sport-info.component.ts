import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SportInfoService } from '../../../../services/sport-info.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MaterialModule } from '../../../../shared/material.module';

@Component({
  selector: 'app-sport-info',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule],
  templateUrl: './sport-info.component.html',
  styleUrl: './sport-info.component.scss'
})
export class SportInfoComponent {
  sportInfoForm: FormGroup;
  userId: number = 1; // This should be dynamically set based on the logged-in user
  constructor(
    private fb: FormBuilder,
    private sportInfoService: SportInfoService,
    private snackBar: MatSnackBar,
    private router: Router,
  ){
    this.sportInfoForm = this.fb.group({
      training_frequency: [''],
      sports_practiced: [''],
      average_session_duration: [''],
      recovery_time : [''],
      training_pace: [''],
    });
  }
  
  onSubmit(): void {
    // Trigger validation for all form fields
    this.sportInfoForm.markAllAsTouched();

    if (!this.sportInfoForm.valid) {
      this.snackBar.open('Por favor, complete los campos requeridos.', 'Cerrar', {
        duration: 3000,
        panelClass: ['snack-bar-error'] 
      });
      return; 
    }

    // Proceed with the form submission if the form is valid
    this.sportInfoService.createSportInfo(this.userId, this.sportInfoForm.value).subscribe({
      next: (response) => {
        this.snackBar.open('Informacion deportiva agregada exitosamente', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/athlete-dashboard']);
      },
      error: (error) => {
        this.snackBar.open('Error al agregar informacion deportiva del usuario', 'Cerrar', { duration: 3000 });
      }
    });
  }
}
