"""Initial migration

Revision ID: 2e5f93df05ba
Revises: 
Create Date: 2024-04-15 19:49:01.498829

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '2e5f93df05ba'
down_revision = None
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('training_history',
    sa.Column('id', sa.UUID(), nullable=False),
    sa.Column('date', sa.DateTime(), nullable=True),
    sa.Column('username', sa.String(), nullable=False),
    sa.Column('sport', sa.String(), nullable=True),
    sa.Column('time', sa.String(), nullable=True),
    sa.Column('distance', sa.String(), nullable=True),
    sa.Column('weight', sa.String(), nullable=False),
    sa.Column('intensity', sa.String(), nullable=True),
    sa.Column('series', sa.String(), nullable=True),
    sa.Column('calories', sa.String(), nullable=True),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_table('training_plan',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('description', sa.String(length=255), nullable=False),
    sa.Column('exercises', sa.Text(), nullable=False),
    sa.Column('duration', sa.String(length=50), nullable=False),
    sa.Column('frequency', sa.String(length=50), nullable=False),
    sa.Column('objectives', sa.Text(), nullable=False),
    sa.Column('assigned_users', sa.Text(), nullable=True),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_table('training_session',
    sa.Column('id', sa.UUID(), nullable=False),
    sa.Column('session_id', sa.UUID(), nullable=True),
    sa.Column('user_id', sa.Integer(), nullable=False),
    sa.Column('end_time', sa.DateTime(), nullable=True),
    sa.Column('duration', sa.String(), nullable=True),
    sa.Column('notes', sa.String(), nullable=True),
    sa.Column('calories_burned', sa.String(), nullable=True),
    sa.Column('training_type', sa.String(), nullable=True),
    sa.Column('ftp', sa.Float(), nullable=True),
    sa.Column('vo2max', sa.Float(), nullable=True),
    sa.PrimaryKeyConstraint('id')
    )
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_table('training_session')
    op.drop_table('training_plan')
    op.drop_table('training_history')
    # ### end Alembic commands ###