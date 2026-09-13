# CLAUDE.md — RolloApp

App Android nativa (Kotlin + Jetpack Compose + Room) que calcula precio real por hoja
y por rollo de papel higiénico, guarda un historial persistente para comparar marcas,
y permite simular el precio equivalente de un paquete con otra cantidad de hojas.

Package id: `com.rolloapp.app`. Sin backend, sin cuentas, todo local en el dispositivo.

## Startup Workflow

1. **Confirm working directory** with `pwd`
2. **Read this file** completely
3. **Read `feature_list.json`** to see current feature state
4. **Run `./init.sh`** to verify environment is healthy (recrea `local.properties` si
   falta, compila, corre tests)
5. **Review recent commits** with `git log --oneline -5`

If baseline verification is failing, repair that first before adding new scope.

## Stack y decisiones ya tomadas (no las reabras sin razón nueva)

- Kotlin + Jetpack Compose + Material 3, sin XML views.
- Persistencia: Room. Sin Hilt — una `RolloApplication` crea `AppDatabase` +
  `PaperRepository` una sola vez; el ViewModel los recibe por factory manual.
- `compileSdk = targetSdk = 35`, `minSdk = 24` (solo `platforms/android-35` está
  instalado en el SDK local — no subas compileSdk sin instalar la platform nueva).
- Toolchain: **Gradle 9.6.1 + AGP 9.4.0 + Kotlin 2.3.21 + KSP 2.3.12**, sobre
  **JDK 26** (`/opt/homebrew/opt/openjdk`). AGP 8.x NO corre sobre Gradle 9.6 (API
  interna removida) — no bajes AGP sin subir Gradle también, o vas a romper el build.
  AGP 9 ya no necesita el plugin `org.jetbrains.kotlin.android` (Kotlin viene
  incorporado); sí sigue haciendo falta `org.jetbrains.kotlin.plugin.compose`.

## Gotchas de entorno de esta máquina (ya nos mordieron una vez cada uno)

- **No hay `java` en el PATH.** Antes de correr `./gradlew` a mano, exportá:
  ```bash
  export JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home
  ```
  `org.gradle.java.home` en `gradle.properties` solo elige el JVM del *daemon*; el
  script `gradlew` igual necesita un `java` en PATH para arrancar el launcher.
  `/opt/homebrew/opt/openjdk` (sin el resto de la ruta) es solo symlinks, no sirve
  como JAVA_HOME.
- **`local.properties` está gitignored y desaparece en cada checkout/worktree
  nuevo.** Si `./gradlew` falla por no encontrar el SDK, recreálo:
  ```bash
  echo "sdk.dir=$HOME/Library/Android/sdk" > local.properties
  ```
- **No hay Android Studio ni emulador instalado.** Todo se compila e instala por
  línea de comandos (`./gradlew`, `adb`). Si conectás un celular por USB, `adb
  devices` lo va a mostrar `unauthorized` hasta aceptar el popup de "USB
  debugging" en la pantalla del teléfono.
- `./init.sh` ya resuelve los dos primeros puntos automáticamente.

## Working Rules

- **One feature at a time**: elegí una entrada `not-started` o `in-progress` de
  `feature_list.json`.
- **Verification required**: no reportes algo como terminado sin correr
  `./init.sh` (o al menos `testDebugUnitTest` + `assembleDebug`).
- **Update artifacts**: antes de cerrar sesión, actualizá `progress.md` y
  `feature_list.json` con evidencia real (comandos + resultado, no solo "listo").
- **Stay in scope**: no toques pantallas/archivos fuera de la feature activa.
- **Leave clean state**: la próxima sesión debe poder correr `./init.sh` ya mismo.

## End of Session

Before ending a session:

1. Corré `./init.sh` una última vez — debe terminar limpio.
2. Actualizá `feature_list.json` (status + evidence) y `progress.md` con lo que
   realmente pasó, no un resumen optimista.
3. Anotá bloqueos o riesgos sin resolver en `progress.md`.
4. Commiteá con mensaje descriptivo; dejá el repo en estado que permita a la
   próxima sesión correr `./init.sh` de inmediato.

## Definition of Done

- [ ] Comportamiento implementado
- [ ] `./gradlew testDebugUnitTest` pasa
- [ ] `./gradlew assembleDebug` genera el APK sin errores
- [ ] Si el cambio toca UI: probado a mano en el dispositivo conectado (`adb
      install -r app/build/outputs/apk/debug/app-debug.apk`), no solo compilado
- [ ] Evidencia registrada en `feature_list.json` / `progress.md`

## Verification Commands

```bash
./init.sh                        # setup + tests + build, ver abajo
./gradlew testDebugUnitTest      # solo unit tests del cálculo (rápido)
./gradlew assembleDebug          # genera app/build/outputs/apk/debug/app-debug.apk
adb devices                      # confirma que el celular está autorizado
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Escalation

- **Decisiones de arquitectura**: ya están tomadas arriba; si una feature nueva
  las contradice, preguntale al usuario antes de cambiar el toolchain o el patrón
  de DI.
- **Fallos de compilación por versión de AGP/Gradle/JDK**: diagnosticalos por el
  mensaje de error (suele decir exactamente qué versión falta), no adivines.
- **Ambigüedad de alcance**: releé `feature_list.json`.
