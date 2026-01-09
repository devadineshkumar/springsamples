@echo off
REM Script to generate a keystore for Spring Boot SSL/HTTPS configuration
REM This script creates a self-signed certificate for development purposes

echo ========================================
echo Spring Boot Keystore Generator
echo ========================================
echo.
echo This script will generate a keystore file for SSL/HTTPS configuration.
echo.

REM Check if keytool is available
where keytool >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: keytool not found!
    echo Please ensure Java JDK is installed and JAVA_HOME is set correctly.
    echo Add %JAVA_HOME%\bin to your system PATH.
    echo.
    pause
    exit /b 1
)

echo keytool found successfully.
echo.

REM Set default values
set KEYSTORE_NAME=keystore.p12
set KEYSTORE_ALIAS=springboot
set KEYSTORE_PASSWORD=changeit
set VALIDITY_DAYS=365

echo Using default configuration:
echo   Keystore file: %KEYSTORE_NAME%
echo   Alias: %KEYSTORE_ALIAS%
echo   Password: %KEYSTORE_PASSWORD%
echo   Validity: %VALIDITY_DAYS% days
echo.

REM Prompt user if they want to customize
set /p CUSTOMIZE="Do you want to customize these settings? (y/N): "
if /i "%CUSTOMIZE%"=="y" (
    echo.
    echo NOTE: Passwords will be visible when typed (Windows limitation)
    echo.
    
    set /p KEYSTORE_NAME="Enter keystore filename (default: keystore.p12): "
    if "%KEYSTORE_NAME%"=="" set KEYSTORE_NAME=keystore.p12
    
    set /p KEYSTORE_ALIAS="Enter alias (default: springboot): "
    if "%KEYSTORE_ALIAS%"=="" set KEYSTORE_ALIAS=springboot
    
    set /p KEYSTORE_PASSWORD="Enter password (default: changeit): "
    if "%KEYSTORE_PASSWORD%"=="" set KEYSTORE_PASSWORD=changeit
    
    set /p VALIDITY_DAYS="Enter validity in days (default: 365): "
    if "%VALIDITY_DAYS%"=="" set VALIDITY_DAYS=365
    
    set /p COMMON_NAME="Enter hostname/CN (default: localhost): "
    if "%COMMON_NAME%"=="" set COMMON_NAME=localhost
) else (
    set COMMON_NAME=localhost
)

echo.
echo Generating keystore...
echo.

REM Generate the keystore
keytool -genkeypair -alias %KEYSTORE_ALIAS% -keyalg RSA -keysize 2048 ^
    -storetype PKCS12 -keystore %KEYSTORE_NAME% -validity %VALIDITY_DAYS% ^
    -storepass %KEYSTORE_PASSWORD% -keypass %KEYSTORE_PASSWORD% ^
    -dname "CN=%COMMON_NAME%, OU=Development, O=SpringSamples, L=City, ST=State, C=US"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo SUCCESS! Keystore generated successfully.
    echo ========================================
    echo.
    echo Keystore file: %KEYSTORE_NAME%
    echo.
    echo Add the following to your application.properties:
    echo.
    echo server.ssl.enabled=true
    echo server.ssl.key-store=classpath:%KEYSTORE_NAME%
    echo server.ssl.key-store-password=%KEYSTORE_PASSWORD%
    echo server.ssl.key-store-type=PKCS12
    echo server.ssl.key-alias=%KEYSTORE_ALIAS%
    echo server.port=8443
    echo.
    echo Move the %KEYSTORE_NAME% file to your src/main/resources directory.
    echo.
) else (
    echo.
    echo ========================================
    echo ERROR! Failed to generate keystore.
    echo ========================================
    echo.
    echo Please check the error message above.
    echo.
)

echo Press any key to exit...
pause >nul
