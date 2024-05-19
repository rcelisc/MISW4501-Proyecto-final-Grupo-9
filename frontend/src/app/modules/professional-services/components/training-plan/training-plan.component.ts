import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TrainingPlanService } from '../../../../services/training-plan.service';
import { MaterialModule } from '../../../../material.module';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslateService, TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-training-plan',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule, CommonModule, TranslateModule],
  templateUrl: './training-plan.component.html',
  styleUrls: ['./training-plan.component.scss']
})
export class TrainingPlanComponent {
  formulario: FormGroup;
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder, 
    private trainingPlanService: TrainingPlanService,
    private snackBar: MatSnackBar,
    private router: Router,
    private translate: TranslateService
  ) {
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
          this.snackBar.open(this.translate.instant('trainingPlanSubmitSuccess'), 'Close', {
            duration: 3000
          });
          this.formulario.reset();
          this.router.navigate(['/professional-dashboard']); 
        },
        error: (error) => {
          console.error('Error while submitting data:', error);
          this.snackBar.open(this.translate.instant('trainingPlanSubmitError'), 'Close', {
            duration: 3000,
            panelClass: ['snack-bar-error']
          });
          this.errorMessage = this.translate.instant('trainingPlanSubmitErrorDetail');
        }
      });
    } else {
      this.snackBar.open(this.translate.instant('trainingPlanCompleteFields'), 'Close', {
        duration: 3000,
        panelClass: ['snack-bar-error']
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/professional-dashboard']);
  }
}
