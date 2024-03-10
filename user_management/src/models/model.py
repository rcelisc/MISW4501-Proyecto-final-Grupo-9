from sqlalchemy import Column, String, Integer, Enum
from sqlalchemy.orm import declarative_base
import enum

Base = declarative_base()

class TIPOIDENTIFICACION(enum.Enum):
    CEDULA = "CEDULA"
    TARJETA_IDENTIDAD = "TARJETA_IDENTIDAD"
    PASAPORTE = "PASAPORTE"
    DNI = "DNI"

class GENERO(enum.Enum):
    MASCULINO = "MASCULINO"
    FEMENINO = "FEMENINO"
    OTRO = "OTRO"

class Usuario():
   usuario = Column(String, primary_key=True)
   contrasena = Column(String, nullable=False)
   tipo_identificacion = Column(Enum(TIPOIDENTIFICACION))
   numero_identificacion = Column(Integer, unique=True, nullable=False)
   nombre = Column(String)
   apellido = Column(String)
   genero = Column(Enum(GENERO))