@echo off
rem =====================================================================
rem  Ejecuta el programa con ventanas (MainGUI) sin necesidad de Maven.
rem  Solo hace falta tener instalado el JDK 17 o superior.
rem  Doble clic sobre este archivo, o desde PowerShell: .\ejecutar_ventanas.bat
rem  Con "demo" arranca con una nave ya tripulada:    .\ejecutar_ventanas.bat demo
rem =====================================================================
cd /d "%~dp0"
chcp 65001 >nul

where javac >nul 2>nul
if errorlevel 1 (
    echo No se encontro javac. Hay que instalar el JDK ^(por ejemplo Temurin 17^):
    echo     https://adoptium.net/temurin/releases/?version=17
    echo Despues de instalarlo, cerra y volve a abrir esta ventana.
    pause
    exit /b 1
)

echo Compilando...
if not exist target\classes mkdir target\classes
dir /s /b src\main\java\*.java > target\fuentes.txt
javac -encoding UTF-8 -d target\classes -cp "lib\*" @target\fuentes.txt
if errorlevel 1 (
    echo.
    echo Hubo errores de compilacion. Revisa los mensajes de arriba.
    pause
    exit /b 1
)
copy /y database\schema.sql target\classes\schema.sql >nul

echo Abriendo las ventanas...
rem -ea activa los assert del modelo (contratos). lib\* es por si esta el jar de H2;
rem si no esta, el programa avisa y guarda los usuarios en memoria.
java -ea -cp "target\classes;lib\*" ar.edu.unmdp.tpg.gui.MainGUI %1
pause
