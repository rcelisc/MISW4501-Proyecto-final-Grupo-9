# Grupo 09 - Proyecto final

A continuación, presentamos el paso a paso para desplegar la aplicación desarrollado y obtener un despliegue como el que se presenta en la imagen a continuación:

![Figura 6](https://github.com/MISW-4301-Desarrollo-Apps-en-la-Nube/s202314-proyecto-grupo24/assets/111831086/a8ec50f1-6c32-4499-9015-3742b325c36c)


## 1. Descargar el repositorio desde github en la siguiente URL: 
           (https://github.com/MISW-4301-Desarrollo-Apps-en-la-Nube/s202314-proyecto-grupo24.)

Es importante que siempre trabajes sobre un ambiente virtual y tengas todas las dependencias necesarias, para ello se recomineda seguir estos pasos:
 - ``python3 -m venv venv``
 - ``source venv/bin/activate``
 - ``pip install -r requirements.txt``
 - ``export FLASK_APP=src/main.py``
 - ``export GOOGLE_APPLICATION_CREDENTIALS="../../../../../service-account.json"``

 - ``coverage run -m unittest discover -s tests -v``
 - ``coverage report -m``
 - ``export STRAVA_CLIENT_SECRET=strava_client_secret``
 - ``export STRAVA_CLIENT_ID=strava_client_id``
 - ``export STRAVA_REDIRECT_URI=http://localhost:3004/strava_callback``
 - ``export FLASK_SECRET_KEY=default_secret_key``

## 2. Instrucciones de Ejecución de la Aplicacion con docker compose y localmente
Esta sesion proporciona las instrucciones necesarias para desplegar y ejecutar los servicios tanto en Docker como localmente.

Despliegue con Docker Compose
1. Ejecutar Docker Compose
Para iniciar todos los servicios definidos en el archivo docker-compose.yml, ejecuta el siguiente comando:

``docker-compose up --build``

Este comando complila y levanta todos los contenedores necesarios para el funcionamiento de la aplicación, incluidos bases de datos y demás servicios.

2. Bajar todos los servicios definidos en el archivo docker-compose.yml y los volumenes creados:

``docker-compose down -v``


3. Ejecutar Migraciones
Una vez que los servicios están corriendo, necesitas ejecutar las migraciones para configurar las bases de datos. Utiliza los siguientes comandos para aplicar migraciones en cada servicio:

``docker-compose exec <nombre_del_servicio> flask db upgrade``
Reemplaza <nombre_del_servicio> con el nombre de cada servicio para el que deseas ejecutar migraciones.

Si necesitas hacer el proceso completo por alguna inconsistencia:
```bash
docker-compose exec event_management_commands rm -r migrations
docker-compose exec event_management_commands flask db init
docker-compose exec event_management_commands flask db migrate -m "Initial migration"
docker-compose exec event_management_commands flask db upgrade
```
## Ejecución Local
Para ejecutar los servicios localmente, sigue los siguientes pasos:

1. Configuración de GCP

Necesitas tener descarga la llave de la cuenta de servicio para poder manipular los pub subs:
- Go to the Google Cloud Console:
  Navigate to the Google Cloud Console.
- Access IAM & Admin:
  On the left sidebar, select IAM & Admin > Service Accounts.
- Select Your Service Account:
  Find the service account you mentioned (sport-app@miso-proyecto-de-grado-g09.iam.gserviceaccount.com) from the list.
- Create Key:
  Click on the service account name to open the account details.
  Click on the Keys tab.
  Click on Add Key, then select Create new key.
  Choose JSON as the key type, then click Create.
  The key file will automatically download to your computer. Ensure to save it in a secure location immediately, as it contains sensitive information and cannot be downloaded again.

2. Crear Bases de Datos en Docker
Puedes crear las bases de datos en contenedores Docker ejecutando comandos similares a los siguientes:

```bash
docker run --name <nombre_bd> -e POSTGRES_USER=<usuario> -e POSTGRES_PASSWORD=<contraseña> -d postgres
```
Reemplaza <nombre_bd>, <usuario>, y <contraseña> con los valores adecuados, guiate del archivo ``config.py`` en el src de cada servicio, ahi podras encontrar cada dato.

3. Ejecutar Migraciones Localmente
Asegúrate de tener las variables de entorno adecuadas para conectar a tus bases de datos, es decir la base de datos que creaste anteriormente debe tener una variable de entorno asociada al servicio llamada `DATABASE_URL` exportala para que sea visible a la sesion de donde vas a lanzar el servicio. Luego, ejecuta las migraciones como se indica:

```bash
flask db upgrade
```
4. Establecer Variables de Entorno
Establece las variables de entorno necesarias para conectar con las bases de datos. Esto depende de tu sistema operativo, pero en general puedes hacerlo de la siguiente manera:
```bash
export DATABASE_URL=postgresql://usuario:contraseña@localhost:5432/nombre_bd
```
```bash 
export FLASK_APP=src/main.py 
```
5. Ejecutar los Servicios en Puertos Específicos
Finalmente, ejecuta cada servicio en un puerto específico usando Flask o tu servidor WSGI preferido:

```bash
flask run --port 3001
```
Reemplaza 3001 con el puerto correspondiente para cada servicio.

## 3. Despliegue con GCP
1. Para desplegar:
```bash 
kubectl apply -f k8s/base/
```

2. Aplicar migraciones:
```bash 
kubectl apply -f k8s/migrations/training-management-commands-job.yaml
kubectl apply -f k8s/migrations/
```

3. Aplicar ingress:
```bash 
kubectl apply -f ingress.yaml
```

4. Para eliminar el despliegue:
```bash 
kubectl delete -f k8s/base/
kubectl delete -f ingress.yaml
```

## 4. Crear clúster en GCP.

Antes de crear el clúster en GCP debemos crea las redes que utilizaron el despliegue en general tanto para el clúster como para la base de datos, teniendo en cuenta los siguientes parámetros:

- **Red virtual:** vpn-devnativa-24 
- **Subred (para el clúster de k8s):** red-k8s-devnativa-24
- **Subred para la base de datos:** red-dbs-devnativa-24
- **Rango de direcciones del pod:** 192.168.64.0/21 
- **Rango de direcciones del servicio:** 192.168.72.0/21
- **Dirección de la base de datos:** 192.168.0.3

El **Clúster** se debe crear en la red y la subred definida para este, con: 

- **Nombre:** cluster-devnativa-24

Imagen del cluster una vez a sido creado.

![image](https://github.com/MISW-4301-Desarrollo-Apps-en-la-Nube/s202314-proyecto-grupo24/assets/111831086/41c9a985-4e32-4701-a706-56cbcb3c0e07)


## 5. Crear servicios, funciones e Ingress en GCP.

En la carpeta  “**deployment**” dentro de la solución, se generan 3 archivos *.yaml los cuales contienen la configuración para la creación de los servicios y el **Ingress** de la siguiente manera: 

* **k8s-componentes-entrega-3.yaml**: Este archivo contiene la configuración y despliegue de los servicios credit-card, usermangement.
* **k8s-true-native-deployment.yaml**: Este archivo contiene la configuración y despliegue de los servicios de TrueNative.
* **k8s-ingress-deloyment.yaml**: Contiene la configuración y despliegue del componente Ingress en GCP de los servicios desarrollados para la funcionalidad y de TrueNative.

> IMPORTANTE Tener en cuenta que los servicios internamente se pueden comunicar debido a la configuración del **Service Discovery**, y el cual fue configurado en cada Deployment de los PODs para poder comunicarse dentro del **Cluster** con los servicios que cada uno requiere.
>
> ![image](https://github.com/MISW-4301-Desarrollo-Apps-en-la-Nube/s202314-proyecto-grupo24/assets/111831086/063d0b1f-9948-42f8-a6f1-b3e8873eaa6e)



* Ejecutar los siguientes comandos en su respectivo orden, para desplegar los **PODs**, los **Servicios**, y crear & configurar el **Ingress** en GCP.

1.	Ejecutar comando:  **`kubectl apply -f k8s-componentes-entrega-3.yaml`**
2.	Ejecutar comando:  **`kubectl apply -f k8s-true-native-deployment.yaml`**


En **Kubernets > Cargas de Trabajo** (Debe ver lo siguiente)

![image](https://github.com/MISW-4301-Desarrollo-Apps-en-la-Nube/s202314-proyecto-grupo24/assets/111831086/30dc1ecc-cd6c-478f-ba21-6cf5f1bf0664)


En **Kubernets > Ingress y Servicios > Servicios (Pestaña)** (Debe ver lo siguiente) 

![image](https://github.com/MISW-4301-Desarrollo-Apps-en-la-Nube/s202314-proyecto-grupo24/assets/111831086/893c1fe8-97f7-41e9-bd4b-029914a0e493)


3.	Ejecutar comando: **kubectl apply -f k8s-ingress-deloyment.yaml**

> IMPORTANTE: Para que ##**Ingress** pueda realizar el **HealtChek** de los servicios que va a exponer, se debe realizar la siguiente configuración en cada uno de los servivios a exponer.
>
> Esta configuración se realiza en **kind: BackendConfig** y  **kind: Service**.
> 
> ![image](https://github.com/MISW-4301-Desarrollo-Apps-en-la-Nube/s202314-proyecto-grupo24/assets/111831086/39f7cfd2-9548-4b33-b788-a5cbff940633)



Finalmente en **Kubernets > Ingress y Servicios > Ingress (Pestaña)** (Debe ver lo siguiente), este proceso toma bastante tiempo en quedar en estado **OK**, 10-15 min aproximandamente.

![image](https://github.com/MISW-4301-Desarrollo-Apps-en-la-Nube/s202314-proyecto-grupo24/assets/111831086/37a48a9b-ed5d-498c-ba8d-4a2449c74497)


4. **Para crear la función de enviar mail** Ejecutar el siguiente comando.
 
**`gcloud functions deploy funcion-enviar-mail --entry-point enviar_mail --runtime python39 --trigger-http --allow-unauthenticated --memory 128MB --region us-central1 --timeout 60 --min-instances 0 --max-instances 1`**

Al terminar de correr este comando y si no hay errores en su ejecución, en la seccion de **Cloud Functions de GCP** debe ver lo siguiente:

![image](https://github.com/MISW-4301-Desarrollo-Apps-en-la-Nube/s202314-proyecto-grupo24/assets/111831086/7e56674d-1a04-47e2-9950-5a99df7efa09)

Al entrar al detalle de la funcion debe ver lo siguiente:

![image](https://github.com/MISW-4301-Desarrollo-Apps-en-la-Nube/s202314-proyecto-grupo24/assets/111831086/917b253b-e76c-4040-afe6-8a6245164d61)


## 6. Tomar la IP pública de Ingress.

Una vez finalizado el despliegue del **Ingress**, se debe validar que el estado este en **OK**, lo cual nos indicara que **Ingress** a logrado hacer validación “**Healtcheck**” de todos los servicios expuestos, y nos indicara que el despliegue se ha realizado con éxito.

En la columna **Frontends** se pueden visualizar cada uno de los servicios expuestos y la **IP** pública con la cual vamos a poder realizar las peticiones a nuestro sistema.


![image](https://github.com/MISW-4301-Desarrollo-Apps-en-la-Nube/s202314-proyecto-grupo24/assets/111831086/f448d361-7a75-4ab8-9d97-d1d3d1712930)


## 7. Configurar en el archivo **config.yaml** la IP tomada de GCP Ingress. (Esto para correr el Pipeline de la entrega 3)

Este paso se debe realizar para que el pipeline pueda correr las pruebas hacia el proyecto correcto y no vaya a generar errores.

![image](https://github.com/MISW-4301-Desarrollo-Apps-en-la-Nube/s202314-proyecto-grupo24/assets/111831086/399562bc-ea3a-4520-b6ba-d092eca6e7d1)


## 8. Pruebas desde postman.

* En su colección de Postman deben ingresar en la sección variables y allí deben colocar en la variable **"INGRESS_PATH"** la **IP** obtenida del **Ingress** en el **punto 7**.

![image](https://github.com/MISW-4301-Desarrollo-Apps-en-la-Nube/s202314-proyecto-grupo24/assets/111831086/ef8bde64-53bb-40de-a3ce-0ee3a8395e09)



* Luego de Configurar la **IP** proceda a ejecutar las pruebas. 

Al realizar la ejecucion de **folder**  o **colecciones** desde postman se debe ver de la siguiente manera. 

![image](https://github.com/MISW-4301-Desarrollo-Apps-en-la-Nube/s202314-proyecto-grupo24/assets/111831086/a25a7f98-9fb4-4dda-b288-68df10a4d5b7)
