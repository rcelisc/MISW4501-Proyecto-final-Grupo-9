from marshmallow import Schema, fields
from  sqlalchemy  import  Column, String #, Integer, DateTime, CheckConstraint, Enum, ForeignKey
from .model  import  Usuario
from .database import Base
# from sqlalchemy.orm import relationship
# import uuid
# from sqlalchemy.dialects.postgresql import UUID

class DeportistaNoProfesional(Usuario, Base):
    __tablename__  =  'desportitas'
    id_deporte = Column(String) # ¿un usauario puede tener diferentes deportes?
    edad = Column(String, unique=True, nullable=False)
    peso = Column(String, unique=True, nullable=False)
    altura = Column(String, unique=True, nullable=False)
    pais_cioudad_nacimiento = Column(String)
    # perfil_demografico =  relationship('PerfilDemografico', back_populates='desportitas_no_profesional', cascade="all, delete-orphan", single_parent=True)
    
    def  __init__(
            self,
            usuario,
            contrasena,
            tipo_identificacion,
            numero_identificacion,
            nombre,
            apellido,
            antiguedad_residencia,
            genero,
            pais_ciudad_residencia,
            id_deporte,
            edad,
            peso,
            altura,
            pais_cioudad_nacimiento,
        ):
        Usuario.__init__(self)
        self.usuario = usuario
        self.contrasena = contrasena
        self.tipo_identificacion = tipo_identificacion
        self.numero_identificacion = numero_identificacion
        self.nombre = nombre
        self.apellido = apellido
        self.antiguedad_residencia = antiguedad_residencia
        self.genero = genero
        self.pais_ciudad_residencia = pais_ciudad_residencia
        self.id_deporte = id_deporte
        self.edad = edad
        self.peso = peso
        self.altura = altura
        self.pais_cioudad_nacimiento = pais_cioudad_nacimiento



# class ProfesionalServiciosComplementarios(Usuario, Base):
#     __tablename__  =  'profesionales_servicios_complementarios'
#     id_deporte = Column(String) # ¿un usauario puede tener diferentes deportes?
#     edad = Column(String, unique=True, nullable=False)
#     peso = Column(String, unique=True, nullable=False)
#     altura = Column(String, unique=True, nullable=False)
#     pais_cioudad_nacimiento = Column(String)
#     # perfil_demografico =  relationship('PerfilDemografico', back_populates='desportitas_no_profesionales', cascade="all, delete-orphan", single_parent=True)
    
#     def  __init__(
#             self,
#             usuario,
#             contrasena,
#             tipo_identificacion,
#             numero_identificacion,
#             nombre,
#             apellido,
#             antiguedad_residencia,
#             genero,
#             pais_ciudad_residencia,
#             id_deporte,
#             edad,
#             peso,
#             altura,
#             pais_cioudad_nacimiento,
#         ):
#         Usuario.__init__(self)
#         self.usuario = usuario
#         self.contrasena = contrasena
#         self.tipo_identificacion = tipo_identificacion
#         self.numero_identificacion = numero_identificacion
#         self.nombre = nombre
#         self.apellido = apellido
#         self.antiguedad_residencia = antiguedad_residencia
#         self.genero = genero
#         self.pais_ciudad_residencia = pais_ciudad_residencia
#         self.id_deporte = id_deporte
#         self.edad = edad
#         self.peso = peso
#         self.altura = altura
#         self.pais_cioudad_nacimiento = pais_cioudad_nacimiento


















































# class ProfesionalServiciosComplementarios(Usuario, Base):
#     __tablename__  =  'profesionales_servicios_complementarios'
#     def  __init__(
#             self,
#             usuario,
#             contrasena,
#             tipo_identificacion,
#             numero_identificacion,
#             nombre,
#             apellido,
#             antiguedad_residencia,
#             genero,
#             pais_ciudad_residencia,
#         ):
#         Usuario.__init__(self)
#         self.usuario = usuario
#         self.contrasena = contrasena
#         self.tipo_identificacion = tipo_identificacion
#         self.numero_identificacion = numero_identificacion
#         self.nombre = nombre
#         self.apellido = apellido
#         self.antiguedad_residencia = antiguedad_residencia
#         self.genero = genero
#         self.pais_ciudad_residencia = pais_ciudad_residencia


# class OrganizadorEventosMasivos(Usuario, Base):
#     __tablename__  =  'organizadores_eventos_masivos'   
#     def  __init__(
#             self,
#             usuario,
#             contrasena,
#             tipo_identificacion,
#             numero_identificacion,
#             nombre,
#             apellido,
#             antiguedad_residencia,
#             genero,
#             pais_ciudad_residencia,
#         ):
#         Usuario.__init__(self)
#         self.usuario = usuario
#         self.contrasena = contrasena
#         self.tipo_identificacion = tipo_identificacion
#         self.numero_identificacion = numero_identificacion
#         self.nombre = nombre
#         self.apellido = apellido
#         self.antiguedad_residencia = antiguedad_residencia
#         self.genero = genero
#         self.pais_ciudad_residencia = pais_ciudad_residencia


# class PerfilFisiologico(Base):
#     __tablename__  =  'perfiles_fisiologicos'
#     id_perfil_fisiologico = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
#     etnia = Column(String) 
#     peso = Column(String)
#     altura = Column(String)
#     imc = Column(String)
#     persion_arterial = Column(String)
#     frecuencia_cardiaca = Column(String)
#     termperatura_corporal = Column(String)
#     quimica_sanguinia = Column(String)
#     funcion_pulmonar = Column(String)
#     fuerza_muscular = Column(String)
#     resistencia_cardiovascular = Column(String)
#     flexibilidad = Column(String)


# class PerfilDemografico(Base):
#     __tablename__  =  'perfiles_demograficos'
#     id_perfil_demografico = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
#     estado_civil = Column(String)
#     ingresos = Column(String)
#     raza = Column(String)
#     ocupacion = Column(String)
#     religion = Column(String)
#     usuario = Column(String, ForeignKey('desportitas_no_profesionales.usuario'))


class  DeportistaNoProfesionalesSchema(Schema):
    usuario = fields.Str()
    contrasena = fields.Str()
    tipo_identificacion = fields.Str()
    numero_identificacion = fields.Str()
    nombre = fields.Str()
    apellido = fields.Str()
    antiguedad_residencia = fields.Str()
    genero = fields.Str()
    pais_ciudad_residencia = fields.Str()
    id_deporte = fields.Str()
    edad = fields.Str()
    peso = fields.Str()
    altura = fields.Str()
    pais_cioudad_nacimiento = fields.Str()


class  ProfesionalServiciosComplementariosSchema(Schema):
    usuario = fields.Str()
    contrasena = fields.Str()
    tipo_identificacion = fields.Str()
    numero_identificacion = fields.Str()
    nombre = fields.Str()
    apellido = fields.Str()
    antiguedad_residencia = fields.Str()
    genero = fields.Str()
    pais_ciudad_residencia = fields.Str()





