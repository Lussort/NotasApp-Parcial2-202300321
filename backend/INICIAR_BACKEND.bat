@echo off
cd /d "%~dp0"
if exist .venv\Scripts\python.exe goto start
py -3 --version >nul 2>&1
if errorlevel 1 goto use_python
py -3 -m venv .venv
goto check
:use_python
python --version >nul 2>&1
if errorlevel 1 goto missing
python -m venv .venv
:check
if not exist .venv\Scripts\python.exe goto missing
:start
.venv\Scripts\python.exe -m pip install -r requirements.txt
if errorlevel 1 goto failed
.venv\Scripts\python.exe app.py
pause
exit /b
:missing
echo Instala Python 3.13 y activa Add python.exe to PATH. Luego abre de nuevo este archivo.
pause
exit /b 1
:failed
echo No se pudieron descargar las dependencias. Revisa la conexion y el mensaje anterior.
pause
exit /b 1
