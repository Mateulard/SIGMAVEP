<p align="center">
  <br/>
  <img src="https://capsule-render.vercel.app/api?type=waving&color=0a1628&height=100&section=header&text=SIGMAVEP+v2.0&fontSize=40&fontColor=ffffff&fontAlignY=32&desc=Sistema+de+Gestión+del+Mantenimiento+Preventivo+de+Vehículos+Policiales&descSize=14&descColor=a8b2d1" width="100%"/>
  <br/>

  <p align="center">
    <b>Prototipo Funcional</b> &nbsp;·&nbsp; EFIP I &nbsp;·&nbsp; Universidad Siglo 21
  </p>

  <p align="center">
    <a href="https://github.com/Mateulard/sigmavep"><img src="https://img.shields.io/badge/Java-11-F89820?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 11"/></a>
    <a href="https://github.com/Mateulard/sigmavep"><img src="https://img.shields.io/badge/MySQL-8.0+-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL 8.0+"/></a>
    <a href="https://github.com/Mateulard/sigmavep"><img src="https://img.shields.io/badge/Swing-Desktop_UI-007396?style=for-the-badge&logo=oracle&logoColor=white" alt="Java Swing"/></a>
    <a href="https://github.com/Mateulard/sigmavep"><img src="https://img.shields.io/badge/JDBC-8.3.0-006400?style=for-the-badge" alt="JDBC"/></a>
  </p>
</p>

<br/>

---

## Acerca del Proyecto

SIGMAVEP centraliza la operación de mantenimiento preventivo de una flota vehicular policial. Permite administrar móviles, registrar kilometraje, generar alertas automáticas de mantenimiento, registrar servicios realizados, novedades operativas y usuarios — con persistencia en **MySQL 8.0** e interfaz de escritorio en **Java Swing**.

### Funcionalidades Principales

| Módulo | Descripción |
|:-------|:------------|
| **Gestión de Móviles** | Alta, edición y baja lógica de vehículos policiales con patente, marca, modelo, dependencia y zona |
| **Registro de Kilometraje** | Actualización de KM con historial completo por vehículo |
| **Alertas Automáticas** | Generación al superar el KM límite definido por tipo de mantenimiento; posibilidad de procesar o postergar |
| **Servicios de Mantenimiento** | Registro de servicios realizados con costo y descripción detallada |
| **Novedades Operativas** | Carga y consulta de novedades vinculadas a la flota |
| **Administración de Usuarios** | ABM con control de roles RBAC: Administrador, Supervisor, Operador |
| **Reportes de Flota** | Estadísticas globales de estado y dependencia con exportación a CSV |

> **Nota:** La base de datos se crea e inicializa automáticamente en el primer inicio si no existe, ejecutando el script `sigmavep_schema.sql` incluido en el proyecto.
>
> **Importante:** Debe tener **MySQL Server 8.0+** instalado y en ejecución. La aplicación no incluye motor de base de datos embebido.

---

## Casos de Uso

| ID | Caso de Uso | Descripción |
|:--:|:------------|:------------|
| CU-01 | Gestionar Móviles | ABM de vehículos: alta, edición, baja lógica por estado |
| CU-02 | Actualizar Kilometraje | Registrar nuevo KM; genera alerta automática si supera límite |
| CU-03 | Gestionar Alertas | Consultar, procesar o postergar alertas de mantenimiento |
| CU-04 | Registrar Servicio | Cargar servicios de mantenimiento realizados con costo |
| CU-05 | Gestionar Novedades | Registrar y consultar novedades operativas por móvil |
| CU-06 | Consultar Reporte de Flota | Vista estadística del estado global de la flota |
| CU-07 | Gestionar Usuarios | ABM de usuarios con asignación de roles (solo Administrador) |
| CU-08 | Configuración Inicial | Asistente para conectar y crear la base de datos |

---

## Requisitos Previos

| Componente | Versión Mínima | Observaciones |
|:-----------|:--------------:|:--------------|
| **Java JDK** | 11 | Adoptium / Eclipse Temurin recomendado |
| **MySQL Server** | 8.0+ | Debe estar instalado y ejecutándose |

```bash
# Verificar instalación de Java
java -version
```

---

