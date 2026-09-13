#!/bin/bash
set -e

echo "=== RolloApp: Harness Initialization ==="

# Gotcha 1: local.properties está gitignored y desaparece en cada
# checkout/worktree nuevo. Sin esto, ./gradlew falla sin encontrar el SDK.
if [ ! -f local.properties ]; then
  echo "local.properties no existe, recreando con el SDK local..."
  echo "sdk.dir=$HOME/Library/Android/sdk" > local.properties
fi

# Gotcha 2: no hay `java` en el PATH en esta máquina. org.gradle.java.home en
# gradle.properties solo elige el JVM del daemon; el launcher de ./gradlew
# igual necesita JAVA_HOME exportado para arrancar.
if [ -z "$JAVA_HOME" ]; then
  export JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home
fi

echo "=== ./gradlew testDebugUnitTest ==="
./gradlew testDebugUnitTest

echo "=== ./gradlew assembleDebug ==="
./gradlew assembleDebug

APK=app/build/outputs/apk/debug/app-debug.apk
if [ -f "$APK" ]; then
  echo "APK generado: $APK ($(du -h "$APK" | cut -f1))"
else
  echo "ERROR: no se encontró $APK después de assembleDebug" >&2
  exit 1
fi

echo "=== Verification Complete ==="
echo ""
echo "Próximos pasos:"
echo "1. Leer feature_list.json para ver el estado de features"
echo "2. Elegir UNA feature not-started o in-progress"
echo "3. Implementar solo esa feature"
echo "4. Si toca UI: adb devices && adb install -r $APK, probar a mano"
echo "5. Re-correr este script antes de dar por terminado"
