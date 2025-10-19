Java Modules Example

This small multi-module Maven project demonstrates the Java Platform Module System (JPMS).

Structure
- parent pom.xml — reactor for two modules and Java version (21).
- greetings/ — a library module that exports the package `com.example.greetings`.
  - module-info.java: `module com.example.greetings { exports com.example.greetings; }`
  - `com.example.greetings.Greeter` — simple static greet(String) method.
- app/ — an application module that requires `com.example.greetings` and uses the Greeter.
  - module-info.java: `module com.example.app { requires com.example.greetings; }`
  - `com.example.app.AppMain` — main class that prints a greeting.

Build
From the repo root run the Maven wrapper (Windows):

```
./mvnw.cmd -f java-modules-example\pom.xml clean package -DskipTests
```

This produces:
- greetings/target/greetings-1.0-SNAPSHOT.jar
- app/target/app-1.0-SNAPSHOT.jar

Run (modular run)
Use the module-path (-p or --module-path) and the -m/--module option to run the app's main class:

```
java -p "java-modules-example\greetings\target\greetings-1.0-SNAPSHOT.jar;java-modules-example\app\target\app-1.0-SNAPSHOT.jar" -m com.example.app/com.example.app.AppMain Alice
```

Expected output:

```
Hello, Alice!
```

Explanation
- module-info.java: describes module name and its dependencies (requires) and public packages to consumers (exports).
- Requires: a module must declare which other modules it depends on.
- Exports: a module explicitly makes packages visible to other modules.

Notes & common pitfalls
- A JAR must be modular (contain module-info.class) or be placed on the classpath as an automatic module; for JPMS you should prefer modular JARs.
- If you get the Java usage output instead of running the program, it usually means the launcher didn't parse the arguments (malformed option or missing -m). Use the -p and -m short forms and quote the module-path on Windows if it contains semicolons or spaces.

Next steps
- Try adding a service interface and `provides`/`uses` to see JPMS service loading.
- Convert an existing large library into a modular JAR (add module-info) and observe the module graph with `--describe-module` and `--list-modules`.

