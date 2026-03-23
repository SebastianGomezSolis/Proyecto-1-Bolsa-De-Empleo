# 💼 Bolsa de Empleo

Sistema web desarrollado en Java con Spring Boot (MVC, Server Side Rendering) para la gestión de una bolsa de empleo. Permite a empresas publicar puestos de trabajo y a oferentes registrar sus habilidades, facilitando búsquedas de coincidencia entre ambos.

> **Curso:** EIF209 – Programación 4  
> **Período:** 2026-01  
> **Universidad Nacional** – Escuela de Informática

---

## 📋 Tabla de contenidos

- [Descripción general](#descripción-general)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Base de datos](#base-de-datos)
- [Funcionalidades por rol](#funcionalidades-por-rol)
- [Instalación y configuración](#instalación-y-configuración)
- [Cómo ejecutar](#cómo-ejecutar)

---

## Descripción general

La aplicación es un sistema de bolsa de empleo donde:

- Las **empresas** publican puestos de trabajo con características requeridas y nivel mínimo por característica.
- Los **oferentes** registran sus habilidades y suben su currículum en PDF.
- El sistema calcula el **grado de coincidencia** (similitud coseno) entre los requisitos de un puesto y las habilidades de los oferentes.
- Los puestos pueden ser **públicos** (visibles para todos) o **privados** (solo para oferentes registrados y autorizados).
- Los **administradores** aprueban los registros de empresas y oferentes, y gestionan el catálogo de características jerárquicas.

---

## Tecnologías utilizadas

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 25 |
| Framework | Spring Boot 4.0.3 (Spring MVC) |
| Plantillas | Thymeleaf (Server Side Rendering) |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | MySQL |
| Seguridad | Spring Security Crypto (BCrypt) |
| Generación de PDF | Apache PDFBox 3.0.5 + iText 5.5.13 |
| Lectura de Excel | Apache POI 5.2.5 (para nacionalidades) |
| Build | Maven (Maven Wrapper) |
| Frontend | Bootstrap 5.3, Font Awesome 7 |

> **Nota:** No se utiliza JavaScript propio. El proyecto se adhiere estrictamente a la técnica Server Side Rendering.

---

## Estructura del proyecto

```
src/
├── main/
│   ├── java/una/sistema/proyecto1bolsadeempleo/
│   │   ├── data/                        # Repositorios JPA
│   │   │   ├── AdministradorRepository
│   │   │   ├── CaracteristicaRepository
│   │   │   ├── EmpresaRepository
│   │   │   ├── HabilidadRepository
│   │   │   ├── OferenteRepository
│   │   │   ├── PuestoCaracteristicaRepository
│   │   │   └── PuestoRepository
│   │   ├── logic/
│   │   │   ├── model/                   # Entidades JPA y modelos auxiliares
│   │   │   │   ├── Administrador
│   │   │   │   ├── Caracteristica
│   │   │   │   ├── Empresa
│   │   │   │   ├── Habilidad
│   │   │   │   ├── Oferente
│   │   │   │   ├── Puesto
│   │   │   │   ├── PuestoCaracteristica
│   │   │   │   ├── CandidatoResultado
│   │   │   │   ├── Nacionalidad
│   │   │   │   └── TipoCambio
│   │   │   ├── servicios/               # Lógica de negocio
│   │   │   │   ├── AdministradorService
│   │   │   │   ├── CaracteristicaService
│   │   │   │   ├── EmpresaService
│   │   │   │   ├── HabilidadService
│   │   │   │   ├── MatchingService      # Algoritmo de similitud coseno
│   │   │   │   ├── NacionalidadServicio # Carga desde Excel
│   │   │   │   ├── OferenteService
│   │   │   │   ├── PasswordHash         # BCrypt
│   │   │   │   ├── PuestoCaracteristicaService
│   │   │   │   ├── PuestoService
│   │   │   │   ├── ReporteService       # Generación de PDF
│   │   │   │   └── TipoCambioServicio   # API Hacienda
│   │   │   └── ModeloDatos              # Fachada de servicios
│   │   └── presentation/               # Controladores Spring MVC
│   │       ├── AdministradorController
│   │       ├── EmpresaController
│   │       ├── OferenteController
│   │       └── PublicoController
│   └── resources/
│       ├── templates/                   # Vistas Thymeleaf
│       │   ├── administrador/
│       │   ├── empresa/
│       │   ├── oferente/
│       │   ├── publico/
│       │   └── fragmentos/              # Head, footer reutilizables
│       └── application.properties
└── test/
```

---

## Base de datos

El sistema utiliza MySQL con la base de datos `BolsaDeEmpleo`. El script de creación se encuentra en `BolsaDeEmpleo.sql`.

### Modelo de tablas

| Tabla | Descripción |
|---|---|
| `Administrador` | Usuarios administradores del sistema |
| `Empresa` | Empresas registradas (requieren autorización) |
| `Oferente` | Candidatos/personas (requieren autorización) |
| `Caracteristica` | Catálogo jerárquico de habilidades (auto-referencial) |
| `Habilidad` | Habilidades de un oferente con su nivel (1-5) |
| `Puesto` | Puestos de trabajo publicados por empresas |
| `PuestoCaracteristica` | Características requeridas por puesto con nivel mínimo |

### Configuración de conexión

En `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/BolsaDeEmpleo
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
```

Ajusta `username` y `password` según tu entorno local.

---

## Funcionalidades por rol

### 🌐 Parte pública (sin sesión)

- Ver los **5 puestos públicos** más recientemente registrados con detalle al hacer clic.
- **Buscar puestos** públicos filtrando por características jerárquicas.
- **Registrar empresa** (queda pendiente de aprobación).
- **Registrar oferente** (queda pendiente de aprobación).
- **Iniciar sesión** como empresa, oferente o administrador.

### 🏢 Empresa

| Funcionalidad | Ruta |
|---|---|
| Dashboard | `/empresa/dashboard` |
| Ver mis puestos | `/empresa/puestos` |
| Publicar nuevo puesto | `/empresa/puestos/publicar` |
| Buscar candidatos para un puesto | `/empresa/puestos/{id}/candidatos` |
| Ver detalle de un candidato | `/empresa/candidatos/{id}` |
| Desactivar un puesto | POST `/empresa/puestos/desactivar/{id}` |

Al publicar un puesto, la empresa selecciona las características requeridas desde el catálogo jerárquico e indica el nivel mínimo (1–5) para cada una. El puesto puede ser **público** o **privado**.

### 👤 Oferente

| Funcionalidad | Ruta |
|---|---|
| Dashboard | `/oferente/dashboard` |
| Gestionar habilidades | `/oferente/habilidades` |
| Subir currículum PDF | `/oferente/cv` |
| Buscar puestos por características | `/puestos/buscar` |

### 🔧 Administrador

| Funcionalidad | Ruta |
|---|---|
| Dashboard | `/admin/dashboard` |
| Aprobar empresas pendientes | `/admin/empresas/pendientes` |
| Aprobar oferentes pendientes | `/admin/oferentes/pendientes` |
| Gestionar características jerárquicas | `/admin/caracteristicas` |
| Generar reporte PDF por mes | `/admin/reportes` → `/admin/reportes/pdf?mes=&anio=` |

El catálogo de características es **jerárquico** (árbol de categorías y subcategorías). El administrador puede navegar por niveles y agregar nuevas categorías con su padre correspondiente.

---

## Algoritmo de matching

El `MatchingService` utiliza **similitud coseno** para calcular el porcentaje de coincidencia entre las habilidades de un oferente y los requisitos de un puesto:

1. Se construye un vector para el puesto: `{ caracteristica_id → nivel_requerido }`.
2. Se construye un vector para el oferente: `{ caracteristica_id → nivel_del_oferente }`.
3. Se calcula el producto punto y las normas de ambos vectores.
4. La similitud coseno resultante se multiplica por 100 para obtener el porcentaje.
5. Adicionalmente se cuenta cuántos requisitos cumple el oferente (nivel ≥ nivel requerido).

Los resultados se ordenan de mayor a menor porcentaje.

---

## Instalación y configuración

### Prerrequisitos

- Java 25 o superior
- Maven (o usar el Maven Wrapper incluido `./mvnw`)
- MySQL 8 o superior
- Archivo `nacionalidades.xlsx` en el directorio raíz del proyecto (usado para cargar el listado de países)

### Pasos

1. Clona el repositorio:
   ```bash
   git clone <url-del-repositorio>
   cd Proyecto-1-Bolsa-De-Empleo
   ```

2. Crea la base de datos ejecutando el script:
   ```bash
   mysql -u root -p < BolsaDeEmpleo.sql
   ```

3. Ajusta las credenciales de base de datos en `src/main/resources/application.properties`.

4. Asegúrate de que el archivo `nacionalidades.xlsx` está en la raíz del proyecto.

---

## Cómo ejecutar

Con Maven Wrapper (recomendado):

```bash
./mvnw spring-boot:run
```

O en Windows:

```cmd
mvnw.cmd spring-boot:run
```

La aplicación estará disponible en: [http://localhost:8080](http://localhost:8080)

### Crear un administrador inicial

Como los administradores no tienen formulario de registro público, se deben insertar directamente en la base de datos:

```sql
INSERT INTO Administrador (identificacion, correo, clave)
VALUES ('admin01', 'admin@bolsaempleo.local', 'tu_clave_aqui');
```

> La clave del administrador se compara en texto plano en la versión actual. Para empresas y oferentes se usa **BCrypt**.

---

## Rutas principales

| Ruta | Descripción |
|---|---|
| `/` | Página principal pública |
| `/puestos/buscar` | Búsqueda pública de puestos |
| `/registro/empresa` | Registro de nueva empresa |
| `/registro/oferente` | Registro de nuevo oferente |
| `/ingresar` | Login (empresa, oferente, admin) |
| `/salir` | Cerrar sesión |
| `/empresa/**` | Área privada de empresa |
| `/oferente/**` | Área privada de oferente |
| `/admin/**` | Área privada de administrador |

---

## Equipo de desarrollo

Proyecto desarrollado para el curso **EIF209 – Programación 4**, período 2026-01.  
Universidad Nacional de Costa Rica – Escuela de Informática.
