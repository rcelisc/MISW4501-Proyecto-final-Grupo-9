import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
//import { UserService } from '../services/user.service'; // Suponiendo que tengas un servicio para obtener la lista de usuarios


@Component({
  selector: 'app-crear-plan-alimentacion',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './crear-plan-alimentacion.component.html',
  styleUrls: ['./crear-plan-alimentacion.component.css']
})
export class CrearPlanAlimentacionComponent implements OnInit {

  formulario!: FormGroup;
  mostrarAlerta: boolean = false;
  errorMessage: string = '';
  usuarios: any[] = [
    { id: 1, nombre: 'Usuario 1' },
    { id: 2, nombre: 'Usuario 2' },
    { id: 3, nombre: 'Usuario 3' }
  ];

  constructor(private fb: FormBuilder) { }

  ngOnInit() {
    this.formulario = this.fb.group({
      usuario: ['', Validators.required],
      objetivo: ['', Validators.required],
      descripcion: ['', Validators.required],
      frecuencia: ['', Validators.required]
    });

    // // Obtener la lista de usuarios al inicializar el componente
    // this.userService.getUsuarios().subscribe(data => {
    //   this.usuarios = data;
    // });
  }

  crearPlanAlimentacion() {
    if (this.formulario.valid) {
        const datos = this.formulario.value;
        this.formulario.reset();
          this.mostrarAlerta = true;

        // Aquí puedes enviar los datos al servidor para crear el plan de entrenamiento
        
        console.log('Datos del formulario:', datos);
        
    } else {
        console.error('El formulario no es válido. Por favor, complete todos los campos.');
        this.errorMessage = 'El formulario no es válido. Por favor, completa todos los campos.';
      }
    }

}
