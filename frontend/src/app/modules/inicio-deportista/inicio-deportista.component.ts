import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-inicio-deportista',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './inicio-deportista.component.html',
  styleUrls: ['./inicio-deportista.component.css']
})
export class InicioDeportistaComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
