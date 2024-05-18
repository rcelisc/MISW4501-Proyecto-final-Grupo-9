import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { FormBuilder, FormGroup, Validators} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TrainingPlanService } from '../../../../services/training-plan.service';
import { MaterialModule } from '../../../../shared/material.module';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-training-plan',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule, CommonModule],
  templateUrl: './training-plan.component.html',
  styleUrl: './training-plan.component.scss'
})
export class TrainingPlanComponent {
    formulario: FormGroup;
    errorMessage: string = '';

    constructor(
      private fb: FormBuilder, 
      private trainingPlanService: TrainingPlanService,
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

      enviarFormulario() {
        this.formulario.markAllAsTouched();
        if (this.formulario.valid) {
          const formData = this.formulario.value;
          this.trainingPlanService.createPlan(formData).subscribe({
            next: (response) => {
              this.snackBar.open('Training plan submitted successfully.', 'Close', {
                duration: 3000
              });
              this.formulario.reset();
              this.router.navigate(['/professional-dashboard']); 
            },
            error: (error) => {
              console.error('Error while submitting data:', error);
              this.snackBar.open('Failed to submit the training plan. Please try again later.', 'Close', {
                duration: 3000,
                panelClass: ['snack-bar-error']
              });
              this.errorMessage = 'Failed to submit data. Please try again later.';
            }
          });
        } else {
          this.snackBar.open('Please complete all required fields.', 'Close', {
            duration: 3000,
            panelClass: ['snack-bar-error']
          });
        }
      }
      goBack(): void {
        this.router.navigate(['/professional-dashboard']);
      }
}