# Usar una imagen base de Python
FROM python:3.11

WORKDIR /event_management_queries

COPY requirements.txt requirements.txt
RUN pip install -r requirements.txt

# Install Gunicorn
RUN pip install gunicorn

COPY . /event_management_queries

# Set environment variables
ENV FLASK_APP=src/main.py
ENV FLASK_ENV=development

# Expose the port app runs on
EXPOSE 3002

# Use Gunicorn to serve the app. Adjust the number of workers as needed.
CMD ["gunicorn", "--workers=3", "--bind=0.0.0.0:3002", "--timeout=120", "--log-level=debug", "--access-logfile", "-", "--error-logfile", "-", "src.main:create_app()"]
