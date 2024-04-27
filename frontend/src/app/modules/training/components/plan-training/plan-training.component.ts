import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { FormBuilder, FormGroup, Validators} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TrainingPlanService } from '../../../../services/training-plan.service';
import { MaterialModule } from '../../../../shared/material.module';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-plan-training',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule, CommonModule],
  templateUrl: './plan-training.component.html',
  styleUrl: './plan-training.component.scss'
})
export class PlanTrainingComponent {
    formulario: FormGroup;
    mostrarAlerta: boolean = false;
    errorMessage: string = '';

    constructor(
      private fb: FormBuilder, 
      private trainingPlanService: TrainingPlanService, // Servicio
      private snackBar: MatSnackBar,
      private router: Router
    ){
        this.formulario = this.fb.group({
          description: ['', Validators.required],
          exercises: ['', Validators.required],
          duration: ['', Validators.required],
          frequency: ['', Validators.required],
          objectives: ['', Validators.required],
          profile_type: ['', Validators.required]
        });
      }

      ngOnInit() {
      }

      enviarFormulario() {

        this.formulario.markAllAsTouched();

        if (!this.formulario.valid) {
          this.snackBar.open('Por favor, complete los campos requeridos.', 'Cerrar', {
            duration: 3000,
            panelClass: ['snack-bar-error'] // Apply custom CSS for styling the error snackbar
          });
          return; // Return early if form is invalid
        }

        if (this.formulario.valid) {
          console.log(this.formulario);
          const formData = this.formulario.value;
    
          this.trainingPlanService.createPlan(formData).subscribe(
            (respuesta) => {
              console.log('Datos enviados correctamente:', respuesta);
              this.snackBar.open('Datos enviados correctamente.', 'Cerrar', {
                duration: 3000,
                panelClass: ['snack-bar-error']});
              this.formulario.reset();
              this.mostrarAlerta = true; 
            },
            (error) => {
              console.error('Error al enviar datos:', error);
              this.errorMessage = 'Error al enviar los datos. Por favor, inténtalo de nuevo más tarde.';
            }
          );
        } else {
          
          console.error('El formulario no es válido. Por favor, completa todos los campos.');
          this.errorMessage = 'El formulario no es válido. Por favor, completa todos los campos.';
        }
      }

}
