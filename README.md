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
- OpenStreetMap Nominatim API

**Frontend**
- Vue 3
- Vite
- Font Awesome 6
- Leaflet.js (mapas interactivos)
- Google Fonts (Schoolbell)

---

## Requisitos previos

- Java 21
- Node.js 18 o superior
- Maven (o usar el wrapper `./mvnw` incluido)

---

## Cómo ejecutar el proyecto

### 1. Backend

```bash
cd employees-api
./mvnw spring-boot:run
```

El servidor arranca en `http://localhost:8080`

### 2. Frontend

```bash
cd employees-frontend
npm install
npm run dev
```

El dashboard abre en `http://localhost:5173`

> Ambos deben estar corriendo al mismo tiempo.

---

## Funcionalidades implementadas

**Empleados**
- Listar, crear, editar y eliminar empleados
- Validación de campos obligatorios y formato de email
- Coordenadas geográficas obtenidas en tiempo real desde OpenStreetMap Nominatim
- Datos de ejemplo cargados automáticamente al iniciar el servidor

**Beneficios**
- CRUD completo de beneficios asociados a cada empleado
- Gestión desde un modal por empleado

**Dashboard**
- Mapa interactivo mundial con Leaflet — muestra la ubicación de cada empleado
- Al seleccionar un empleado en la tabla, el mapa vuela a su ubicación
- Métricas en tiempo real: total de empleados, beneficios y empleados georeferenciados
- Tema oscuro y diseño responsive

---

## Endpoints disponibles

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

**Detalle con coordenadas:**
```json
GET /api/employees/1
{
  "employee": { "id": 1, "name": "María García", "city": "Bogotá" },
  "location": { "latitude": "4.6533817", "longitude": "-74.0836331" }
}
```

---

## Arquitectura del backend

```
controller/   → recibe y responde peticiones HTTP
service/      → lógica de negocio
repository/   → acceso a base de datos (JPA)
model/        → entidades y DTOs
```

---

## Decisiones técnicas

- **H2 en memoria** — simplifica el setup para evaluación. Reemplazable por PostgreSQL cambiando la dependencia y `application.properties`.
- **Cache de coordenadas** — las coordenadas de cada ciudad se almacenan en memoria durante la sesión para respetar el límite de 1 req/seg de Nominatim.
- **Arquitectura por capas** — separación estricta Controller → Service → Repository siguiendo principios MVC y Clean Code.
- **Backend único** — se optó por un solo backend en Spring Boot en lugar de dos servicios separados, consolidando employees y benefits en una sola API para simplificar el setup de evaluación.

---

## Limitaciones conocidas

- **H2 es volátil** — los datos se pierden al reiniciar el servidor. Los datos de ejemplo se recargan automáticamente desde `data.sql`.
- **Nominatim rate limit** — la API de OpenStreetMap permite 1 solicitud por segundo. En la primera carga puede haber un breve retraso para resolver coordenadas. Las ciudades ya consultadas se cachean para no repetir llamadas.

---

## Requerimientos opcionales implementados

- ✅ DELETE de beneficios
- ✅ Datos de ejemplo precargados (`data.sql`)
- ⬜ Autenticación JWT
- ⬜ Pruebas unitarias
- ⬜ Docker

---

## English summary

Full-stack solution for the Symplifica technical challenge.

**Backend:** REST API built with Java 21 and Spring Boot. Manages employees and benefits with full CRUD. Integrates OpenStreetMap Nominatim to resolve geographic coordinates by city name. Coordinates are cached in memory to respect the 1 req/sec rate limit.

**Frontend:** Vue 3 dashboard with an interactive Leaflet map, employee table with inline actions, and a benefits modal per employee.

**Run:** Start the backend with `./mvnw spring-boot:run` and the frontend with `npm run dev`. Both must run simultaneously.