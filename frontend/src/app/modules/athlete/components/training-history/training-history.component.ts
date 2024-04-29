import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TrainingPlanService } from '../../../../services/training-plan.service'
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

  constructor(
    private trainingPlanService: TrainingPlanService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadTrainings();
  }

  loadTrainings(): void {
    this.trainingPlanService.getTrainingSessions().subscribe({
      next: (response:any) => {
        this.dataSource.data = response;
      },
      error: (error) => {
        console.error('Failed to load trainings', error);
      }
    });
  }
}
