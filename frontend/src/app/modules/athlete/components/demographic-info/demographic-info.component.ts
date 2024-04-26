import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { DemographicInfoService } from '../../../../services/demographic-info.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MaterialModule } from '../../../../shared/material.module';

@Component({
  selector: 'app-demographic-info',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule],
  templateUrl: './demographic-info.component.html',
  styleUrl: './demographic-info.component.scss'
})
export class DemographicInfoComponent {
  demographicInfoForm: FormGroup;
  userId: number = 1; // This should be dynamically set based on the logged-in user
  constructor(
    private fb: FormBuilder,
    private demographicInfoService: DemographicInfoService,
    private snackBar: MatSnackBar,
    private router: Router,
  ){
    this.demographicInfoForm = this.fb.group({
      ethnicity: [''],
      heart_rate: [''],
      vo2_max: [''],
      blood_pressure : [''],
      respiratory_rate: [''],
    });
  }
  
  onSubmit(): void {
    // Trigger validation for all form fields
    this.demographicInfoForm.markAllAsTouched();

    if (!this.demographicInfoForm.valid) {
      this.snackBar.open('Por favor, complete los campos requeridos.', 'Cerrar', {
        duration: 3000,
        panelClass: ['snack-bar-error'] 
      });
      return; 
    }

    // Proceed with the form submission if the form is valid
    this.demographicInfoService.createDemographicInfo(this.userId, this.demographicInfoForm.value).subscribe({
      next: (response) => {
        this.snackBar.open('Informacion demografica agregada exitosamente', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/athlete-dashboard']);
      },
      error: (error) => {
        this.snackBar.open('Error al agregar informacion demografica del usuario', 'Cerrar', { duration: 3000 });
      }
    });
  }
}
