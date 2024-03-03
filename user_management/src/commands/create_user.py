# from .base_command import BaseCommannd
# from ..errors.errors import InvalidParams, EmailUsernameExist
# from sqlalchemy.exc import IntegrityError
# from psycopg2.errors import UniqueViolation
# from ..models.database import db_session #, init_db
# from ..models.users import DeportistaNoProfesional
# # import os, requests, uuid


# class CreateUser(BaseCommannd):
#   def __init__( 
#         self, 
#         tipo_identificacion,
#         numero_identificacion,
#         nombre,
#         apellido,
#         antiguedad_residencia,
#         genero,
#         pais_ciudad_residencia,
#         id_deporte,
#         edad,
#         peso,
#         altura,
#         pais_cioudad_nacimiento,
#         contrasena,
#       ):
#     self.tipo_identificacion = tipo_identificacion 
#     self.numero_identificacion = numero_identificacion 
#     self.nombre = nombre 
#     self.apellido = apellido 
#     self.antiguedad_residencia = antiguedad_residencia 
#     self.genero = genero 
#     self.pais_ciudad_residencia = pais_ciudad_residencia 
#     self.id_deporte = id_deporte 
#     self.edad = edad 
#     self.peso = peso 
#     self.altura = altura 
#     self.pais_cioudad_nacimiento = pais_cioudad_nacimiento 
#     self.contrasena = contrasena 

#   def execute(self):
#     usuario_nuevo = DeportistaNoProfesional(
#             self.tipo_identificacion,
#             self.numero_identificacion,
#             self.nombre,
#             self.apellido,
#             self.antiguedad_residencia,
#             self.genero,
#             self.pais_ciudad_residencia,
#             self.id_deporte,
#             self.edad,
#             self.peso,
#             self.altura,
#             self.pais_cioudad_nacimiento,
#             self.contrasena,
#           )
    
#     db_session.add(usuario_nuevo)
    
#     if (
#         self.tipo_identificacion == "" or
#         self.numero_identificacion == "" or
#         self.nombre == "" or
#         self.apellido == "" or
#         self.antiguedad_residencia == "" or
#         self.genero == "" or
#         self.pais_ciudad_residencia == "" or
#         self.id_deporte == "" or
#         self.edad == "" or
#         self.peso == "" or
#         self.altura == "" or
#         self.pais_cioudad_nacimiento == "" or
#         self.contrasena == "" 
#        ):
#       raise InvalidParams
    
#     else:
#       try:
#         db_session.commit()
#         response = {"id":usuario_nuevo.numero_identificacion, "Nombre":usuario_nuevo.nombre}
#         db_session.close()
#         #Funcion - Native/Verifiy.
#         # CreateUser.CreateTaskVerify(self, str(u.id))

#         return response
#       except IntegrityError  as e:
#         if isinstance(e.orig, UniqueViolation):
#           db_session.close()
#           raise EmailUsernameExist


#   # def CreateTaskVerify(self, idUser):
#   #     url_api_native=os.environ["NATIVE_PATH"]

#   #     headers={'Authorization':f'Bearer {os.environ["SECRET_TOKEN"]}'}
#   #     json_data = {
#   #                   "transactionIdentifier": str(uuid.uuid4()),
#   #                   "userIdentifier": idUser,
#   #                   "userWebhook": str(os.environ["USERS_PATH"]) + "/users",
#   #                         "user": {
#   #                                   "email": self.email,
#   #                                   "dni": self.dni,
#   #                                   "fullName": self.email,
#   #                                   "phone": self.phoneNumber
#   #                                 }
#   #                 }
#       # response=requests.post(url_api_native +'/native/verify',json=json_data,headers=headers)

#       if (response.status_code==201):
#           return response
#       else:
#             return response

