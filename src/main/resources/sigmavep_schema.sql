-- ==========================================================
-- SIGMAVEP v2.0 — Script de Base de Datos MySQL
-- Sistema de Gestión del Mantenimiento Preventivo de Vehículos Policiales
-- Policía de la Provincia de Santa Fe
-- Universidad Siglo 21 — Programación II
-- Autor: Mateo German Ruiz Díaz
-- ==========================================================

-- CRITICO: Forzar UTF-8 para evitar corrupcion de acentos
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection = utf8mb4;

DROP DATABASE IF EXISTS sigmavep;
CREATE DATABASE IF NOT EXISTS sigmavep CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sigmavep;

SET NAMES utf8mb4;

-- ==========================================================
-- TABLAS DE CATÁLOGO
-- ==========================================================

CREATE TABLE zona (
    id_zona   INT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    sede      VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE estado_movil (
    id_estado INT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(60) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE estado_alerta (
    id_estado_alerta INT AUTO_INCREMENT PRIMARY KEY,
    nombre           VARCHAR(60) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tipo_mantenimiento (
    id_tipo_mantenimiento INT AUTO_INCREMENT PRIMARY KEY,
    nombre                VARCHAR(100) NOT NULL,
    limite_km             INT NOT NULL CHECK (limite_km > 0),
    descripcion           TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tipo_novedad (
    id_tipo_novedad INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    descripcion     TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE rol (
    id_rol      INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(60) NOT NULL UNIQUE,
    descripcion TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================================
-- TABLAS PRINCIPALES
-- ==========================================================

CREATE TABLE dependencia (
    id_dependencia INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(150) NOT NULL,
    id_zona        INT NOT NULL,
    CONSTRAINT fk_dep_zona FOREIGN KEY (id_zona) REFERENCES zona(id_zona)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE usuario (
    id_usuario     INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    apellido       VARCHAR(100) NOT NULL,
    username       VARCHAR(50) NOT NULL UNIQUE,
    password       VARCHAR(64) NOT NULL,
    id_rol         INT NOT NULL,
    activo         BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usr_rol FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE movil (
    id_movil       INT AUTO_INCREMENT PRIMARY KEY,
    numero_interno VARCHAR(20) NOT NULL UNIQUE,
    patente        VARCHAR(10) NOT NULL UNIQUE,
    marca          VARCHAR(60) NOT NULL,
    modelo         VARCHAR(60) NOT NULL,
    anio           INT NOT NULL,
    km_actual      INT NOT NULL DEFAULT 0,
    id_dependencia INT NOT NULL,
    id_estado      INT NOT NULL,
    fecha_alta     DATE NOT NULL DEFAULT (CURRENT_DATE),
    activo         BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_movil_dep    FOREIGN KEY (id_dependencia) REFERENCES dependencia(id_dependencia),
    CONSTRAINT fk_movil_estado FOREIGN KEY (id_estado)      REFERENCES estado_movil(id_estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE alerta (
    id_alerta             INT AUTO_INCREMENT PRIMARY KEY,
    id_movil              INT NOT NULL,
    id_tipo_mantenimiento INT NOT NULL,
    id_estado_alerta      INT NOT NULL DEFAULT 1,
    km_disparo            INT NOT NULL,
    fecha_generacion      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones         TEXT,
    id_usuario            INT,
    fecha_procesamiento   DATETIME,
    CONSTRAINT fk_alerta_movil   FOREIGN KEY (id_movil)              REFERENCES movil(id_movil),
    CONSTRAINT fk_alerta_tipo    FOREIGN KEY (id_tipo_mantenimiento) REFERENCES tipo_mantenimiento(id_tipo_mantenimiento),
    CONSTRAINT fk_alerta_estado  FOREIGN KEY (id_estado_alerta)      REFERENCES estado_alerta(id_estado_alerta),
    CONSTRAINT fk_alerta_usuario FOREIGN KEY (id_usuario)            REFERENCES usuario(id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE registro_kilometraje (
    id_registro  INT AUTO_INCREMENT PRIMARY KEY,
    id_movil     INT NOT NULL,
    km_anterior  INT NOT NULL,
    km_nuevo     INT NOT NULL,
    fecha_hora   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario   INT NOT NULL,
    CONSTRAINT fk_rk_movil   FOREIGN KEY (id_movil)   REFERENCES movil(id_movil),
    CONSTRAINT fk_rk_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE servicio_realizado (
    id_servicio           INT AUTO_INCREMENT PRIMARY KEY,
    id_movil              INT NOT NULL,
    id_tipo_mantenimiento INT NOT NULL,
    km_servicio           INT NOT NULL,
    fecha_servicio        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones         TEXT,
    id_usuario            INT NOT NULL,
    CONSTRAINT fk_sr_movil   FOREIGN KEY (id_movil)              REFERENCES movil(id_movil),
    CONSTRAINT fk_sr_tipo    FOREIGN KEY (id_tipo_mantenimiento) REFERENCES tipo_mantenimiento(id_tipo_mantenimiento),
    CONSTRAINT fk_sr_usuario FOREIGN KEY (id_usuario)            REFERENCES usuario(id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE novedad (
    id_novedad      INT AUTO_INCREMENT PRIMARY KEY,
    id_movil        INT NOT NULL,
    id_tipo_novedad INT NOT NULL,
    descripcion     TEXT NOT NULL,
    km_novedad      INT NOT NULL,
    fecha_hora      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario      INT NOT NULL,
    CONSTRAINT fk_nov_movil   FOREIGN KEY (id_movil)        REFERENCES movil(id_movil),
    CONSTRAINT fk_nov_tipo    FOREIGN KEY (id_tipo_novedad) REFERENCES tipo_novedad(id_tipo_novedad),
    CONSTRAINT fk_nov_usuario FOREIGN KEY (id_usuario)      REFERENCES usuario(id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================================
-- DATOS INICIALES — CATÁLOGOS
-- ==========================================================

INSERT INTO estado_movil (nombre) VALUES
    ('En servicio'),
    ('Fuera de servicio'),
    ('En reparacion');

INSERT INTO estado_alerta (nombre) VALUES
    ('Pendiente'),
    ('Procesada'),
    ('Postergada');

INSERT INTO tipo_mantenimiento (nombre, limite_km, descripcion) VALUES
    ('Cambio de aceite y filtro',          5000,  'Cambio de aceite de motor y filtro de aceite'),
    ('Cambio de filtro de aire',          10000,  'Reemplazo del filtro de aire del motor'),
    ('Cambio de filtro de combustible',   15000,  'Reemplazo del filtro de combustible'),
    ('Revision de frenos',                20000,  'Inspeccion y ajuste del sistema de frenos'),
    ('Cambio de pastillas de freno',      30000,  'Reemplazo de pastillas y discos de freno'),
    ('Cambio de bujias',                  20000,  'Reemplazo de bujias de encendido'),
    ('Revision de correa de distribucion',50000,  'Inspeccion y reemplazo preventivo de correa'),
    ('Alineacion y balanceo',             10000,  'Alineacion de direccion y balanceo de neumaticos'),
    ('Cambio de liquido de frenos',       20000,  'Renovacion del liquido de frenos (DOT 4)'),
    ('Revision general',                  10000,  'Inspeccion completa del vehiculo');

INSERT INTO tipo_novedad (nombre, descripcion) VALUES
    ('Falla mecanica',       'Falla en componentes mecanicos del vehiculo'),
    ('Accidente de transito','Colision, choque u otro accidente'),
    ('Rotura de vidrio',     'Daño en parabrisas, ventanas o espejos'),
    ('Falla electrica',      'Falla en sistema electrico o electronico'),
    ('Neumatico danado',     'Pinchazo, rotura o desgaste excesivo de neumatico'),
    ('Vandalismo',           'Daño causado por terceros intencionalmente'),
    ('Falla de motor',       'Problemas graves en el motor del vehiculo'),
    ('Otras novedades',      'Novedades no clasificadas en las categorias anteriores');

INSERT INTO rol (nombre, descripcion) VALUES
    ('Administrador', 'Acceso total al sistema: gestion de usuarios, moviles y reportes'),
    ('Finanzas',      'Acceso a reportes y consultas de flota'),
    ('Supervisor',    'Gestion de moviles, alertas, km y novedades');

INSERT INTO zona (nombre, sede) VALUES
    ('Zona 1 - Rosario',   'Rosario'),
    ('Zona 2 - Cordoba',   'Cordoba Capital'),
    ('Zona 3 - Santa Fe',  'Santa Fe Capital'),
    ('Zona 4 - Mendoza',   'Mendoza');

INSERT INTO dependencia (nombre, id_zona) VALUES
    ('Comisaria 1ra - Rosario',        1),
    ('Comisaria 2da - Rosario',        1),
    ('Destacamento Zona Norte',        1),
    ('Comisaria Central - Cordoba',    2),
    ('Comisaria 5ta - Cordoba',        2),
    ('Jefatura Regional - Santa Fe',   3),
    ('Comisaria 3ra - Santa Fe',       3),
    ('Comisaria Central - Mendoza',    4);

-- ==========================================================
-- USUARIOS
-- Contrasenas hasheadas SHA-256:
--   admin123 -> 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
--   super123 -> 9c56cc51b374c3ba189210d5b6d4bf57790d351ef09d9a4a85ba1dcc0f13db
--   fin123   -> ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94
-- ==========================================================

INSERT INTO usuario (nombre, apellido, username, password, id_rol, activo) VALUES
    ('Administrador', 'Sistema',  'admin',      '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 1, TRUE),
    ('Juan',          'Perez',    'supervisor', '9c56cc51b374c3ba189210d5b6d4bf57790d351ef09d9a4a85ba1dcc0f13db10', 3, TRUE),
    ('Maria',         'Gonzalez', 'finanzas',   'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f',  2, TRUE);

-- ==========================================================
-- MOVILES
-- ==========================================================

INSERT INTO movil (numero_interno, patente, marca, modelo, anio, km_actual, id_dependencia, id_estado, fecha_alta) VALUES
    ('P-001', 'ABC123',  'Ford',       'Ranger',   2021, 45000, 1, 1, '2021-03-15'),
    ('P-002', 'DEF456',  'Volkswagen', 'Amarok',   2022, 32000, 1, 1, '2022-01-10'),
    ('P-003', 'GHI789',  'Chevrolet',  'S10',      2020, 67000, 2, 1, '2020-06-20'),
    ('P-004', 'JKL012',  'Toyota',     'Hilux',    2023, 12000, 3, 1, '2023-04-01'),
    ('P-005', 'MNO345',  'Ford',       'F-150',    2019, 89000, 4, 2, '2019-11-05'),
    ('P-006', 'PQR678',  'Renault',    'Duster',   2022, 28000, 5, 1, '2022-07-18'),
    ('P-007', 'STU901',  'Fiat',       'Toro',     2021, 51000, 6, 3, '2021-09-22'),
    ('P-008', 'VWX234',  'Nissan',     'Frontier', 2022, 39000, 7, 1, '2022-03-14'),
    ('P-009', 'AA123BB', 'Toyota',     'Corolla',  2023,  8000, 8, 1, '2023-08-30'),
    ('P-010', 'BB456CC', 'Ford',       'EcoSport', 2020, 73000, 1, 1, '2020-02-12');

-- ==========================================================
-- HISTORIAL DE KILOMETRAJE REALISTA
-- Progresion logica desde el alta hasta el km actual
-- id_usuario=1 (admin) para todos los registros historicos
-- ==========================================================

-- P-001 Ford Ranger (alta 2021, km_actual=45000)
INSERT INTO registro_kilometraje (id_movil, km_anterior, km_nuevo, fecha_hora, id_usuario) VALUES
(1,     0,  5000, '2021-04-01 08:00:00', 1),
(1,  5000, 10000, '2021-07-10 09:30:00', 1),
(1, 10000, 15000, '2021-10-05 10:00:00', 1),
(1, 15000, 20000, '2022-01-18 08:45:00', 1),
(1, 20000, 25000, '2022-04-20 11:00:00', 1),
(1, 25000, 30000, '2022-08-15 09:00:00', 1),
(1, 30000, 35000, '2022-12-01 10:30:00', 1),
(1, 35000, 40000, '2023-04-10 08:00:00', 1),
(1, 40000, 45000, '2023-09-20 09:15:00', 1);

-- P-002 VW Amarok (alta 2022, km_actual=32000)
INSERT INTO registro_kilometraje (id_movil, km_anterior, km_nuevo, fecha_hora, id_usuario) VALUES
(2,     0,  5000, '2022-02-01 08:00:00', 2),
(2,  5000, 10000, '2022-05-14 10:00:00', 2),
(2, 10000, 15000, '2022-09-08 09:00:00', 2),
(2, 15000, 20000, '2022-12-20 11:30:00', 2),
(2, 20000, 25000, '2023-04-05 08:30:00', 2),
(2, 25000, 30000, '2023-08-18 09:45:00', 2),
(2, 30000, 32000, '2023-11-10 10:00:00', 2);

-- P-003 Chevrolet S10 (alta 2020, km_actual=67000)
INSERT INTO registro_kilometraje (id_movil, km_anterior, km_nuevo, fecha_hora, id_usuario) VALUES
(3,     0,  5000, '2020-07-15 08:00:00', 1),
(3,  5000, 10000, '2020-10-20 09:00:00', 1),
(3, 10000, 15000, '2021-01-12 10:00:00', 1),
(3, 15000, 20000, '2021-04-18 08:30:00', 1),
(3, 20000, 25000, '2021-07-22 09:00:00', 1),
(3, 25000, 30000, '2021-10-05 11:00:00', 1),
(3, 30000, 35000, '2022-01-20 08:00:00', 1),
(3, 35000, 40000, '2022-04-15 09:30:00', 1),
(3, 40000, 45000, '2022-07-28 10:00:00', 1),
(3, 45000, 50000, '2022-11-10 08:45:00', 1),
(3, 50000, 55000, '2023-02-22 09:00:00', 1),
(3, 55000, 60000, '2023-06-14 11:30:00', 1),
(3, 60000, 65000, '2023-10-01 08:00:00', 1),
(3, 65000, 67000, '2023-12-15 09:00:00', 1);

-- P-004 Toyota Hilux (alta 2023, km_actual=12000)
INSERT INTO registro_kilometraje (id_movil, km_anterior, km_nuevo, fecha_hora, id_usuario) VALUES
(4,    0,  5000, '2023-05-10 08:00:00', 2),
(4, 5000, 10000, '2023-08-22 10:00:00', 2),
(4,10000, 12000, '2023-11-30 09:30:00', 2);

-- P-005 Ford F-150 (alta 2019, km_actual=89000)
INSERT INTO registro_kilometraje (id_movil, km_anterior, km_nuevo, fecha_hora, id_usuario) VALUES
(5,     0,  5000, '2019-12-01 08:00:00', 1),
(5,  5000, 10000, '2020-03-10 09:00:00', 1),
(5, 10000, 15000, '2020-06-15 10:00:00', 1),
(5, 15000, 20000, '2020-09-20 08:30:00', 1),
(5, 20000, 25000, '2020-12-08 09:00:00', 1),
(5, 25000, 30000, '2021-03-15 11:00:00', 1),
(5, 30000, 35000, '2021-06-22 08:00:00', 1),
(5, 35000, 40000, '2021-09-30 09:30:00', 1),
(5, 40000, 45000, '2022-01-05 10:00:00', 1),
(5, 45000, 50000, '2022-04-18 08:45:00', 1),
(5, 50000, 55000, '2022-07-25 09:00:00', 1),
(5, 55000, 60000, '2022-11-02 11:30:00', 1),
(5, 60000, 65000, '2023-02-14 08:00:00', 1),
(5, 65000, 70000, '2023-05-20 09:00:00', 1),
(5, 70000, 75000, '2023-08-08 10:00:00', 1),
(5, 75000, 80000, '2023-10-25 08:30:00', 1),
(5, 80000, 85000, '2024-01-15 09:00:00', 1),
(5, 85000, 89000, '2024-04-10 11:00:00', 1);

-- P-006 Renault Duster (alta 2022, km_actual=28000)
INSERT INTO registro_kilometraje (id_movil, km_anterior, km_nuevo, fecha_hora, id_usuario) VALUES
(6,     0,  5000, '2022-08-10 08:00:00', 2),
(6,  5000, 10000, '2022-11-22 09:00:00', 2),
(6, 10000, 15000, '2023-03-05 10:00:00', 2),
(6, 15000, 20000, '2023-06-18 08:30:00', 2),
(6, 20000, 25000, '2023-09-30 09:00:00', 2),
(6, 25000, 28000, '2023-12-20 11:00:00', 2);

-- P-007 Fiat Toro (alta 2021, km_actual=51000)
INSERT INTO registro_kilometraje (id_movil, km_anterior, km_nuevo, fecha_hora, id_usuario) VALUES
(7,     0,  5000, '2021-10-15 08:00:00', 1),
(7,  5000, 10000, '2022-01-20 09:30:00', 1),
(7, 10000, 15000, '2022-04-12 10:00:00', 1),
(7, 15000, 20000, '2022-07-25 08:45:00', 1),
(7, 20000, 25000, '2022-10-30 09:00:00', 1),
(7, 25000, 30000, '2023-01-14 11:00:00', 1),
(7, 30000, 35000, '2023-04-22 08:00:00', 1),
(7, 35000, 40000, '2023-07-18 09:30:00', 1),
(7, 40000, 45000, '2023-10-05 10:00:00', 1),
(7, 45000, 50000, '2024-01-08 08:30:00', 1),
(7, 50000, 51000, '2024-03-20 09:00:00', 1);

-- P-008 Nissan Frontier (alta 2022, km_actual=39000)
INSERT INTO registro_kilometraje (id_movil, km_anterior, km_nuevo, fecha_hora, id_usuario) VALUES
(8,     0,  5000, '2022-04-10 08:00:00', 2),
(8,  5000, 10000, '2022-07-22 09:00:00', 2),
(8, 10000, 15000, '2022-11-05 10:00:00', 2),
(8, 15000, 20000, '2023-02-18 08:30:00', 2),
(8, 20000, 25000, '2023-05-30 09:00:00', 2),
(8, 25000, 30000, '2023-09-12 11:00:00', 2),
(8, 30000, 35000, '2023-12-20 08:00:00', 2),
(8, 35000, 39000, '2024-03-15 09:30:00', 2);

-- P-009 Toyota Corolla (alta 2023, km_actual=8000)
INSERT INTO registro_kilometraje (id_movil, km_anterior, km_nuevo, fecha_hora, id_usuario) VALUES
(9,    0, 4000, '2023-10-10 08:00:00', 1),
(9, 4000, 8000, '2024-02-15 09:00:00', 1);

-- P-010 Ford EcoSport (alta 2020, km_actual=73000)
INSERT INTO registro_kilometraje (id_movil, km_anterior, km_nuevo, fecha_hora, id_usuario) VALUES
(10,     0,  5000, '2020-03-10 08:00:00', 1),
(10,  5000, 10000, '2020-06-22 09:00:00', 1),
(10, 10000, 15000, '2020-10-05 10:00:00', 1),
(10, 15000, 20000, '2021-01-18 08:30:00', 1),
(10, 20000, 25000, '2021-04-25 09:00:00', 1),
(10, 25000, 30000, '2021-08-08 11:00:00', 1),
(10, 30000, 35000, '2021-11-20 08:00:00', 1),
(10, 35000, 40000, '2022-02-14 09:30:00', 1),
(10, 40000, 45000, '2022-05-28 10:00:00', 1),
(10, 45000, 50000, '2022-09-10 08:45:00', 1),
(10, 50000, 55000, '2022-12-22 09:00:00', 1),
(10, 55000, 60000, '2023-04-05 11:00:00', 1),
(10, 60000, 65000, '2023-07-18 08:00:00', 1),
(10, 65000, 70000, '2023-11-02 09:00:00', 1),
(10, 70000, 73000, '2024-02-20 10:00:00', 1);

-- ==========================================================
-- HISTORIAL DE SERVICIOS DE MANTENIMIENTO
-- ==========================================================

-- P-001 (45000 km)
INSERT INTO servicio_realizado (id_movil, id_tipo_mantenimiento, km_servicio, fecha_servicio, observaciones, id_usuario) VALUES
(1, 1,  5000, '2021-04-02 10:00:00', 'Primer cambio de aceite. Sin novedades.', 1),
(1, 8,  5000, '2021-04-02 10:30:00', 'Alineacion y balanceo al inicio del servicio.', 1),
(1, 1, 10000, '2021-07-11 09:00:00', 'Cambio de aceite 10k. Todo normal.', 1),
(1, 2, 10000, '2021-07-11 09:30:00', 'Cambio filtro de aire.', 1),
(1, 1, 15000, '2021-10-06 08:30:00', 'Cambio de aceite 15k.', 1),
(1, 8, 15000, '2021-10-06 09:00:00', 'Alineacion y balanceo 15k km.', 1),
(1, 3, 15000, '2021-10-06 09:30:00', 'Cambio filtro de combustible preventivo.', 1),
(1, 1, 20000, '2022-01-19 08:00:00', 'Cambio de aceite 20k.', 1),
(1, 4, 20000, '2022-01-19 09:00:00', 'Revision de frenos. Pastillas en buen estado.', 1),
(1, 1, 25000, '2022-04-21 10:00:00', 'Cambio de aceite 25k.', 1),
(1, 2, 25000, '2022-04-21 10:30:00', 'Cambio filtro de aire 25k.', 1),
(1, 1, 30000, '2022-08-16 08:00:00', 'Cambio de aceite 30k.', 1),
(1, 5, 30000, '2022-08-16 09:00:00', 'Cambio pastillas de freno delantera.', 1),
(1, 6, 30000, '2022-08-16 10:00:00', 'Cambio de bujias.', 1),
(1, 1, 35000, '2022-12-02 08:30:00', 'Cambio de aceite 35k.', 1),
(1, 8, 35000, '2022-12-02 09:00:00', 'Alineacion y balanceo 35k.', 1),
(1, 1, 40000, '2023-04-11 09:00:00', 'Cambio de aceite 40k.', 1),
(1, 2, 40000, '2023-04-11 09:30:00', 'Cambio filtro de aire.', 1),
(1, 4, 40000, '2023-04-11 10:00:00', 'Revision frenos. Se nota desgaste en traseros.', 1),
(1, 1, 45000, '2023-09-21 08:00:00', 'Cambio de aceite 45k.', 1),
(1,10, 45000, '2023-09-21 09:00:00', 'Revision general a los 45k km.', 1);

-- P-002 VW Amarok (32000 km)
INSERT INTO servicio_realizado (id_movil, id_tipo_mantenimiento, km_servicio, fecha_servicio, observaciones, id_usuario) VALUES
(2, 1,  5000, '2022-02-02 09:00:00', 'Cambio aceite primer servicio.', 2),
(2, 8,  5000, '2022-02-02 09:30:00', 'Alineacion inicial.', 2),
(2, 1, 10000, '2022-05-15 08:00:00', 'Cambio aceite 10k.', 2),
(2, 2, 10000, '2022-05-15 08:30:00', 'Filtro de aire.', 2),
(2, 1, 15000, '2022-09-09 09:00:00', 'Cambio aceite 15k.', 2),
(2, 3, 15000, '2022-09-09 09:30:00', 'Cambio filtro de combustible.', 2),
(2, 1, 20000, '2022-12-21 08:30:00', 'Cambio aceite 20k.', 2),
(2, 4, 20000, '2022-12-21 09:30:00', 'Revision de frenos OK.', 2),
(2, 1, 25000, '2023-04-06 09:00:00', 'Cambio aceite 25k.', 2),
(2, 2, 25000, '2023-04-06 09:30:00', 'Filtro de aire 25k.', 2),
(2, 8, 25000, '2023-04-06 10:00:00', 'Alineacion y balanceo 25k.', 2),
(2, 1, 30000, '2023-08-19 08:00:00', 'Cambio aceite 30k.', 2),
(2, 5, 30000, '2023-08-19 09:00:00', 'Cambio pastillas freno.', 2),
(2, 6, 30000, '2023-08-19 10:00:00', 'Cambio bujias 30k.', 2);

-- P-003 Chevrolet S10 (67000 km)
INSERT INTO servicio_realizado (id_movil, id_tipo_mantenimiento, km_servicio, fecha_servicio, observaciones, id_usuario) VALUES
(3, 1,  5000, '2020-07-16 08:00:00', 'Primer servicio.', 1),
(3, 1, 10000, '2020-10-21 08:00:00', 'Cambio aceite.', 1),
(3, 2, 10000, '2020-10-21 08:30:00', 'Filtro aire.', 1),
(3, 1, 15000, '2021-01-13 09:00:00', 'Cambio aceite.', 1),
(3, 3, 15000, '2021-01-13 09:30:00', 'Filtro combustible.', 1),
(3, 1, 20000, '2021-04-19 08:00:00', 'Cambio aceite.', 1),
(3, 4, 20000, '2021-04-19 09:00:00', 'Revision frenos.', 1),
(3, 1, 25000, '2021-07-23 08:00:00', 'Cambio aceite.', 1),
(3, 2, 25000, '2021-07-23 08:30:00', 'Filtro aire.', 1),
(3, 1, 30000, '2021-10-06 09:00:00', 'Cambio aceite.', 1),
(3, 5, 30000, '2021-10-06 10:00:00', 'Cambio pastillas freno.', 1),
(3, 1, 35000, '2022-01-21 08:00:00', 'Cambio aceite.', 1),
(3, 1, 40000, '2022-04-16 08:00:00', 'Cambio aceite.', 1),
(3, 2, 40000, '2022-04-16 08:30:00', 'Filtro aire.', 1),
(3, 3, 40000, '2022-04-16 09:00:00', 'Filtro combustible.', 1),
(3, 1, 45000, '2022-07-29 08:00:00', 'Cambio aceite.', 1),
(3, 4, 45000, '2022-07-29 09:00:00', 'Revision frenos.', 1),
(3, 1, 50000, '2022-11-11 08:00:00', 'Cambio aceite.', 1),
(3, 7, 50000, '2022-11-11 09:00:00', 'Revision correa distribucion OK.', 1),
(3, 1, 55000, '2023-02-23 08:00:00', 'Cambio aceite.', 1),
(3, 2, 55000, '2023-02-23 08:30:00', 'Filtro aire.', 1),
(3, 1, 60000, '2023-06-15 08:00:00', 'Cambio aceite.', 1),
(3, 5, 60000, '2023-06-15 09:00:00', 'Cambio pastillas.', 1),
(3, 6, 60000, '2023-06-15 10:00:00', 'Cambio bujias.', 1),
(3, 1, 65000, '2023-10-02 08:00:00', 'Cambio aceite.', 1),
(3,10, 65000, '2023-10-02 09:00:00', 'Revision general 65k.', 1);

-- P-005 Ford F-150 (89000 km - mayor historial por ser el mas antiguo)
INSERT INTO servicio_realizado (id_movil, id_tipo_mantenimiento, km_servicio, fecha_servicio, observaciones, id_usuario) VALUES
(5, 1,  5000, '2019-12-02 08:00:00', 'Primer servicio.', 1),
(5, 1, 10000, '2020-03-11 08:00:00', 'Cambio aceite.', 1),
(5, 2, 10000, '2020-03-11 09:00:00', 'Filtro aire.', 1),
(5, 1, 15000, '2020-06-16 08:00:00', 'Cambio aceite.', 1),
(5, 1, 20000, '2020-09-21 08:00:00', 'Cambio aceite.', 1),
(5, 4, 20000, '2020-09-21 09:00:00', 'Revision frenos.', 1),
(5, 1, 25000, '2020-12-09 08:00:00', 'Cambio aceite.', 1),
(5, 2, 25000, '2020-12-09 08:30:00', 'Filtro aire.', 1),
(5, 1, 30000, '2021-03-16 08:00:00', 'Cambio aceite.', 1),
(5, 5, 30000, '2021-03-16 09:00:00', 'Cambio pastillas.', 1),
(5, 1, 35000, '2021-06-23 08:00:00', 'Cambio aceite.', 1),
(5, 1, 40000, '2021-10-01 08:00:00', 'Cambio aceite.', 1),
(5, 4, 40000, '2021-10-01 09:00:00', 'Revision frenos.', 1),
(5, 1, 45000, '2022-01-06 08:00:00', 'Cambio aceite.', 1),
(5, 2, 45000, '2022-01-06 09:00:00', 'Filtro aire.', 1),
(5, 1, 50000, '2022-04-19 08:00:00', 'Cambio aceite.', 1),
(5, 7, 50000, '2022-04-19 09:00:00', 'Revision correa distribucion.', 1),
(5, 1, 55000, '2022-07-26 08:00:00', 'Cambio aceite.', 1),
(5, 1, 60000, '2022-11-03 08:00:00', 'Cambio aceite.', 1),
(5, 5, 60000, '2022-11-03 09:00:00', 'Cambio pastillas y discos.', 1),
(5, 6, 60000, '2022-11-03 10:00:00', 'Cambio bujias.', 1),
(5, 1, 65000, '2023-02-15 08:00:00', 'Cambio aceite.', 1),
(5, 1, 70000, '2023-05-21 08:00:00', 'Cambio aceite.', 1),
(5, 4, 70000, '2023-05-21 09:00:00', 'Revision frenos.', 1),
(5, 1, 75000, '2023-08-09 08:00:00', 'Cambio aceite.', 1),
(5, 2, 75000, '2023-08-09 09:00:00', 'Filtro aire.', 1),
(5, 1, 80000, '2023-10-26 08:00:00', 'Cambio aceite.', 1),
(5, 1, 85000, '2024-01-16 08:00:00', 'Cambio aceite.', 1),
(5,10, 85000, '2024-01-16 09:00:00', 'Revision general 85k.', 1);

-- ==========================================================
-- HISTORIAL DE NOVEDADES
-- ==========================================================

INSERT INTO novedad (id_movil, id_tipo_novedad, descripcion, km_novedad, fecha_hora, id_usuario) VALUES
(1, 5, 'Pinchazo de neumatico trasero izquierdo en ruta. Se coloco neumatico de auxilio.', 22000, '2022-02-15 14:30:00', 1),
(2, 4, 'Falla en sistema electrico. Luces de tablero intermitentes.', 18000, '2022-10-08 16:00:00', 2),
(3, 1, 'Ruido extrano en suspension delantera. Se reviso y ajusto amortiguador.', 42000, '2022-05-20 11:00:00', 1),
(3, 2, 'Choque menor en estacionamiento. Daño en paragolpes trasero.', 58000, '2023-05-10 08:30:00', 1),
(5, 1, 'Motor presenta consumo excesivo de aceite desde los 75000 km.', 75000, '2023-08-10 10:00:00', 1),
(5, 7, 'Falla de motor: se rompio correa de alternador. Vehiculo inmovilizado.', 87000, '2024-03-20 09:00:00', 1),
(6, 3, 'Rotura de vidrio trasero derecho por piedra en ruta.', 14000, '2022-12-05 15:00:00', 2),
(7, 5, 'Neumatico delantero derecho con desgaste irregular. Requiere balanceo urgente.', 35000, '2023-01-18 09:00:00', 1),
(7, 6, 'Rayones en carroceria lateral derecha. Posible vandalismo en via publica.', 48000, '2023-08-25 07:30:00', 1),
(8, 4, 'Falla en alternador. Bateria no carga correctamente.', 28000, '2023-07-14 16:30:00', 2),
(10,1, 'Ruido en caja de cambios al ingresar 4ta velocidad.', 60000, '2023-04-10 10:00:00', 1);

-- ==========================================================
-- ALERTAS PENDIENTES
-- ==========================================================

INSERT INTO alerta (id_movil, id_tipo_mantenimiento, id_estado_alerta, km_disparo) VALUES
(1, 1, 1, 45000),   -- P-001: cambio de aceite pendiente
(3, 4, 1, 65000),   -- P-003: revision de frenos pendiente
(5, 7, 1, 85000),   -- P-005: correa de distribucion pendiente
(5, 1, 1, 85000),   -- P-005: cambio aceite pendiente (fuera de servicio)
(7, 7, 1, 50000),   -- P-007: correa de distribucion pendiente (en reparacion)
(10,1, 1, 70000);   -- P-010: cambio de aceite pendiente

-- Alertas ya procesadas (historial)
INSERT INTO alerta (id_movil, id_tipo_mantenimiento, id_estado_alerta, km_disparo, observaciones, id_usuario, fecha_procesamiento) VALUES
(1, 1, 2, 10000, 'Realizado en taller municipal.', 1, '2021-07-11 10:00:00'),
(1, 1, 2, 20000, 'Realizado en taller municipal.', 1, '2022-01-19 09:00:00'),
(1, 1, 2, 30000, 'Realizado en taller municipal.', 1, '2022-08-16 09:00:00'),
(1, 1, 2, 40000, 'Realizado.', 1, '2023-04-11 10:00:00'),
(2, 1, 2, 10000, 'Realizado.', 2, '2022-05-15 09:00:00'),
(2, 1, 2, 20000, 'Realizado.', 2, '2022-12-21 09:30:00'),
(3, 1, 2, 10000, 'Realizado.', 1, '2020-10-21 09:00:00'),
(3, 7, 2, 50000, 'Correa revisada, en buen estado.', 1, '2022-11-11 10:00:00');

-- ==========================================================
-- INDICES
-- ==========================================================

CREATE INDEX idx_movil_patente ON movil(patente);
CREATE INDEX idx_movil_activo  ON movil(activo);
CREATE INDEX idx_alerta_estado ON alerta(id_estado_alerta);
CREATE INDEX idx_alerta_movil  ON alerta(id_movil);
CREATE INDEX idx_rk_movil      ON registro_kilometraje(id_movil);
CREATE INDEX idx_sr_movil      ON servicio_realizado(id_movil);
CREATE INDEX idx_nov_movil     ON novedad(id_movil);
