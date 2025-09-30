@echo off
rem written by gemini
echo Creating output directory...
if not exist bin mkdir bin

echo Compiling source files...
dir /s /b src\*.java > sources.txt
javac -d bin -sourcepath src @sources.txt
del sources.txt

echo.
echo Running the application...
java -cp "bin;res" engine.Core
pause
