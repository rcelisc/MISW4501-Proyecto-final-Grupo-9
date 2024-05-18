import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CreateServiceService } from '../../../../services/create-service.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MaterialModule } from '../../../../shared/material.module';

@Component({
  selector: 'app-create-service',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule],
  templateUrl: './create-service.component.html',
  styleUrl: './create-service.component.scss'
})
export class CreateServiceComponent {
  createServiceForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private createServiceService: CreateServiceService,
    private snackBar: MatSnackBar,
    private router: Router
  ){
    this.createServiceForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      rate: ['', [Validators.required, Validators.pattern(/^\d+$/)]]
    });
  }

  onSubmit(): void {
    // Trigger validation for all form fields
    this.createServiceForm.markAllAsTouched();

    if (!this.createServiceForm.valid) {
      this.snackBar.open('Por favor, complete los campos requeridos.', 'Cerrar', {
        duration: 3000,
        panelClass: ['snack-bar-error']
      });
      return; // Return early if form is invalid
    }

    this.createServiceService.createService(this.createServiceForm.value).subscribe({
      next: (response) => {
        this.snackBar.open('Servicio creado exitosamente', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/service-list']);
      },
      error: (error) => {
        this.snackBar.open('Error al crear el servicio', 'Cerrar', { duration: 3000 });
      }
    });
  }
  goBack(): void {
    this.router.navigate(['/professional-dashboard']);
  }
}
