@echo off
title SIGMAVEP v2.0 - Inicio
echo.
echo  ================================================
echo   SIGMAVEP v2.0 - Iniciando aplicacion...
echo  ================================================
echo.

:: Buscar Java en el sistema
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo  [ERROR] Java no encontrado en el sistema.
    echo  Por favor instale Java 11 o superior desde:
    echo  https://www.java.com/es/download/
    echo.
    pause
    exit /b 1
)

:: Verificar que el JAR existe
if not exist "sigmavep.jar" (
    echo  [ERROR] No se encontro sigmavep.jar en esta carpeta.
    echo  Asegurese de ejecutar este script desde la carpeta "target".
    echo.
    pause
    exit /b 1
)

:: Ejecutar la aplicacion
java -jar sigmavep.jar

if %errorlevel% neq 0 (
    echo.
    echo  [ERROR] La aplicacion finalizo con un error.
    echo  Verifique que Java este correctamente instalado.
    pause
)
