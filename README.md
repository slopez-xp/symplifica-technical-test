# Prueba Técnica Fullstack Junior — Symplifica

Solución completa para el reto técnico de Symplifica. Incluye una API REST en Spring Boot y un dashboard interactivo en Vue 3.

---

## Estructura del proyecto

```
PruebaTecnicaSymplifica/
├── employees-api/        → Backend: API REST con Spring Boot
└── employees-frontend/   → Frontend: Dashboard con Vue 3
```

---

## Tecnologías utilizadas

**Backend**
- Java 21
- Spring Boot 4.1.0
- Spring Data JPA
- H2 Database (en memoria)
- Spring Security + JWT (jjwt 0.12.6)
- OpenStreetMap Nominatim API

**Frontend**
- Vue 3
- Vite
- Font Awesome 6
- Leaflet.js (mapas interactivos)
- Google Fonts (Schoolbell)

---

## Opción 1 — Docker (recomendado)

```bash
docker-compose up --build
```

- Frontend: http://localhost
- Backend: http://localhost:8080
- Credenciales: `admin` / `symplifica123`

---

## Opción 2 — Ejecución manual

### Requisitos previos
- Java 21
- Node.js 18 o superior
- Maven (o usar el wrapper `./mvnw` incluido)

### Backend

```bash
cd employees-api
./mvnw spring-boot:run
```

El servidor arranca en `http://localhost:8080`

### Frontend

```bash
cd employees-frontend
npm install
npm run dev
```

El dashboard abre en `http://localhost:5173`

> Ambos deben estar corriendo al mismo tiempo.

---

## Autenticación

Todas las rutas de la API están protegidas con JWT excepto `/auth/login`.

**Login:**
```
POST http://localhost:8080/auth/login
{
  "username": "admin",
  "password": "symplifica123"
}
```

El token retornado debe enviarse en cada request:
```
Authorization: Bearer <token>
```

El token expira en 8 horas.

---

## Funcionalidades implementadas

**Empleados**
- Listar, crear, editar y eliminar empleados
- Validación de campos obligatorios y formato de email
- Coordenadas geográficas en tiempo real desde OpenStreetMap Nominatim

**Beneficios**
- CRUD completo de beneficios asociados a cada empleado
- Gestión desde modal por empleado

**Dashboard**
- Mapa interactivo mundial con Leaflet
- Al seleccionar un empleado el mapa vuela a su ubicación
- Métricas en tiempo real: empleados, beneficios y empleados georeferenciados
- Tema oscuro y diseño responsive

---

## Endpoints disponibles

### Autenticación
| Método | Ruta | Descripción |
|---|---|---|
| POST | /auth/login | Obtener token JWT |

### Empleados
| Método | Ruta | Descripción |
|---|---|---|
| GET | /api/employees | Listar todos |
| POST | /api/employees | Crear empleado |
| GET | /api/employees/{id} | Detalle con coordenadas |
| PUT | /api/employees/{id} | Actualizar empleado |
| DELETE | /api/employees/{id} | Eliminar empleado |

### Beneficios
| Método | Ruta | Descripción |
|---|---|---|
| GET | /api/employees/{id}/benefits | Listar beneficios |
| POST | /api/employees/{id}/benefits | Agregar beneficio |
| DELETE | /api/employees/{id}/benefits/{bid} | Eliminar beneficio |

---

## Ejemplo de uso

**Login:**
```json
POST /auth/login
{
  "username": "admin",
  "password": "symplifica123"
}
```

**Crear empleado:**
```json
POST /api/employees
{
  "name": "María García",
  "email": "maria@symplifica.com",
  "city": "Bogotá"
}
```

**Agregar beneficio:**
```json
POST /api/employees/1/benefits
{
  "name": "Seguro médico",
  "amount": 150000
}
```

---

## Arquitectura del backend

```
security/     → JWT, filtros y configuración de Spring Security
controller/   → recibe y responde peticiones HTTP
service/      → lógica de negocio
repository/   → acceso a base de datos (JPA)
model/        → entidades y DTOs
```

---

## Pruebas unitarias

```bash
cd employees-api
./mvnw test
```

Cubre: `getAll`, `create`, `delete` y manejo de errores en `update`.

---

## Decisiones técnicas

- **H2 en memoria** — simplifica el setup para evaluación. Reemplazable por PostgreSQL cambiando la dependencia y `application.properties`.
- **Cache de coordenadas** — las coordenadas de cada ciudad se almacenan en memoria durante la sesión para respetar el límite de 1 req/seg de Nominatim.
- **Arquitectura por capas** — separación estricta Controller → Service → Repository siguiendo principios MVC y Clean Code.
- **Security en paquete propio** — JWT, filtros y configuración de seguridad agrupados en `/security` por cohesión, no dispersados en las carpetas MVC.
- **Backend único** — se consolidaron employees y benefits en una sola API para simplificar el setup de evaluación.
- **Credenciales hardcodeadas** — en producción se usarían variables de entorno (`JWT_SECRET`, `ADMIN_PASSWORD`).

---

## Limitaciones conocidas

- **H2 es volátil** — los datos se pierden al reiniciar. Los datos de ejemplo se recargan automáticamente desde `data.sql`.
- **Nominatim rate limit** — permite 1 solicitud por segundo. En la primera carga puede haber un breve retraso. Las ciudades consultadas se cachean para no repetir llamadas.
- **Usuario único hardcodeado** — sistema de autenticación de demostración. En producción se implementaría una base de usuarios con contraseñas encriptadas.

---

## Requerimientos opcionales implementados

- ✅ Autenticación JWT
- ✅ Pruebas unitarias
- ✅ Docker

---

## English summary

Full-stack solution for the Symplifica technical challenge.

**Backend:** REST API built with Java 21 and Spring Boot. Full CRUD for employees and benefits. JWT authentication protecting all endpoints. OpenStreetMap Nominatim integration for real-time geocoding with in-memory caching.

**Frontend:** Vue 3 dashboard with an interactive Leaflet map, employee table with inline actions, benefits modal per employee, and login/logout flow.

**Run with Docker:** `docker-compose up --build` — frontend at http://localhost, backend at http://localhost:8080.

**Credentials:** `admin` / `symplifica123`