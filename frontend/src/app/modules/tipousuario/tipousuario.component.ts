import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';

@Component({
  selector: 'app-tipousuario',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './tipousuario.component.html',
  styleUrls: ['./tipousuario.component.css']
})

export class TipousuarioComponent implements OnInit {

  constructor(private router: Router) { }

  ngOnInit() {
  }

  redireccionarSegunSeleccion() {
    
    console.log('Redireccionando según selección');

    const seleccion = (document.getElementById('seleccion') as HTMLSelectElement).value;
    console.log('Opcion escogida --> ', seleccion);
    switch (seleccion) {
      case 'Deportista':
        this.router.navigate(['/iniciodeportista']);
        break;
      case 'Profesional de servicios complementarios':
        this.router.navigate(['/inicioprofesional']);
        break;
      case 'Organizador de eventos':
        this.router.navigate(['/inicioorganizador']);
        break;
      default:
        // No hacer nada o manejar caso no definido
        break;
    }
  }
}
