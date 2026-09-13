# Session Progress Log

## Current State

**Last Updated:** 2026-09-12
**Active Feature:** feat-008 (harness de documentación) — feat-007 (verificación
manual end-to-end) queda como siguiente feature real de producto.

## Status

### What's Done

- [x] feat-001 — Toolchain Gradle 9.6.1 + AGP 9.4.0 + Kotlin 2.3.21 sobre JDK 26,
      compila desde cero.
- [x] feat-002 — Capa de datos Room (PaperPackage, Dao, AppDatabase, Repository).
- [x] feat-003 — PaperPriceCalculator + 16 unit tests, todos pasando.
- [x] feat-004 — UI Compose completa (Agregar, Comparar, Detalle/Simular) +
      Navigation + ViewModel.
- [x] feat-005 — `app-debug.apk` generado y verificado (aapt2, apksigner, lint
      0 errores).
- [x] feat-006 — APK instalado en un Samsung Galaxy real (SM_S918U1) vía adb.
- [~] feat-008 — Harness de documentación (este mismo trabajo), en progreso.

### What's In Progress

- [ ] Commitear y mergear el harness (CLAUDE.md, feature_list.json, progress.md,
      session-handoff.md, init.sh) a `main`.

### What's Next

1. feat-007: probar la app a mano en el celular ya instalado — agregar 2-3
   paquetes reales, verificar orden en "Comparar", probar el simulador de
   hojas, y confirmar que el historial sobrevive a cerrar/reabrir la app.
2. Evaluar si hace falta un release build firmado (hoy solo hay debug APK, a
   propósito — así se decidió en el plan original).

## Blockers / Risks

- [ ] No hay emulador ni Android Studio en esta máquina — toda verificación de
      UI depende de tener un celular físico conectado y autorizado por adb.
- [ ] `local.properties` no está trackeado en git: cualquier checkout/worktree
      nuevo necesita recrearlo (`init.sh` ya lo hace solo).

## Decisions Made

- **AGP 9.4.0 en vez de bajar Gradle a 9.5**: Gradle 9.6.1 es el único Gradle
  instalado y el que soporta JDK 26 (el único JDK de la máquina). AGP 8.x no
  corre sobre Gradle 9.6 (usa una API interna removida), así que se subió AGP
  en vez de arriesgar romper el soporte de JDK.
  - Alternativas consideradas: bajar Gradle a 9.5 (se descartó por perder
    soporte JDK 26).
- **Sin Hilt**: app chica, DI manual vía `RolloApplication` + factory alcanza
  sin la capa extra.
- **compileSdk/targetSdk fijo en 35**: es la única platform instalada en el SDK
  local (`~/Library/Android/sdk/platforms/android-35`); librerías AndroidX más
  nuevas piden compileSdk 36/37, por eso el set de dependencias quedó fijado a
  versiones de mediados de 2025 (Compose BOM 2025.06.01, Room 2.7.1, etc.).

## Files Modified This Session

- `CLAUDE.md`, `feature_list.json`, `progress.md`, `session-handoff.md`,
  `init.sh` — creados/reescritos con hechos reales del proyecto (reemplazan un
  scaffold genérico "java-gradle" con placeholders).

## Evidence of Completion

- [x] Tests pass: `./gradlew testDebugUnitTest` → 16 tests, 0 fallos.
- [x] APK build: `./gradlew assembleDebug` → BUILD SUCCESSFUL,
      `app/build/outputs/apk/debug/app-debug.apk` (~10.5 MiB).
- [x] Instalación real: `adb install -r app-debug.apk` → Success en SM_S918U1.
- [ ] Uso manual completo de la app (feat-007) — pendiente.

## Notes for Next Session

Arrancá con `./init.sh`. Si falla por SDK o Java, releé la sección "Gotchas de
entorno" en `CLAUDE.md` — ya están documentadas las dos trampas que nos
mordieron (local.properties gitignored, JAVA_HOME del launcher vs. daemon).
