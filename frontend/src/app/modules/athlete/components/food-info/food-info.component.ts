import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FoodInfoService } from '../../../../services/food-info.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MaterialModule } from '../../../../shared/material.module';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-food-info',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule],
  templateUrl: './food-info.component.html',
  styleUrl: './food-info.component.scss'
})
export class FoodInfoComponent implements OnInit{
  foodInfoForm: FormGroup;
  userId: number = 0;
  constructor(
    private fb: FormBuilder,
    private foodInfoService: FoodInfoService,
    private snackBar: MatSnackBar,
    private router: Router,
    private authService: AuthService
  ){
    this.foodInfoForm = this.fb.group({
      daily_calories: [''],
      daily_protein: [''],
      daily_liquid: [''],
      daily_carbs : [''],
      meal_frequency: [''],
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
    this.foodInfoForm.markAllAsTouched();

    this.foodInfoService.createFoodInfo(this.userId, this.foodInfoForm.value).subscribe({
      next: (response) => {
        this.snackBar.open('Informacion alimenticia agregada exitosamente', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/athlete-dashboard']);
      },
      error: (error) => {
        this.snackBar.open('Error al agregar informacion alimenticia del usuario', 'Cerrar', { duration: 3000 });
      }
    });
  }
  
  goBack(): void {
    this.router.navigate(['/athlete-dashboard']);
  }
}

