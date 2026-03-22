CREATE DATABASE BolsaDeEmpleo;
USE bolsadeempleo;

CREATE TABLE Administrador (
                               identificacion VARCHAR(20) NOT NULL,
                               correo VARCHAR(100) NOT NULL,
                               clave VARCHAR(255) NOT NULL,
                               PRIMARY KEY (identificacion)
);

CREATE TABLE Nacionalidad (
                              iso VARCHAR(5) NOT NULL,
                              nombre VARCHAR(100) NOT NULL,
                              descripcion VARCHAR(255),
                              iso3 VARCHAR(5),
                              codigoNumero INT,
                              codigoTelefono INT,
                              PRIMARY KEY (iso)
);

CREATE TABLE Empresa (
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
                          nacionalidad VARCHAR(50),
                          telefono VARCHAR(20),
                          correo VARCHAR(100) NOT NULL,
                          lugar_residencia VARCHAR(150),
                          clave VARCHAR(255) NOT NULL,
                          autorizado BOOLEAN NOT NULL DEFAULT FALSE,
                          curriculum VARCHAR(255),
                          PRIMARY KEY (identificacion)

);

CREATE TABLE Caracteristica (
                                id INT NOT NULL AUTO_INCREMENT,
                                nombre VARCHAR(100) NOT NULL,
                                padre_id INT,
                                PRIMARY KEY (id),
                                FOREIGN KEY (padre_id) REFERENCES Caracteristica(id)
);

CREATE TABLE Habilidad (
                           id INT NOT NULL AUTO_INCREMENT,
                           oferente_identificacion VARCHAR(20) NOT NULL,
                           caracteristica_id INT NOT NULL,
                           nivel INT NOT NULL,
                           PRIMARY KEY (id),
                           UNIQUE KEY uq_oferente_caracteristica (oferente_identificacion, caracteristica_id),
                           FOREIGN KEY (oferente_identificacion) REFERENCES Oferente(identificacion),
                           FOREIGN KEY (caracteristica_id) REFERENCES Caracteristica(id)
);

CREATE TABLE Puesto (
                        id INT NOT NULL AUTO_INCREMENT,
                        descripcion TEXT NOT NULL,
                        salario DOUBLE NOT NULL,
                        tipoPublicacion VARCHAR(20) NOT NULL DEFAULT 'publico',
                        empresa_id INT NOT NULL,
                        activo BOOLEAN NOT NULL DEFAULT TRUE,
                        fechaRegistro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY (id),
                        FOREIGN KEY (empresa_id) REFERENCES Empresa(id)
);

-- *** TABLA NUEVA Y CRÍTICA ***
CREATE TABLE PuestoCaracteristica (
                                      id INT NOT NULL AUTO_INCREMENT,
                                      puesto_id INT NOT NULL,
                                      caracteristica_id INT NOT NULL,
                                      nivelRequerido INT NOT NULL,
                                      PRIMARY KEY (id),
                                      UNIQUE KEY uq_puesto_caracteristica (puesto_id, caracteristica_id),
                                      FOREIGN KEY (puesto_id) REFERENCES Puesto(id),
                                      FOREIGN KEY (caracteristica_id) REFERENCES Caracteristica(id)
);
