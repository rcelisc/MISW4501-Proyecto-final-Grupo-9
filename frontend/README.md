# SportApp

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 17.3.3.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The application will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via a platform of your choice. To use this command, you need to first add a package that implements end-to-end testing capabilities.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI Overview and Command Reference](https://angular.io/cli) page.

## Internacionalization
1. Mark the Texts in Templates for Translation: `<h2 i18n="@@createServiceTitle">Crear Servicio</h2>`
2. Extract the Translation Source File: `ng extract-i18n --output-path src/locale` 
3. Translate the Content: Create a copy of this file for each language and translate the contents, like: `messages.es.xlf`
4. Serve or Build the App with Localization: 
```bash
ng serve --configuration=es
ng build --configuration=es
```

## Despliegue del frontend

1. Ensure the Firebase CLI is installed: `npm install -g firebase-tools`
2. Login to Firebase: `firebase login`
3. Initialize Firebase in your project directory: `firebase init` 
4. Build your project: `npm run build` (adjust this command based on your project setup)
5. Deploy to Firebase:
```bash
firebase deploy --only hosting
```