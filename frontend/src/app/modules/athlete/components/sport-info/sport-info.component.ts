import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SportInfoService } from '../../../../services/sport-info.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MaterialModule } from '../../../../material.module';
import { AuthService } from '../../../../services/auth.service';
import { TranslateService, TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-sport-info',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule, TranslateModule],
  templateUrl: './sport-info.component.html',
  styleUrls: ['./sport-info.component.scss']
})
export class SportInfoComponent implements OnInit {
  sportInfoForm: FormGroup;
  userId: number = 0;

  constructor(
    private fb: FormBuilder,
    private sportInfoService: SportInfoService,
    private snackBar: MatSnackBar,
    private router: Router,
    private authService: AuthService,
    private translate: TranslateService
  ) {
    this.translate.setDefaultLang('en');
    this.sportInfoForm = this.fb.group({
      training_frequency: ['', Validators.required],
      sports_practiced: ['', Validators.required],
      average_session_duration: ['', Validators.required],
      recovery_time: ['', Validators.required],
      training_pace: ['', Validators.required],
    });
  }

  switchLanguage(language: string) {
    this.translate.use(language);
  }

  ngOnInit(): void {
    this.setUserIdFromToken();
  }

  setUserIdFromToken() {
    const token = localStorage.getItem('token');
    if (token) {
      const decodedToken = this.authService.decodeToken(token);
      if (decodedToken && 'user_id' in decodedToken) {
        this.userId = decodedToken.user_id;
      } else {
        console.error('Token is invalid or expired');
        this.router.navigate(['/login']); // Redirect to login if the token is invalid or expired
      }
    } else {
      this.router.navigate(['/login']); // Redirect to login if there's no token
    }
  }

  onSubmit(): void {
    // Trigger validation for all form fields
    this.sportInfoForm.markAllAsTouched();

    if (!this.sportInfoForm.valid) {
      this.translate.get('requiredFieldsError').subscribe((res: string) => {
        this.snackBar.open(res, 'Close', {
          duration: 3000,
          panelClass: ['snack-bar-error']
        });
      });
      return;
    }

    this.sportInfoService.createSportInfo(this.userId, this.sportInfoForm.value).subscribe({
      next: (response) => {
        this.translate.get('sportInfoAddedSuccess').subscribe((res: string) => {
          this.snackBar.open(res, 'Close', { duration: 3000 });
          this.router.navigate(['/athlete-dashboard']);
        });
      },
      error: (error) => {
        this.translate.get('sportInfoAddedError').subscribe((res: string) => {
          this.snackBar.open(res, 'Close', { duration: 3000 });
        });
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/athlete-dashboard']);
  }
}
