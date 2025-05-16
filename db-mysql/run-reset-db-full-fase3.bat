@echo off
REM Executable path for MySQL
set MYSQL_BIN="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

REM Username, password, and database name
set USER=root
set PASSWORD=Jobmatcher_123
set DATABASE=jobmatcher

REM .bat + SQL file name
set SQLFILE=%~dp0reset-db-full-fase3.sql

REM Verify if the MySQL executable exists
if not exist %MYSQL_BIN% (
    echo ERROR: No se pudo encontrar mysql.exe en:
    echo    %MYSQL_BIN%
    echo Asegúrate de que MySQL está instalado y la ruta es correcta.
    pause
    exit /b
)

REM Verify if the SQL file exists
if not exist "%SQLFILE%" (
    echo ERROR: No se pudo encontrar el archivo SQL:
    echo    "%SQLFILE%"
	echo Asegurate de que existe bien nombrado y en esta ubicacion.
    pause
    exit /b
)

REM Execute the command with options to silence warnings
%MYSQL_BIN% -u %USER% -p%PASSWORD% -D %DATABASE% --silent --skip-column-names 2>nul < "%SQLFILE%"

REM Success message
echo.
echo Base de datos "%DATABASE%" restaurada correctamente desde:
echo    "%SQLFILE%"
echo.
pause
