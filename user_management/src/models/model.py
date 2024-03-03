from sqlalchemy import Column, String, Integer, ForeignKey, Enum
from sqlalchemy.orm import declarative_base
from sqlalchemy.orm import relationship
import enum


class TipoIdentificacion(enum.Enum):
    CEDULA = "CEDULA"
    TARJETA_IDENTIDAD = "TARJETA_IDENTIDAD"

class Genero(enum.Enum):
    Hombre = "Hombre"
    Mujer = "Mujer"
    Otros = "Otros"

Base = declarative_base()

class Pais(Base):
    __tablename__  =  'paises'
    id = Column(String, primary_key=True)
    nombre = Column(String)
    ciudades = relationship('Ciudad', back_populates='pais')


class Ciudad(Base):
    __tablename__  =  'ciudades'
    id = Column(String, primary_key=True)
    nombre = Column(String)
    pais = Column(String, ForeignKey('pais.id'))
    usuarios = relationship('Usuario', back_populates='usuario')


class Usuario():
   usuario = Column(String, primary_key=True)
   contrasena = Column(String, nullable=False)
   tipo_identificacion = Column(Enum(TipoIdentificacion))
   numero_identificacion = Column(Integer, unique=True, nullable=False)
   nombre = Column(String)
   apellido = Column(String)
   antiguedad_residencia = Column(Integer)
   genero = Column(Enum(Genero))
   pais_ciudad_residencia = Column(String, ForeignKey('ciudades.id'))
   