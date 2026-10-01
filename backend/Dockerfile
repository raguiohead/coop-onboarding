# ==========================================
# 1. Estágio de Compilação (Build Stage)
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Instala curl e bash para execução do wrapper Maven
RUN apk add --no-cache bash curl

# Cache de dependências Maven
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B || true

# Copia código-fonte e compila artefato executável (.jar)
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ==========================================
# 2. Estágio de Execução (Runtime Stage)
# ==========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Cria usuário não-root por segurança corporativa
RUN addgroup -S coopgroup && adduser -S coopuser -G coopgroup
USER coopuser

# Copia o JAR gerado do estágio anterior
COPY --from=builder --chown=coopuser:coopgroup /app/target/*.jar app.jar

# Configuração de portas e variáveis JVM otimizadas para contêiner
EXPOSE 8080
ENV JAVA_OPTS="-XX:+UseSerialGC -Xms128m -Xmx320m -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
