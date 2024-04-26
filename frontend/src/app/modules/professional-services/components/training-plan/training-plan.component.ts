import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TrainingManagementService } from '../../../../services/training-management.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MaterialModule } from '../../../../shared/material.module';

@Component({
  selector: 'app-training-plan',
  standalone: true,
  imports: [],
  templateUrl: './training-plan.component.html',
  styleUrl: './training-plan.component.scss'
})
export class TrainingPlanComponent {

}
