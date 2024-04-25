import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MaterialModule } from '../../../../shared/material.module';
import { ReactiveFormsModule } from '@angular/forms';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  registerForm: FormGroup;

  constructor(
    private fb: FormBuilder, 
    private authService: AuthService, 
    private router: Router
  ){
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      surname: ['', Validators.required],
      id_type: ['', Validators.required],
      id_number: ['', Validators.required],
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
      ethnicity: [''],
    });
  }

  onRegister() {
    if (this.registerForm.valid) {
      let userData = { ...this.registerForm.value }; // Copy the form value
  
      // Check the user type and remove unnecessary fields
      if (userData.type === 'professional' || userData.type === 'organizer') {
        delete userData.age;
        delete userData.gender;
        delete userData.weight;
        delete userData.height;
        delete userData.city_of_birth;
        delete userData.country_of_birth;
        delete userData.sports;
        delete userData.profile_type;
        delete userData.ethnicity;
      }
  
      this.authService.registerUser(userData).subscribe({
        next: (response) => {
          console.log('User registered successfully:', response);
          this.router.navigate(['/iniciodeportista']);
        },
        error: (error) => {
          console.error('Registration error:', error);
        }
      });
    } else {
      console.error('Form is not valid');
    }
  }
}