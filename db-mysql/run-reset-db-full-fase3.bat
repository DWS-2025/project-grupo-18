@echo off
REM Ruta del ejecutable MySQL
set MYSQL_BIN="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

REM Usuario, contraseña y base de datos
set USER=root
set PASSWORD=Jobmatcher_123
set DATABASE=jobmatcher

REM Usamos la ruta del .bat + nombre del archivo SQL
set SQLFILE=%~dp0reset-db-full-fase3.sql

REM Verificar si mysql.exe existe
if not exist %MYSQL_BIN% (
    echo ERROR: No se pudo encontrar mysql.exe en:
    echo    %MYSQL_BIN%
    echo Asegúrate de que MySQL está instalado y la ruta es correcta.
    pause
    exit /b
)

REM Verificar si el archivo SQL existe
if not exist "%SQLFILE%" (
    echo ERROR: No se pudo encontrar el archivo SQL:
    echo    "%SQLFILE%"
	echo Asegurate de que existe bien nombrado y en esta ubicacion.
    pause
    exit /b
)

REM Ejecutamos el comando con opciones para silenciar advertencias
%MYSQL_BIN% -u %USER% -p%PASSWORD% -D %DATABASE% --silent --skip-column-names 2>nul < "%SQLFILE%"

REM Mensaje de éxito
echo.
echo Base de datos "%DATABASE%" restaurada correctamente desde:
echo    "%SQLFILE%"
echo.
pause
