import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CreateServiceService } from '../../../../services/create-service.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MaterialModule } from '../../../../material.module';
import { TranslateService, TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-create-service',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule, TranslateModule],
  templateUrl: './create-service.component.html',
  styleUrls: ['./create-service.component.scss']
})
export class CreateServiceComponent {
  createServiceForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private createServiceService: CreateServiceService,
    private snackBar: MatSnackBar,
    private router: Router,
    private translate: TranslateService
  ) {
    this.createServiceForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      rate: ['', [Validators.required, Validators.pattern(/^\d+$/)]]
    });
    this.translate.setDefaultLang('en');
  }

  onSubmit(): void {
    // Trigger validation for all form fields
    this.createServiceForm.markAllAsTouched();

    if (!this.createServiceForm.valid) {
      this.snackBar.open(this.translate.instant('fillRequiredFields'), 'Cerrar', {
        duration: 3000,
        panelClass: ['snack-bar-error']
      });
      return; // Return early if form is invalid
    }

    this.createServiceService.createService(this.createServiceForm.value).subscribe({
      next: (response) => {
        this.snackBar.open(this.translate.instant('serviceCreated'), 'Cerrar', { duration: 3000 });
        this.router.navigate(['/professional-dashboard']);
      },
      error: (error) => {
        this.snackBar.open(this.translate.instant('serviceCreationError'), 'Cerrar', { duration: 3000 });
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/professional-dashboard']);
  }
}
