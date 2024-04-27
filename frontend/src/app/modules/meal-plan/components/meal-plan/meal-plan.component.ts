import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { FormBuilder, FormGroup, Validators} from '@angular/forms';
import { TrainingPlanService } from '../../../../services/training-plan.service';
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
  mostrarAlerta: boolean = false;
  errorMessage: string = '';
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
      this.formulario = this.fb.group({
        user_id: ['', Validators.required],
        nutritional_objectives: ['', Validators.required],
        description: ['', Validators.required],
        meal_frequency: ['', Validators.required],
        food_types: ['', Validators.required]
      });
  
      // Connect to the WebSocket
      this.socketService.connect();
      this.socketSubscription = this.socketService.fromEvent<any>('nutrition_plan_notification').subscribe({
        next: (data) => {
          console.log('Notification Received:', data);
          alert(`New Nutrition Plan Created: ${data.details} `);
        },
        error: (error) => console.error('Error receiving data:', error)
      });
      // // Obtener la lista de usuarios al inicializar el componente
      // this.userService.getUsuarios().subscribe(data => {
      //   this.usuarios = data;
      // });
    }
  
    ngOnDestroy() {
      this.socketService.disconnect();
      if (this.socketSubscription) {
        this.socketSubscription.unsubscribe();
      }
    }

    crearPlanAlimentacion() {

      this.formulario.markAllAsTouched();
      if (!this.formulario.valid) {
        this.snackBar.open('Por favor, complete los campos requeridos.', 'Cerrar', {
          duration: 3000,
          panelClass: ['snack-bar-error'] // Apply custom CSS for styling the error snackbar
        });
        return; // Return early if form is invalid
      }

      if (this.formulario.valid) {
          const datos = this.formulario.value;
          this.formulario.reset();
            this.mostrarAlerta = true;

          // Aquí puedes enviar los datos al servidor para crear el plan de entrenamiento
          this.nutritionPlanService.createMealPlan(datos).subscribe({
            next: (respuesta) => {
              console.log('Plan Creado correctamente:', respuesta);
              this.snackBar.open('Plan Creado correctamente.', 'Cerrar', {
                duration: 3000,
                panelClass: ['snack-bar-error']});
              this.formulario.reset();
              this.mostrarAlerta = true; 
            },
            error: (error) => {
              console.error('Error al enviar datos:', error);
              this.errorMessage = 'Error al enviar los datos. Por favor, inténtalo de nuevo más tarde.';
            }
          });
      } else {
          this.snackBar.open('Error al crear el plan.', 'Cerrar', {
          duration: 3000,
          panelClass: ['snack-bar-error']});
          console.error('El formulario no es válido. Por favor, completa todos los campos.');
          this.errorMessage = 'El formulario no es válido. Por favor, completa todos los campos.';
      }
   }
}
