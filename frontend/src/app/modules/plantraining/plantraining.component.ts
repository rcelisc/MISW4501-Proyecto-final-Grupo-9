import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TrainingPlanService } from '../../services/training-plan.service';
import { TrainingPlanRequest } from '../../models/plan-training.model';

@Component({
  selector: 'app-plantraining',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './plantraining.component.html',
  styleUrls: ['./plantraining.component.css']
})
export class PlantrainingComponent implements OnInit {

  formulario: FormGroup;
  mostrarAlerta: boolean = false;
  errorMessage: string = '';

  constructor(private fb: FormBuilder, private trainingPlanService: TrainingPlanService) {
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
    if (this.formulario.valid) {
      console.log(this.formulario);
      const formData = this.formulario.value;

      this.trainingPlanService.createPlan(formData).subscribe(
        (respuesta) => {
          console.log('Datos enviados correctamente:', respuesta);
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
