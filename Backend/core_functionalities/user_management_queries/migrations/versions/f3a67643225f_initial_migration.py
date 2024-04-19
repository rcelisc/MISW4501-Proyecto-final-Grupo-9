"""Initial migration

Revision ID: f3a67643225f
Revises: 
Create Date: 2024-04-19 02:36:29.778798

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = 'f3a67643225f'
down_revision = None
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('user',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('name', sa.String(length=100), nullable=False),
    sa.Column('surname', sa.String(length=100), nullable=True),
    sa.Column('id_type', sa.String(length=50), nullable=True),
    sa.Column('id_number', sa.String(length=50), nullable=False),
    sa.Column('city_of_living', sa.String(length=100), nullable=True),
    sa.Column('country_of_living', sa.String(length=100), nullable=True),
    sa.Column('type', sa.String(length=50), nullable=True),
    sa.Column('age', sa.Integer(), nullable=True),
    sa.Column('gender', sa.String(length=10), nullable=True),
    sa.Column('weight', sa.Float(), nullable=True),
    sa.Column('height', sa.Float(), nullable=True),
    sa.Column('city_of_birth', sa.String(length=100), nullable=True),
    sa.Column('country_of_birth', sa.String(length=100), nullable=True),
    sa.Column('sports', sa.String(length=200), nullable=True),
    sa.Column('profile_type', sa.String(length=50), nullable=True),
    sa.PrimaryKeyConstraint('id')
    )
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_table('user')
    # ### end Alembic commands ###