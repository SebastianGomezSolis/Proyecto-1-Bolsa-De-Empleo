CREATE DATABASE bolsadeempleo;
USE bolsadeempleo;

CREATE TABLE administrador (
    identificacion VARCHAR(20) NOT NULL,
    correo VARCHAR(100) NOT NULL,
    clave VARCHAR(255) NOT NULL,
    PRIMARY KEY (identificacion),
    UNIQUE KEY uq_administrador_correo (correo)
);

CREATE TABLE nacionalidad (
    iso VARCHAR(5) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    iso3 VARCHAR(5),
    codigoNumero INT,
    codigoTelefono INT,
    PRIMARY KEY (iso)
);

CREATE TABLE empresa (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    localizacion VARCHAR(150),
    correo VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    descripcion TEXT,
    clave VARCHAR(255) NOT NULL,
    autorizado BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uq_empresa_correo (correo)
);

CREATE TABLE oferente (
    identificacion VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    primer_apellido VARCHAR(100) NOT NULL,
    nacionalidad VARCHAR(5) NOT NULL,
    telefono VARCHAR(20),
    correo VARCHAR(100) NOT NULL,
    lugar_residencia VARCHAR(150),
    clave VARCHAR(255) NOT NULL,
    autorizado BOOLEAN NOT NULL DEFAULT FALSE,
    curriculum VARCHAR(255),
    PRIMARY KEY (identificacion),
    UNIQUE KEY uq_oferente_correo (correo),
    CONSTRAINT fk_oferente_nacionalidad
        FOREIGN KEY (nacionalidad) REFERENCES nacionalidad(iso)
);

CREATE TABLE caracteristica (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    padre_id INT,
    PRIMARY KEY (id),
    CONSTRAINT fk_caracteristica_padre
        FOREIGN KEY (padre_id) REFERENCES caracteristica(id)
);

CREATE TABLE habilidad (
    id INT NOT NULL AUTO_INCREMENT,
    oferente_identificacion VARCHAR(20) NOT NULL,
    caracteristica_id INT NOT NULL,
    nivel INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_oferente_caracteristica (oferente_identificacion, caracteristica_id),
    CONSTRAINT fk_habilidad_oferente
        FOREIGN KEY (oferente_identificacion) REFERENCES oferente(identificacion),
    CONSTRAINT fk_habilidad_caracteristica
        FOREIGN KEY (caracteristica_id) REFERENCES caracteristica(id),
    CONSTRAINT chk_habilidad_nivel
        CHECK (nivel BETWEEN 1 AND 5)
);

CREATE TABLE puesto (
    id INT NOT NULL AUTO_INCREMENT,
    descripcion TEXT NOT NULL,
    salario DECIMAL(12,2) NOT NULL,
    tipoPublicacion VARCHAR(20) NOT NULL DEFAULT 'publico',
    empresa_id INT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fechaRegistro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_puesto_empresa
        FOREIGN KEY (empresa_id) REFERENCES empresa(id),
    CONSTRAINT chk_puesto_tipo_publicacion
        CHECK (tipoPublicacion IN ('publico', 'privado'))
);

CREATE TABLE puestocaracteristica (
    id INT NOT NULL AUTO_INCREMENT,
    puesto_id INT NOT NULL,
    caracteristica_id INT NOT NULL,
    nivel_requerido INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_puesto_caracteristica (puesto_id, caracteristica_id),
    CONSTRAINT fk_puestocaracteristica_puesto
        FOREIGN KEY (puesto_id) REFERENCES puesto(id),
    CONSTRAINT fk_puestocaracteristica_caracteristica
        FOREIGN KEY (caracteristica_id) REFERENCES caracteristica(id),
    CONSTRAINT chk_puestocaracteristica_nivel
        CHECK (nivel_requerido BETWEEN 1 AND 5)
);
