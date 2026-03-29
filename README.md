# 💼 Bolsa de Empleo

Sistema web desarrollado en Java con Spring Boot (MVC, Server Side Rendering) para la gestión de una bolsa de empleo. Permite a empresas publicar puestos de trabajo y a oferentes registrar sus habilidades, facilitando búsquedas de coincidencia entre ambos mediante similitud coseno.

> **Curso:** EIF209 – Programación 4
> **Período:** 2026-01
> **Universidad Nacional** – Escuela de Informática

---

## 👥 Autores

- Sebastián Gómez Solís — [@SebastianGomezSolis](https://github.com/SebastianGomezSolis)
- Daniel Chacón González — [@DaniChacG05](https://github.com/DaniChacG05)
- Julian Ramos Arias — [@JulianRamos95](https://github.com/JulianRamos95)

---

## 📋 Tabla de contenidos

- [Descripción general](#descripción-general)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Requisitos técnicos cumplidos](#requisitos-técnicos-cumplidos)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Base de datos](#base-de-datos)
- [Funcionalidades por rol](#funcionalidades-por-rol)
- [Algoritmo de matching](#algoritmo-de-matching)
- [Instalación y configuración](#instalación-y-configuración)
- [Cómo ejecutar](#cómo-ejecutar)
- [Rutas principales](#rutas-principales)

---

## Descripción general

La aplicación es un sistema de bolsa de empleo donde:

- Las **empresas** publican puestos de trabajo con características requeridas y nivel mínimo (1–5) por característica.
- Los **oferentes** registran sus habilidades con su respectivo nivel y suben su currículum en PDF.
- El sistema calcula el **grado de coincidencia mediante similitud coseno** entre los requisitos de un puesto y las habilidades de un oferente.
- Los puestos pueden ser **públicos** (visibles para todos) o **privados** (solo para oferentes registrados y autorizados).
- Los **administradores** aprueban los registros de empresas y oferentes, gestionan el catálogo jerárquico de características y generan reportes PDF por mes.

---

## Tecnologías utilizadas

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 25 |
| Framework | Spring Boot 4.0.3 (Spring MVC) |
| Plantillas | Thymeleaf (Server Side Rendering) |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | MySQL 8 |
| Seguridad | Spring Security Crypto (BCrypt) |
| Generación de PDF | Apache PDFBox 3.0.5 |
| Lectura de Excel | Apache POI 5.2.5 (carga de nacionalidades) |
| HTTP externo | API de Hacienda (tipo de cambio USD→CRC) |
| Build | Maven (Maven Wrapper incluido) |
| Frontend | Bootstrap 5.3, Font Awesome 7 |

> **Nota:** No se utiliza JavaScript propio. El proyecto cumple estrictamente con la técnica Server Side Rendering.

---

## Requisitos técnicos cumplidos

Según los documentos de arquitectura y consideraciones del curso:

- ✅ **Nombres de controladores** con la convención requerida (`AdministradorController`, `EmpresaController`, `OferenteController`, `PublicoController`, `PuestosController`).
- ✅ **Inyección de `HttpSession`** vía `@Autowired` en cada controlador que la requiere.
- ✅ **Controladores delegan** toda lógica de negocio a la capa de servicios; no contienen cálculos ni acceso directo a repositorios.
- ✅ **Verificación de sesión** al inicio de cada método protegido, con redirección a `/ingresar`.
- ✅ **`ModeloDatos`** como fachada centralizada de todos los servicios, inyectado con `@Autowired`.
- ✅ **Fragmentos de vistas** Thymeleaf reutilizables: `head`, `footer`, `navbar-administrador`, `navbar-empresa`, `navbar-oferente`, `navbar-publico`.
- ✅ **Nacionalidades** cargadas desde archivo Excel mediante Apache POI al arrancar la aplicación.
- ✅ **Contraseñas** de empresa y oferente almacenadas con hash BCrypt.
- ✅ **Salario** ingresado en dólares; mostrado en dólares y colones usando el tipo de cambio de venta del BCCR vía API de Hacienda.
- ✅ **Árbol de características** implementado como tabla auto-referencial en base de datos.
- ✅ **Similitud coseno** para calcular porcentaje de coincidencia entre puestos y oferentes.
- ✅ **Solo nodos hoja** son seleccionables al configurar puestos o habilidades, validado en backend y frontend.

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
│   │   │   ├── NacionalidadRepository
│   │   │   ├── OferenteRepository
│   │   │   ├── PuestoCaracteristicaRepository
│   │   │   └── PuestoRepository
│   │   ├── logic/
│   │   │   ├── model/                   # Entidades JPA y modelos auxiliares
│   │   │   │   ├── Administrador
│   │   │   │   ├── Caracteristica
│   │   │   │   ├── Empresa
│   │   │   │   ├── Habilidad
│   │   │   │   ├── Nacionalidad
│   │   │   │   ├── Oferente
│   │   │   │   ├── Puesto
│   │   │   │   ├── PuestoCaracteristica
│   │   │   │   ├── CandidatoResultado   # Modelo auxiliar para resultados de matching
│   │   │   │   └── TipoCambio           # Modelo auxiliar para API de Hacienda
│   │   │   ├── servicios/               # Lógica de negocio
│   │   │   │   ├── AdministradorService
│   │   │   │   ├── CaracteristicaService
│   │   │   │   ├── EmpresaService
│   │   │   │   ├── HabilidadService
│   │   │   │   ├── MatchingService      # Similitud coseno
│   │   │   │   ├── NacionalidadExcelLoader  # Carga inicial desde Excel
│   │   │   │   ├── NacionalidadService
│   │   │   │   ├── OferenteService
│   │   │   │   ├── PasswordHash         # BCrypt
│   │   │   │   ├── PuestoCaracteristicaService
│   │   │   │   ├── PuestoService
│   │   │   │   ├── ReporteService       # Generación de PDF con PDFBox
│   │   │   │   └── TipoCambioServicio   # Consumo de API de Hacienda
│   │   │   └── ModeloDatos              # Fachada centralizada de servicios
│   │   └── presentation/               # Controladores Spring MVC
│   │       ├── AdministradorController
│   │       ├── EmpresaController
│   │       ├── OferenteController
│   │       ├── PublicoController
│   │       ├── PuestosController
│   │       └── RecursosConfig           # Handler de archivos estáticos (CVs)
│   └── resources/
│       ├── templates/                   # Vistas Thymeleaf
│       │   ├── administrador/
│       │   ├── empresa/
│       │   ├── oferente/
│       │   ├── publico/
│       │   └── fragmentos/              # head, footer, navbars reutilizables
│       ├── static/images/               # Imágenes del sitio
│       ├── nacionalidades.xlsx          # Fuente de datos de nacionalidades
│       └── application.properties
└── test/
```

---

## Base de datos

El sistema utiliza MySQL con la base de datos `BolsaDeEmpleo`. El script de creación se encuentra en `BolsaDeEmpleo.sql`.

### Modelo de tablas

| Tabla | Descripción |
|---|---|
| `administrador` | Usuarios administradores del sistema |
| `empresa` | Empresas registradas (requieren autorización) |
| `oferente` | Candidatos/personas (requieren autorización) |
| `nacionalidad` | Catálogo de países cargado desde Excel |
| `caracteristica` | Catálogo jerárquico auto-referencial de habilidades |
| `habilidad` | Habilidades de un oferente con su nivel (1–5) |
| `puesto` | Puestos de trabajo publicados por empresas |
| `puestocaracteristica` | Características requeridas por puesto con nivel mínimo |

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

- Ver los **5 puestos públicos** más recientemente registrados con detalle de características al hacer clic.
- **Buscar puestos** públicos filtrando por características jerárquicas (solo nodos hoja).
- **Registrar empresa** (queda pendiente de aprobación del administrador).
- **Registrar oferente** (queda pendiente de aprobación del administrador).
- **Iniciar sesión** como empresa, oferente o administrador.

### 🏢 Empresa

| Funcionalidad | Ruta |
|---|---|
| Dashboard | `/empresa/dashboard` |
| Ver mis puestos | `/empresa/puestos` |
| Publicar nuevo puesto | `/empresa/puestos/publicar` |
| Activar un puesto | POST `/empresa/puestos/activar/{id}` |
| Desactivar un puesto | POST `/empresa/puestos/desactivar/{id}` |
| Buscar candidatos para un puesto | `/empresa/puestos/{id}/candidatos` |
| Ver detalle de un candidato | `/empresa/candidatos/{id}?puestoId={id}` |

Al publicar un puesto, la empresa selecciona características del catálogo jerárquico e indica el nivel mínimo requerido (1–5). El puesto puede ser **público** o **privado**.

### 👤 Oferente

| Funcionalidad | Ruta |
|---|---|
| Dashboard | `/oferente/dashboard` |
| Gestionar habilidades (agregar/quitar) | `/oferente/habilidades` |
| Ver y subir currículum PDF | `/oferente/cv` |
| Buscar puestos por características | `/oferente/buscar` |

### 🔧 Administrador

| Funcionalidad | Ruta |
|---|---|
| Dashboard | `/admin/dashboard` |
| Aprobar empresas pendientes | `/admin/empresas/pendientes` |
| Aprobar oferentes pendientes | `/admin/oferentes/pendientes` |
| Gestionar características jerárquicas | `/admin/caracteristicas` |
| Generar reporte PDF por mes | GET `/admin/reportes/pdf?mes=&anio=` |

El catálogo de características es **jerárquico** (árbol de n niveles). El administrador navega por niveles y agrega nuevas categorías con su padre correspondiente.

---

## Algoritmo de matching

`MatchingService` implementa **similitud coseno** para calcular el porcentaje de coincidencia entre las habilidades de un oferente y los requisitos de un puesto:

1. Se construye un vector para el puesto: `{ caracteristica_id → nivel_requerido }`.
2. Se construye un vector para el oferente: `{ caracteristica_id → nivel_del_oferente }`.
3. Se calcula el producto punto y las normas de ambos vectores.
4. La similitud coseno resultante se multiplica por 100 para obtener el porcentaje.
5. Adicionalmente se cuenta cuántos requisitos cumple el oferente (nivel ≥ nivel requerido).

Los resultados se ordenan de mayor a menor porcentaje de coincidencia.

---

## Instalación y configuración

### Prerrequisitos

- Java 25 o superior
- Maven (o usar el Maven Wrapper incluido `./mvnw`)
- MySQL 8 o superior
- El archivo `nacionalidades.xlsx` debe estar en `src/main/resources/` (se carga automáticamente al arrancar)

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

---

## Cómo ejecutar

Con Maven Wrapper (recomendado):

```bash
./mvnw spring-boot:run
```

En Windows:

```cmd
mvnw.cmd spring-boot:run
```

La aplicación estará disponible en: [http://localhost:8080](http://localhost:8080)

### Crear un administrador inicial

Los administradores no tienen formulario de registro público. Se insertan directamente en la base de datos:

```sql
INSERT INTO administrador (identificacion, correo, clave)
VALUES ('admin01', 'admin@bolsaempleo.local', 'tu_clave_aqui');
```

> **Nota:** La clave del administrador se compara en texto plano. Para empresas y oferentes se usa **BCrypt**.

---

## Rutas principales

| Ruta | Descripción |
|---|---|
| `/` | Página principal pública (últimos 5 puestos) |
| `/puestos/buscar` | Búsqueda pública de puestos por características |
| `/empresa/registro` | Registro de nueva empresa |
| `/oferente/registro` | Registro de nuevo oferente |
| `/ingresar` | Login (empresa, oferente o administrador) |
| `/salir` | Cerrar sesión |
| `/empresa/**` | Área privada de empresa |
| `/oferente/**` | Área privada de oferente |
| `/admin/**` | Área privada de administrador |

---

## Equipo de desarrollo

Proyecto desarrollado para el curso **EIF209 – Programación 4**, período 2026-01.
Universidad Nacional de Costa Rica – Escuela de Informática.