## Instalación y Ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/Mateulard/sigmavep.git
cd sigmavep
```

<details>
<summary><b>Alternativa: descargar como .zip</b></summary>

Descargar desde **Code → Download ZIP** en GitHub y descomprimir.

</details>

### 2. Instalar MySQL Server

> La aplicación **no arranca sin MySQL**.

<details>
<summary><b>🖥️ Opción A — MySQL Community Server (recomendado)</b></summary>

1. Descargar desde [dev.mysql.com/downloads/mysql](https://dev.mysql.com/downloads/mysql/)
2. Instalar y definir usuario `root` y contraseña durante el asistente
3. Asegurarse de que el servicio `MySQL80` esté **Iniciado** en Servicios de Windows

</details>

<details>
<summary><b>📦 Opción B — XAMPP (más simple para pruebas)</b></summary>

1. Descargar desde [apachefriends.org](https://www.apachefriends.org/)
2. Instalar XAMPP e iniciar el módulo **MySQL** desde el panel de control
3. Por defecto: usuario `root`, contraseña vacía, puerto `3306`

</details>

```bash
# Verificar que MySQL responde
mysql -u root -p -e "SELECT 1;"
```

### 3. Configurar la conexión

Al primer inicio, si las credenciales por defecto no coinciden, SIGMAVEP muestra automáticamente el **Asistente de Configuración** con opciones para:

- Configurar host, puerto, base de datos, usuario y contraseña
- **Probar Conexión** antes de guardar
- **Crear Base de Datos** automáticamente si no existe

> Los datos se guardan en `db.properties` en la misma carpeta del JAR — no es necesario editar el JAR.

### 4. Ejecutar la aplicación

<details>
<summary><b>▶️ Desde script Windows</b></summary>

```
Doble clic en → target/ejecutar_sigmavep.bat
```

</details>

<details>
<summary><b>⌨️ Desde terminal</b></summary>

```bash
java -jar target/sigmavep.jar
```

</details>

### 5. Compilar desde código fuente

<details>
<summary><b>🔨 Instrucciones de compilación</b></summary>

```bash
# Compilar
javac -encoding UTF-8 -cp target\lib\mysql-connector-j-8.3.0.jar -d target\classes @sources.txt

