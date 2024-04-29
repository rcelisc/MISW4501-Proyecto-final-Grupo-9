import os

class Config:
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    # Common config settings

class DevelopmentConfig(Config):
    SQLALCHEMY_DATABASE_URI = os.environ.get('DATABASE_URL', 'postgresql://queries_user:queries_pass@localhost:5439/queries_db')

class TestingConfig(Config):
    TESTING = True
    SQLALCHEMY_DATABASE_URI = os.environ.get('TEST_DATABASE_URL', 'sqlite:///:memory:')

class ProductionConfig(Config):
    SQLALCHEMY_DATABASE_URI = os.environ.get('PROD_DATABASE_URL', 'sqlite:///user_queries_production.db')