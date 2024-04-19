# Usar una imagen base de Python
FROM python:3.11

WORKDIR /user_management_commands

COPY requirements.txt requirements.txt
RUN pip install -r requirements.txt

# Install Gunicorn
RUN pip install gunicorn

COPY . /user_management_commands

# Set environment variables
ENV FLASK_APP=src/main.py
ENV FLASK_ENV=development

# Expose the port app runs on
EXPOSE 3006

# Use Gunicorn to serve the app. Adjust the number of workers as needed.
CMD ["gunicorn", "--workers=3", "--threads=2" ,"--bind=0.0.0.0:3006","--timeout=240", "--log-level=debug", "src.main:create_app()"]