# Reempaquetar el JAR
cd target\classes
jar cfm ..\sigmavep.jar ..\MANIFEST.MF .
```

> El archivo `sources.txt` en la raíz lista todos los `.java` del proyecto.

</details>

---

## Credenciales de Prueba

| Usuario | Contraseña | Rol |
|:--------|:----------:|:----|
| `admin` | `admin123` | Administrador |
| `supervisor` | `super123` | Supervisor |

---

## Base de Datos

### Creación Automática

Si la base `sigmavep` no existe, el sistema puede crearla al iniciar desde el diálogo de configuración, ejecutando el script embebido `sigmavep_schema.sql` — incluye tablas, relaciones, índices y datos iniciales.

### Tablas Principales

| Tabla | Descripción |
|:------|:------------|
| `movil` | Vehículos policiales con patente, marca, modelo y estado |
| `dependencia` | Dependencias policiales de cada móvil |
| `zona` | Zonas geográficas que agrupan dependencias |
| `estado_movil` | Estados posibles (Operativo, En Taller, Baja, etc.) |
| `registro_kilometraje` | Historial de actualizaciones de KM por vehículo |
| `tipo_mantenimiento` | Tipos de servicio preventivo con KM límite de disparo |
| `alerta` | Alertas generadas al superar el KM límite |
| `estado_alerta` | Estados de alerta (Pendiente, Procesada, Postergada) |
| `servicio_realizado` | Registro de servicios de mantenimiento efectuados |
| `novedad` | Novedades operativas vinculadas a móviles |
| `tipo_novedad` | Categorías de novedades |
| `usuario` | Usuarios del sistema con roles y contraseña hasheada |
| `rol` | Roles del sistema (Administrador, Supervisor, Operador) |

---

## Estructura del Proyecto

```
sigmavep/
├── src/main/java/com/sigmavep/
│   ├── App.java                              # Punto de entrada
│   ├── controlador/
│   │   ├── AuthControlador.java              # Login y autenticación
│   │   ├── Controller.java                   # Interfaz base de controladores
│   │   └── MenuPrincipalControlador.java     # Controlador central (MVC)
│   ├── dao/
│   │   ├── BaseDAO.java                      # Interfaz genérica DAO
│   │   └── impl/                             # 14 implementaciones JDBC
│   ├── modelo/
│   │   ├── entidad/                          # 14 entidades del dominio
│   │   └── enumerado/                        # EstadoMovilEnum, EstadoAlertaEnum, RolEnum
│   ├── util/
│   │   ├── ConexionMySQL.java                # Configuración y pool de conexión
│   │   ├── HashUtil.java                     # SHA-256 para contraseñas
│   │   ├── Session.java                      # Sesión activa del usuario (RBAC)
│   │   ├── ValidadorUtil.java                # Validaciones de dominio
│   │   ├── OrdenamientoUtil.java             # Utilidades de ordenamiento
│   │   └── ArchivoUtil.java                  # Exportación CSV
│   ├── exepcion/                             # Excepciones de dominio personalizadas
│   └── vista/                                # Todos los JFrame/JPanel Swing
│       ├── SetupFrame.java                   # Asistente de configuración inicial
│       ├── LoginFrame.java                   # Pantalla de inicio de sesión
│       ├── MenuPrincipalFrame.java           # Ventana principal con sidebar
│       ├── MovilPanel.java                   # Gestión de móviles
│       ├── AlertaPanel.java                  # Gestión de alertas
│       ├── KilometrajePanel.java             # Registro de kilometraje
│       ├── MantenimientoPanel.java           # Servicios realizados
│       ├── NovedadPanel.java                 # Novedades operativas
│       ├── FlotaPanel.java                   # Reporte de flota
│       ├── ReportePanel.java                 # Reportes y exportación CSV
│       ├── UsuarioPanel.java                 # Gestión de usuarios
│       ├── BaseFrame.java                    # Frame base con icono y configuración
│       └── Estilo.java                       # Sistema de diseño visual
├── src/main/resources/
│   ├── db.properties                         # Configuración JDBC por defecto
│   └── sigmavep_schema.sql                   # Schema completo de la base de datos
├── target/
│   ├── sigmavep.jar                          # JAR ejecutable (recursos embebidos)
│   ├── lib/
│   │   └── mysql-connector-j-8.3.0.jar       # Driver JDBC MySQL
│   ├── MANIFEST.MF                           # Manifiesto del JAR
│   └── ejecutar_sigmavep.bat                 # Lanzador Windows
├── sources.txt                               # Lista de fuentes para compilación
└── README.md
```

---

## Configuración de Conexión

### Diálogo Automático

Si MySQL no está disponible o las credenciales son incorrectas, aparece el **Asistente de Configuración de SIGMAVEP**. Tras presionar *Probar Conexión* exitosamente, *Guardar y Continuar* persiste la configuración y lanza el sistema.

### Archivo `db.properties`

```properties
db.host=localhost
db.port=3306
db.name=sigmavep
db.user=root
db.password=
```

**Prioridad de lectura:**

1. `db.properties` en el directorio de trabajo (generado por el diálogo o editado manualmente)
2. Archivo embebido en el JAR (`/src/main/resources`)
3. Valores por defecto (`localhost:3306`, usuario `root`, contraseña vacía)

---

## Solución de Problemas

| Problema | Solución |
|:---------|:---------|
| `Java Exception` al abrir el `.jar` | Usar `ejecutar_sigmavep.bat` o `java -jar target/sigmavep.jar` en terminal |
| `Access denied` (MySQL) | Usuario o contraseña incorrectos; usar el diálogo de configuración al iniciar |
| `Communications link failure` | MySQL no está instalado, no está iniciado, o el puerto/host son incorrectos |
| La BD no se crea | El usuario MySQL necesita permiso `CREATE DATABASE`; usar `root` o usuario con privilegios |
| Contraseña distinta a la del README | Normal en otra PC: el diálogo guarda la configuración local en `db.properties` |

---

## Decisiones Técnicas

| Aspecto | Decisión |
|:--------|:---------|
| **Arquitectura** | Capas separadas modelo → DAO → controlador → vista (MVC simplificado) |
| **Acceso a datos** | JDBC puro con `PreparedStatement` y `try-with-resources` (sin ORM) |
| **Seguridad** | Contraseñas hasheadas con SHA-256 (`HashUtil`) |
| **Sesión** | Clase estática `Session` con control de roles por `RolEnum` (RBAC) |
| **Portabilidad** | Configuración MySQL editable sin recompilar (`db.properties` externo) |
| **Alertas** | Generación automática al registrar kilometraje si supera el límite del tipo de mantenimiento |
| **Exportación** | CSV disponible en el módulo Reportes para todos los listados |
| **UI** | Swing con paleta azul marino policial + dorado, fuente Segoe UI, Nimbus L&F |

---

<p align="center">
  <br/>
  <img src="https://capsule-render.vercel.app/api?type=waving&color=0a1628&height=80&section=footer"/>
  <br/>
  <b>Mateo German Ruiz Díaz</b>
  <br/>
  <i>EFIP I — Universidad Siglo 21</i>
  <br/>
  <br/>
  <a href="https://github.com/Mateulard"><img src="https://img.shields.io/badge/GitHub-Mateulard-181717?style=flat-square&logo=github" alt="GitHub"/></a>
</p>
