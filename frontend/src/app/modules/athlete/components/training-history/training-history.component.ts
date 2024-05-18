import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TrainingPlanService } from '../../../../services/training-plan.service'
import { AuthService } from '../../../../services/auth.service';
import { MaterialModule } from '../../../../shared/material.module';
import { MatTableDataSource } from '@angular/material/table';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-training-history',
  standalone: true,
  imports: [MaterialModule, CommonModule],
  templateUrl: './training-history.component.html',
  styleUrl: './training-history.component.scss'
})
export class TrainingHistoryComponent implements OnInit {
  trainings: any[] = [];
  dataSource = new MatTableDataSource<any>([]);
  displayedColumns: string[] = ['tipo_entrenamiento', 'fecha', 'duracion', 'notas'];
  userId: number = 0;

  constructor(
    private trainingPlanService: TrainingPlanService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.setUserIdFromToken();
  }

  setUserIdFromToken() {
    const token = localStorage.getItem('token');
    if (token) {
      const decodedToken = this.authService.decodeToken(token);
      if (decodedToken && 'user_id' in decodedToken) {
        this.userId = decodedToken.user_id;
        this.loadTrainings();  // Load trainings after setting the user ID
      } else {
        console.error('Token is invalid or expired');
        this.router.navigate(['/login']);
      }
    } else {
      this.router.navigate(['/login']);
    }
  }

  loadTrainings() {
    if (this.userId) {
      this.trainingPlanService.getTrainingSessionByUser(this.userId).subscribe({
        next: (response:any) => {
          this.dataSource.data = response;
        },
        error: (error) => {
          console.error('Error loading trainings', error);
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/athlete-dashboard']);
  }
}
