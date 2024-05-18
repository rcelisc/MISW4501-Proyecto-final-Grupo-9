import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { DemographicInfoService } from '../../../../services/demographic-info.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MaterialModule } from '../../../../shared/material.module';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-demographic-info',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule],
  templateUrl: './demographic-info.component.html',
  styleUrl: './demographic-info.component.scss'
})
export class DemographicInfoComponent implements OnInit{
  demographicInfoForm: FormGroup;
  userId: number = 0;
  constructor(
    private fb: FormBuilder,
    private demographicInfoService: DemographicInfoService,
    private snackBar: MatSnackBar,
    private router: Router,
    private authService: AuthService
  ){
    this.demographicInfoForm = this.fb.group({
      ethnicity: [''],
      heart_rate: [''],
      vo2_max: [''],
      blood_pressure : [''],
      respiratory_rate: [''],
    });
  }

  ngOnInit(): void {
      this.setUserIdFromToken();
  }
  
  setUserIdFromToken() {
    const token = localStorage.getItem('token');
    if (token) {
      const decodedToken = this.authService.decodeToken(token);
      if (decodedToken && 'user_id' in decodedToken) {
        this.userId = decodedToken.user_id;
      } else {
        console.error('Token is invalid or expired');
        this.router.navigate(['/login']); // Redirect to login if the token is invalid or expired
      }
    } else {
      this.router.navigate(['/login']); // Redirect to login if there's no token
    }
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

  goBack(): void {
    this.router.navigate(['/athlete-dashboard']);
  }
}
