# Guía de Pruebas Unitarias - NextDate Backend

Este documento detalla la estructura, cobertura y forma de ejecución de las pruebas unitarias desarrolladas para el backend del proyecto **NextDate**.

---

## 🛠️ Tecnologías y Patrones Utilizados

* **Framework de Pruebas:** JUnit 5 (Jupiter)
* **Mocking:** Mockito 5 (`@ExtendWith(MockitoExtension.class)`)
* **Framework Backend:** Spring Boot 3.3.0 / Java 17+
* **Patrón de Diseño:** **AAA** (Arrange, Act, Assert) en cada prueba.
* **Aislamiento:** Cero llamadas HTTP reales o dependencias de base de datos vivas.

---

## 📋 Lista de Pruebas Implementadas

### 1. Módulo de Autenticación (`com.nextdate.backend.auth`)

#### 🔐 `LoginUserServiceTest`
* **`deberiaIniciarSesionCorrectamente()`** *(Happy Path)*: Verifica que el usuario pueda iniciar sesión con credenciales válidas y retorne su entidad junto con el token JWT.
* **`deberiaLanzarExcepcionCuandoUsuarioNoExiste()`** *(Casos Negativos/Excepción)*: Valida que se lance `IllegalArgumentException` si el correo no está registrado.
* **`deberiaLanzarExcepcionCuandoContrasenaEsIncorrecta()`** *(Validaciones/Ramas)*: Garantiza el rechazo cuando el hash de la contraseña no coincide.

#### 📝 `RegisterUserServiceTest`
* **`deberiaRegistrarUsuarioCorrectamente()`** *(Happy Path)*: Comprueba que un nuevo usuario se cree con su contraseña codificada (`PasswordEncoder`) y estado activo por defecto.
* **`deberiaLanzarExcepcionCuandoCorreoYaEstaRegistrado()`** *(Recurso Duplicado)*: Evita registros duplicados lanzando `IllegalArgumentException` si el correo ya existe.

#### 📧 `RequestPasswordResetServiceTest`
* **`deberiaSolicitarRecuperacionDeContrasenaCorrectamente()`** *(Happy Path)*: Genera un token UUID único de recuperación y establece la fecha de expiración a 15 minutos en el futuro.
* **`deberiaLanzarExcepcionCuandoUsuarioNoExiste()`** *(Excepción)*: Rechaza solicitudes para correos no registrados.

#### 🔑 `ResetPasswordServiceTest`
* **`deberiaRestablecerContrasenaCorrectamente()`** *(Happy Path)*: Actualiza la contraseña encriptada y limpia el token de recuperación y la expiración.
* **`deberiaLanzarExcepcionCuandoTokenNoExiste()`** *(Token Inválido)*: Rechaza operaciones con tokens inexistentes.
* **`deberiaLanzarExcepcionCuandoTokenHaExpirado()`** *(Expiración/Boundary)*: Rechaza tokens cuya fecha de expiración sea menor a `Instant.now()`.

---

### 2. Módulo de Logística e IA (`com.nextdate.backend.logistics`)

#### 🤖 `RecommendItineraryServiceTest`
* **`deberiaRecomendarEItinerarioCorrectamente()`** *(Happy Path)*: Simula la respuesta JSON del motor de IA (Google Gemini / Concierge) y valida la creación del itinerario.
* **`deberiaLanzarExcepcionCuandoPerfilNoExiste()`** *(Recurso no encontrado)*: Falla si el perfil del usuario no existe antes de invocar a la IA.
* **`deberiaLanzarRuntimeExceptionCuandoJsonEsInvalido()`** *(Manejo de Excepciones)*: Captura respuestas malformadas de la IA y lanza una excepción descriptiva.

#### 🧠 `GeminiAiConciergeClientTest`
* **`shouldParseJsonFromGeminiResponse()`** *(Happy Path)*: Valida la extracción tipada del JSON estructurado devuelto por la API de Google Gemini.
* **`shouldCleanMarkdownFencesIfPresent()`** *(Sanitización)*: Comprueba la eliminación automática de bloques ` ```json ` si el modelo los incluye.
* **`shouldThrowWhenApiKeyIsMissing()`** *(Validación)*: Lanza excepción preventiva si `GEMINI_API_KEY` no está configurada.

#### ⏱️ `EstimateTravelTimeServiceTest`
* **Pruebas por transporte (`WALKING`, `DRIVING`, `TRANSIT`, `CYCLING`)**: Valida el cálculo exacto de tiempos en minutos según velocidades estimadas.
* **Casos Límite (Boundary)**: Verifica que para distancias `<= 0` o tipos de transporte `NONE` / `null`, el tiempo retornado sea `0`.

#### 🗺️ `OptimizeRouteServiceTest`
* **Reorganización de secuencias**: Valida que los ítems del itinerario se reordenen secuencialmente (1..N) manteniendo distancias mínimas entre ubicaciones.

---

### 3. Módulo de Experiencias (`com.nextdate.backend.experience`)

#### 📍 `CreateItineraryServiceTest`
* **`deberiaCrearItinerarioCorrectamente()`** *(Happy Path)*: Construye itinerarios asociando lugares válidos y duraciones.
* **`deberiaAplicarValoresPorDefectoEnItems()`** *(Valores por defecto)*: Asigna 60 minutos si la duración es `<= 0` y `TransportType.NONE` si el transporte es nulo.
* **`deberiaLanzarExcepcionCuandoLugarNoExiste()`** *(Validación de ID)*: Lanza excepción si algún `placeId` no existe en la base de datos.

---

### 4. Pruebas de Arquitectura de Módulos

#### 🏗️ `ModulithStructureTests`
* Verifica que los módulos (`auth`, `experience`, `logistics`) cumplan las reglas de acoplamiento e integridad definidas por **Spring Modulith**.

---

## 🚀 Cómo Ejecutar las Pruebas

Asegúrate de estar ubicado en el directorio `nextdate-backend`.

### 1. Ejecutar **todas** las pruebas unitarias
```bash
./mvnw test
```

### 2. Ejecutar una clase de prueba específica
```bash
./mvnw test -Dtest=LoginUserServiceTest
```

### 3. Ejecutar varias clases de prueba específicas
En PowerShell (Windows):
```powershell
./mvnw test "-Dtest=LoginUserServiceTest,RegisterUserServiceTest,RecommendItineraryServiceTest"
```

En Bash (Linux/macOS):
```bash
./mvnw test -Dtest=LoginUserServiceTest,RegisterUserServiceTest,RecommendItineraryServiceTest
```

---

## 📊 Reportes de Cobertura

Una vez ejecutadas las pruebas, los reportes detallados en formato TXT y XML se generan automáticamente en la siguiente ruta:

```text
nextdate-backend/target/surefire-reports/
```
