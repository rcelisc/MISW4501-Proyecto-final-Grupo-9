import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { FormBuilder, FormGroup, Validators} from '@angular/forms';
import { NutritionPlanService } from '../../../../services/nutrition-plan.service';
import { MaterialModule } from '../../../../shared/material.module';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import { SocketService } from '../../../../services/socket.service'

@Component({
  selector: 'app-meal-plan',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule, CommonModule],
  templateUrl: './meal-plan.component.html',
  styleUrl: './meal-plan.component.scss'
})

// implements OnInit, OnDestroy
export class MealPlanComponent {
  formulario: FormGroup;
  socketSubscription!: Subscription;

  constructor(
    private fb: FormBuilder, 
    private nutritionPlanService: NutritionPlanService,
    private snackBar: MatSnackBar,
    private router: Router,
    private socketService: SocketService
  ){
      this.formulario = this.fb.group({
        user_id: ['', Validators.required],
        nutritional_objectives: ['', Validators.required],
        description: ['', Validators.required],
        meal_frequency: ['', Validators.required],
        food_types: ['', Validators.required]
      });
    }

    ngOnInit() {
      this.socketSubscription = this.socketService.fromEvent<any>('nutrition_plan_notification').subscribe({
        next: (data) => {
          console.log('Notification Received:', data);
          this.snackBar.open(`New Nutrition Plan Created: ${data.details}`, 'Close', {
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

    crearPlanAlimentacion() {
      this.formulario.markAllAsTouched();
      if (this.formulario.valid) {
        const datos = this.formulario.value;
        this.nutritionPlanService.createMealPlan(datos).subscribe({
          next: (respuesta) => {
            this.snackBar.open('Meal plan created successfully.', 'Close', { duration: 3000 });
            this.router.navigate(['/professional-dashboard']); // Redirecting to the professional dashboard
          },
          error: (error) => {
            console.error('Error while sending data:', error);
            this.snackBar.open('Error while creating the plan. Please try again later.', 'Close', {
              duration: 3000,
              panelClass: ['snack-bar-error']
            });
          }
        });
      } else {
        this.snackBar.open('Please complete all required fields.', 'Close', {
          duration: 3000,
          panelClass: ['snack-bar-error']
        });
      }
    }
  }