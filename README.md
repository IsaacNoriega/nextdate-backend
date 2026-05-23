# NextDate Backend

El núcleo del backend de **NextDate**. Una plataforma de citas moderna diseñada bajo un enfoque de **Monolito Modular** utilizando Java 17, Spring Boot 3.3 y Spring Modulith. El sistema implementa **Arquitectura Hexagonal (Puertos y Adaptadores)**, expone un API GraphQL (BFF) y utiliza PostgreSQL con la extensión espacial PostGIS para geolocalización.

---

## 🛠️ Arquitectura y Estructura

El backend está diseñado como un **Monolito Modular**. Cada carpeta raíz de negocio dentro de `com.nextdate.backend` representa un módulo independiente y encapsulado (controlado por **Spring Modulith**):

* **`auth`**: Gestión de cuentas de usuario, registro y seguridad.
* **`experience`**: Lógica de citas, interacciones y emparejamientos (Matches).
* **`logistics`**: Logs de soporte, auditorías y tareas del sistema.

### Estructura Hexagonal de los Módulos
Cada módulo interno se organiza en tres capas para garantizar el desacoplamiento tecnológico:
1. **`domain` (El Corazón)**: Entidades inmutables, reglas del negocio y puertos de entrada/salida (interfaces). Libre de frameworks.
2. **`application` (Los Casos de Uso)**: Servicios de aplicación que implementan y coordinan la lógica del negocio.
3. **`infrastructure` (Los Detalles)**: Adaptadores de entrada (Resolvers de GraphQL) y de salida (Repositorios JPA con Hibernate, integraciones de base de datos).

---

## ⚙️ Requisitos Previos

Antes de ejecutar la aplicación, asegúrate de tener instalado:
* **Java 17 (JDK)** o superior configurado en tu entorno.
* **Docker & Docker Compose** para levantar la base de datos PostgreSQL con PostGIS.

---

## 🚀 Cómo Correr el Proyecto

Sigue estos pasos en tu terminal para levantar el entorno de desarrollo:

### 1. Iniciar la Base de Datos
En la raíz del proyecto (donde se encuentra `docker-compose.yml`), levanta el contenedor de PostgreSQL en segundo plano:
```powershell
docker compose up -d
```
* **Credenciales por defecto:**
  * **BD:** `nextdate_db`
  * **Usuario:** `nextdate_admin`
  * **Contraseña:** `nextdate_secure_pass`
  * **Puerto:** `5432`

### 2. Ejecutar el Backend
Levanta la aplicación Spring Boot utilizando el Maven Wrapper (`mvnw`):
```powershell
# En Windows (PowerShell):
.\mvnw spring-boot:run

# En Linux o macOS (Bash):
./mvnw spring-boot:run
```
La aplicación se compilará, ejecutará las migraciones pendientes con **Flyway** y levantará un servidor web en el puerto **`8080`**.

---

## 🔍 Cómo Probar la API de GraphQL

El BFF expone un único endpoint en la ruta `/graphql`. Para probar tus consultas de forma visual y cómoda:

1. Asegúrate de que el backend esté corriendo en `http://localhost:8080`.
2. Ve al **[Apollo Sandbox Explorer](https://studio.apollographql.com/sandbox/explorer)** en tu navegador.
3. En la barra de dirección superior (Endpoint URL), introduce:
   ```text
   http://localhost:8080/graphql
   ```
4. Intenta realizar la consulta de prueba `ping`:
   ```graphql
   query {
     ping
   }
   ```
   Deberías recibir la respuesta: `{"data": { "ping": "pong" }}`.
