@echo off
REM Quick keystore generator with no prompts - uses default values
REM For customization options, use generate-keystore.bat instead

echo Generating keystore with default settings...
echo.

keytool -genkeypair -alias springboot -keyalg RSA -keysize 2048 ^
    -storetype PKCS12 -keystore keystore.p12 -validity 365 ^
    -storepass changeit -keypass changeit ^
    -dname "CN=localhost, OU=Development, O=SpringSamples, L=City, ST=State, C=US"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo SUCCESS! Keystore generated: keystore.p12
    echo.
    echo Add these properties to application.properties:
    echo server.ssl.enabled=true
    echo server.ssl.key-store=classpath:keystore.p12
    echo server.ssl.key-store-password=changeit
    echo server.ssl.key-store-type=PKCS12
    echo server.ssl.key-alias=springboot
    echo server.port=8443
    echo.
) else (
    echo.
    echo ERROR! Failed to generate keystore.
    echo Make sure Java JDK is installed and keytool is in your PATH.
    echo.
)

echo Press any key to close this window...
pause >nul
