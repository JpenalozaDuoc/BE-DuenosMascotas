# Etapa 1: Construcción con Maven y JDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS buildstage

WORKDIR /app

# Copiar archivos necesarios
COPY pom.xml .
COPY src /app/src

# Copiar el wallet
COPY src/wallet /app/wallet

# Configurar el wallet para Oracle
ENV TNS_ADMIN=/app/wallet

# Compilar la aplicación sin ejecutar los tests
RUN mvn clean package -DskipTests

# Etapa 2: Imagen de ejecución con solo JDK 17
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copiar el JAR generado desde la etapa de build
COPY --from=buildstage /app/target/*.jar /app/app.jar

# Copiar el wallet para Oracle
COPY src/wallet /app/wallet

ENV TNS_ADMIN=/app/wallet

# Puerto que expone tu aplicación
EXPOSE 8094

# Comando de inicio
ENTRYPOINT ["java", "-jar", "/app/app.jar"]


# docker build -t dueno_mascotas .
# docker run -d -p 8094:8094 --name dueno_mascotas_app --restart unless-stopped dueno_mascotas