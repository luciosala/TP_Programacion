@echo off
rem =====================================================================
rem  Corre las pruebas por consola sin necesidad de Maven.
rem  Sin argumentos corre PruebaEscenarios. Se le puede pasar otra clase:
rem     ejecutar_pruebas.bat ar.edu.unmdp.tpg.test.PruebaWarp
rem     ejecutar_pruebas.bat ar.edu.unmdp.tpg.app.Main
rem     ejecutar_pruebas.bat ar.edu.unmdp.tpg.visor.PruebaVisor
rem =====================================================================
cd /d "%~dp0"
chcp 65001 >nul

where javac >nul 2>nul
if errorlevel 1 (
    echo No se encontro javac. Hay que instalar el JDK 17: https://adoptium.net/temurin/releases/?version=17
    pause
    exit /b 1
)

set PROGRAMA=%1
if "%PROGRAMA%"=="" set PROGRAMA=ar.edu.unmdp.tpg.test.PruebaEscenarios

echo Compilando...
if not exist target\classes mkdir target\classes
dir /s /b src\main\java\*.java > target\fuentes.txt
javac -encoding UTF-8 -d target\classes -cp "lib\*" @target\fuentes.txt
if errorlevel 1 (
    echo Hubo errores de compilacion.
    pause
    exit /b 1
)
copy /y database\schema.sql target\classes\schema.sql >nul

echo Ejecutando %PROGRAMA% ...
echo.
java -ea -cp "target\classes;lib\*" %PROGRAMA%
pause
