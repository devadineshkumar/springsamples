# Keystore Generation for Spring Boot SSL/HTTPS

This guide explains how to generate a keystore for enabling SSL/HTTPS in your Spring Boot applications.

## Problem

When trying to run keytool commands directly on Windows, the command window may close immediately, making it difficult to see errors or results.

## Solution

Use the provided `generate-keystore.bat` script which includes:
- Error handling and validation
- Clear instructions and prompts
- The window stays open so you can see all messages
- Default values for quick setup
- Optional customization of settings

## How to Use

### Option 1: Double-Click (Recommended for Windows)

1. Locate the `generate-keystore.bat` file in the repository root
2. Double-click the file to run it
3. Follow the on-screen prompts
4. The window will stay open until you press a key, allowing you to read all output

### Option 2: Command Line

1. Open Command Prompt or PowerShell
2. Navigate to the repository root directory
3. Run the script:
   ```cmd
   generate-keystore.bat
   ```

## What the Script Does

1. **Checks Java Installation**: Verifies that keytool is available (requires Java JDK)
2. **Sets Default Values**:
   - Keystore file: `keystore.p12`
   - Alias: `springboot`
   - Password: `changeit`
   - Validity: 365 days
3. **Generates Keystore**: Creates a PKCS12 keystore with a self-signed certificate
4. **Provides Configuration**: Shows the exact properties to add to your `application.properties`

## Using the Generated Keystore

After successful generation:

1. **Move the keystore file** to your Spring Boot project's `src/main/resources` directory

2. **Update application.properties**:
   ```properties
   server.ssl.enabled=true
   server.ssl.key-store=classpath:keystore.p12
   server.ssl.key-store-password=changeit
   server.ssl.key-store-type=PKCS12
   server.ssl.key-alias=springboot
   server.port=8443
   ```

3. **Run your application**:
   ```cmd
   mvn spring-boot:run
   ```

4. **Access your application**:
   - Open browser to: `https://localhost:8443`
   - You'll see a security warning (normal for self-signed certificates)
   - Click "Advanced" and proceed to accept the certificate

## Customizing the Keystore

When running the script, you can:
- Press `N` (or just Enter) to use default values
- Press `Y` to customize:
  - Keystore filename
  - Alias name
  - Password
  - Validity period in days

## Troubleshooting

### Error: "keytool not found"

**Cause**: Java JDK is not installed or not in the system PATH.

**Solution**:
1. Install Java JDK (version 8 or higher)
2. Set `JAVA_HOME` environment variable to your JDK installation path
3. Add `%JAVA_HOME%\bin` to your system PATH
4. Restart Command Prompt and try again

To verify Java installation:
```cmd
java -version
keytool -help
```

### Window Closes Immediately

**Cause**: Old version of the batch file without pause command.

**Solution**: Make sure you're using the updated `generate-keystore.bat` file which includes the `pause` command at the end.

### Certificate Not Trusted in Browser

**Cause**: This is normal for self-signed certificates.

**Solution**: 
- For development: Click "Advanced" and accept the security exception
- For production: Use a certificate from a trusted Certificate Authority (CA)

## Security Notes

⚠️ **Important**:
- This script generates **self-signed certificates** for **development purposes only**
- Do NOT use self-signed certificates in production
- Change the default password (`changeit`) for any sensitive environments
- Add `*.p12`, `*.jks`, and `*.keystore` to `.gitignore` to avoid committing keystores

## Manual Keytool Command (Advanced)

If you prefer to run keytool manually:

```cmd
keytool -genkeypair -alias springboot -keyalg RSA -keysize 2048 ^
    -storetype PKCS12 -keystore keystore.p12 -validity 365 ^
    -storepass changeit -keypass changeit ^
    -dname "CN=localhost, OU=Development, O=SpringSamples, L=City, ST=State, C=US"
```

**Note**: When running manually, the window may close immediately. Use the batch script instead.

## Additional Resources

- [Spring Boot SSL Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.webserver.configure-ssl)
- [Java Keytool Documentation](https://docs.oracle.com/en/java/javase/11/tools/keytool.html)
- [PKCS12 Keystore Format](https://en.wikipedia.org/wiki/PKCS_12)
