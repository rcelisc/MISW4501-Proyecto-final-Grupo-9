import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FoodInfoService } from '../../../../services/food-info.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MaterialModule } from '../../../../material.module';
import { AuthService } from '../../../../services/auth.service';
import { CommonModule } from '@angular/common';
import { TranslateService, TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-food-info',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule, CommonModule, TranslateModule],
  templateUrl: './food-info.component.html',
  styleUrls: ['./food-info.component.scss']
})
export class FoodInfoComponent implements OnInit {
  foodInfoForm: FormGroup;
  userId: number = 0;

  constructor(
    private fb: FormBuilder,
    private foodInfoService: FoodInfoService,
    private snackBar: MatSnackBar,
    private router: Router,
    private authService: AuthService,
    private translate: TranslateService
  ) {
    this.foodInfoForm = this.fb.group({
      daily_calories: ['', Validators.required],
      daily_protein: ['', Validators.required],
      daily_liquid: ['', Validators.required],
      daily_carbs: ['', Validators.required],
      meal_frequency: ['', Validators.required],
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

    if (!this.foodInfoForm.valid) {
      this.translate.get('requiredFieldsError').subscribe((res: string) => {
        this.snackBar.open(res, 'Cerrar', {
          duration: 3000,
          panelClass: ['snack-bar-error']
        });
      });
      return;
    }

    this.foodInfoService.createFoodInfo(this.userId, this.foodInfoForm.value).subscribe({
      next: (response) => {
        this.translate.get('foodInfoAddedSuccess').subscribe((res: string) => {
          this.snackBar.open(res, 'Cerrar', { duration: 3000 });
          this.router.navigate(['/athlete-dashboard']);
        });
      },
      error: (error) => {
        this.translate.get('foodInfoAddedError').subscribe((res: string) => {
          this.snackBar.open(res, 'Cerrar', { duration: 3000 });
        });
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/athlete-dashboard']);
  }
}
