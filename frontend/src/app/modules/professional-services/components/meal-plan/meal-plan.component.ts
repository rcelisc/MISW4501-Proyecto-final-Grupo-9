import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NutritionPlanService } from '../../../../services/nutrition-plan.service';
import { AuthService } from '../../../../services/auth.service';
import { MaterialModule } from '../../../../material.module';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import { SocketService } from '../../../../services/socket.service';
import { TranslateService, TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-meal-plan',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule, CommonModule, TranslateModule],
  templateUrl: './meal-plan.component.html',
  styleUrls: ['./meal-plan.component.scss']
})
export class MealPlanComponent implements OnInit, OnDestroy {
  formulario: FormGroup;
  users: any[] = [];
  socketSubscription!: Subscription;

  constructor(
    private fb: FormBuilder,
    private nutritionPlanService: NutritionPlanService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private router: Router,
    private socketService: SocketService,
    private translate: TranslateService
  ) {
    this.translate.setDefaultLang('en');
    this.formulario = this.fb.group({
      user_id: ['', Validators.required],
      nutritional_objectives: ['', Validators.required],
      description: ['', Validators.required],
      meal_frequency: ['', Validators.required],
      food_types: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.loadUsers('athlete');
    this.socketSubscription = this.socketService.fromEvent<any>('nutrition_plan_notification').subscribe({
      next: (data) => {
        console.log('Notification Received:', data);
        this.snackBar.open(this.translate.instant('newNutritionPlanNotification', { details: data.details }), 'Close', {
          duration: 3000
        });
      },
      error: (error) => console.error('Error receiving data:', error)
    });
  }

  ngOnDestroy() {
    this.socketService.disconnect();
    if (this.socketSubscription) {
      this.socketSubscription.unsubscribe();
    }
  }

  loadUsers(userType: string = 'athlete') {
    this.authService.getUsers(userType).subscribe({
      next: (response) => {
        this.users = response;
      },
      error: (error) => {
        console.error('Error while fetching users:', error);
        this.snackBar.open(this.translate.instant('errorFetchingUsers'), 'Close', {
          duration: 3000,
          panelClass: ['snack-bar-error']
        });
      }
    });
  }

  crearPlanAlimentacion() {
    this.formulario.markAllAsTouched();
    if (this.formulario.valid) {
      const datos = this.formulario.value;
      this.nutritionPlanService.createMealPlan(datos).subscribe({
        next: (respuesta) => {
          this.snackBar.open(this.translate.instant('mealPlanCreated'), 'Close', { duration: 3000 });
          this.router.navigate(['/professional-dashboard']);
        },
        error: (error) => {
          console.error('Error while sending data:', error);
          this.snackBar.open(this.translate.instant('errorCreatingPlan'), 'Close', {
            duration: 3000,
            panelClass: ['snack-bar-error']
          });
        }
      });
    } else {
      this.snackBar.open(this.translate.instant('completeRequiredFields'), 'Close', {
        duration: 3000,
        panelClass: ['snack-bar-error']
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/professional-dashboard']);
  }
}
