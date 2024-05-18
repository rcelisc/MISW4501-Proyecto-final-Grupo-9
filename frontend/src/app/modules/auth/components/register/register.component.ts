import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MaterialModule } from '../../../../shared/material.module';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

type UserType = 'athlete' | 'complementary_services_professional' | 'event_organizer';

const routes: { [key in UserType]: string } = {
  athlete: '/athlete-dashboard',
  complementary_services_professional: '/professional-dashboard',
  event_organizer: '/organizer-dashboard'
};

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      surname: ['', Validators.required],
      id_type: ['', Validators.required],
      id_number: ['', Validators.required],
      password: ['', Validators.required],
      city_of_living: [''],
      country_of_living: [''],
      type: ['', Validators.required],
      // Athlete specific
      age: [''],
      gender: [''],
      weight: [''],
      height: [''],
      city_of_birth: [''],
      country_of_birth: [''],
      sports: [''],
      profile_type: [''],
    });
  }

  ngOnInit(): void {
    this.registerForm.get('type')?.valueChanges.subscribe(userType => {
      this.updateValidators(userType);
    });
  }

  updateValidators(userType: UserType) {
    const athleteFields = ['age', 'gender', 'weight', 'height', 'city_of_birth', 'country_of_birth', 'sports', 'profile_type'];

    if (userType === 'athlete') {
      athleteFields.forEach(field => {
        this.registerForm.get(field)?.setValidators(Validators.required);
        this.registerForm.get(field)?.updateValueAndValidity();
      });
    } else {
      athleteFields.forEach(field => {
        this.registerForm.get(field)?.clearValidators();
        this.registerForm.get(field)?.updateValueAndValidity();
      });
    }
  }

  onRegister() {
    if (this.registerForm.valid) {
      let userData = this.registerForm.value;
      const userType = userData.type as UserType;

      // Clear unrelated data for non-athlete types
      if (userType !== 'athlete') {
        const fieldsToRemove = ['age', 'gender', 'weight', 'height', 'city_of_birth', 'country_of_birth', 'sports', 'profile_type'];
        fieldsToRemove.forEach(field => delete userData[field]);
      }

      this.authService.registerUser(userData).subscribe({
        next: (response) => {
          console.log('User registered successfully:', response);
          this.router.navigate(['/login']);
        },
        error: (error) => {
          console.error('Registration error:', error);
          this.snackBar.open('Error al crear el evento', 'Cerrar', { duration: 3000 });
        }
      });
    } else {
      console.error('Form is not valid');
      this.snackBar.open('Por favor, complete los campos requeridos.', 'Cerrar', { duration: 3000 });
    }
  }

  goBack(): void {
    this.router.navigate(['/']);
  }
}
