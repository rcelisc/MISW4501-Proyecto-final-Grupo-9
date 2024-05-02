import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FoodInfoService } from '../../../../services/food-info.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MaterialModule } from '../../../../shared/material.module';

@Component({
  selector: 'app-food-info',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule],
  templateUrl: './food-info.component.html',
  styleUrl: './food-info.component.scss'
})
export class FoodInfoComponent {
  foodInfoForm: FormGroup;
  userId: number = 1; // This should be dynamically set based on the logged-in user
  constructor(
    private fb: FormBuilder,
    private foodInfoService: FoodInfoService,
    private snackBar: MatSnackBar,
    private router: Router,
  ){
    this.foodInfoForm = this.fb.group({
      daily_calories: [''],
      daily_protein: [''],
      daily_liquid: [''],
      daily_carbs : [''],
      meal_frequency: [''],
    });
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
}

