# Session Progress Log

## Current State

**Last Updated:** 2026-09-13
**Active Feature:** feat-010 (reversión del rediseño a Material 3 convencional)
— terminado en la rama `worktree-rediseno-m3`, sin mergear todavía a `main`.

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
- [x] feat-008 — Harness de documentación, mergeado a `main`.
- [x] feat-009 — Rediseño "HUD de precisión" — **rechazado por el usuario**
      ("el diseño está horrible y también la tipografía", "todo el concepto")
      tras verlo instalado en el celular real. Superado por feat-010.
- [x] feat-010 — Reversión a Material 3 convencional (ver Decisions Made).

### What's In Progress

- [ ] Commitear feat-010 y mergear a `main`.

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

- **feat-009 (HUD de precisión) se descarta por completo, no se itera sobre
  él.** El usuario lo rechazó como concepto, no por un detalle puntual —
  seguir ajustando esa base habría sido pulir algo que ya no se quiere.
- **`dynamicColor` vuelve a `true` (Material You)**: es la opción de menor
  riesgo de gusto — el sistema arma la paleta desde el wallpaper del usuario,
  que es "el look de Android moderno" que la gente ya reconoce, en vez de
  apostar de nuevo a una paleta hecha a mano. El fallback estático (pre-Android
  12) usa un solo hue teal (#00695C) con **todos** los roles de `ColorScheme`
  explícitos (no solo primary/secondary/tertiary) — dejar que
  `lightColorScheme()`/`darkColorScheme()` rellenen el resto es lo que había
  filtrado violeta de Material Baseline en el rediseño anterior.
- **Tipografía: `FontFamily.Default` en toda la escala, sin excepción.** Se
  sacaron Space Grotesk y JetBrains Mono (y los `.ttf` de `res/font/`) — el
  usuario los calificó de "ilegible/rara". Los números vuelven a usar los
  roles normales de `Typography` con `FontWeight.Bold` para destacarlos, como
  en el código original antes de cualquier rediseño.
- **`ComparisonScreen`: se restauró el desglose completo por tarjeta**
  (precio por rollo, por hoja, por 100 hojas). Un intento intermedio (dentro
  de esta misma corrección, antes de este commit) lo había recortado a un solo
  dato para maximizar cuántas tarjetas entran en pantalla — el usuario aclaró
  explícitamente que quería *toda* la información de vuelta, y que lo único
  que pedía era menos padding, no menos datos. Con `Spacing.md` (12dp) en vez
  de 16dp entran 3 tarjetas completas en una pantalla de celular típica sin
  perder ninguna métrica.
- **Eliminar pasa por diálogo de confirmación**, no por swipe directo: ícono
  de basura por fila + `AlertDialog` ("¿Eliminar X?" / Cancelar / Eliminar).
  El estado del diálogo vive en `ComparisonScreen` (no por fila) porque una
  fila puede salir de composición por scroll justo cuando el diálogo está
  abierto.
- **AGP 9.4.0 en vez de bajar Gradle a 9.5**: Gradle 9.6.1 es el único Gradle
  instalado y el que soporta JDK 26 (el único JDK de la máquina). AGP 8.x no
  corre sobre Gradle 9.6 (usa una API interna removida), así que se subió AGP
  en vez de arriesgar romper el soporte de JDK.
- **Sin Hilt**: app chica, DI manual vía `RolloApplication` + factory alcanza
  sin la capa extra.
- **compileSdk/targetSdk fijo en 35**: es la única platform instalada en el SDK
  local (`~/Library/Android/sdk/platforms/android-35`).

## Files Modified This Session

- Reversión (feat-010): `ui/theme/Color.kt`, `Theme.kt`, `Type.kt` reescritos
  (fuente del sistema, dynamic color, paleta teal de un solo hue);
  `Shapes.kt`, `MagnitudeScale.kt` eliminados; `res/font/*.ttf` eliminados;
  `AddEntryScreen.kt` (OutlinedTextField estándar), `EntryDetailScreen.kt`,
  `Navigation.kt` restilados a defaults de M3; `ComparisonScreen.kt`
  reescrito: desglose completo por tarjeta + ícono de eliminar con
  `AlertDialog` de confirmación (antes: solo precio/hoja + swipe directo).

## Evidence of Completion

- [x] Tests pass: `./gradlew testDebugUnitTest` → 16 tests, 0 fallos.
- [x] APK build: `./gradlew assembleDebug` → BUILD SUCCESSFUL.
- [x] Lint: `./gradlew :app:lintDebug` → 0 errores.
- [x] Instalación real: `adb install -r app-debug.apk` → Success en SM_S918U1.
- [x] Probado a mano por adb (capturas revisadas, 2026-09-13): pantalla
      "Agregar" con campos OutlinedTextField legibles; "Comparar" con 3
      tarjetas completas (desglose de 3 métricas c/u) visibles sin scroll;
      diálogo "¿Eliminar Higienol?" se abre al tocar el ícono de basura,
      "Cancelar" no borra (verificado); colores Material You normales del
      dispositivo (sin verde fósforo ni violeta). Datos de prueba limpiados
      con `pm clear` al final.
- [ ] Uso manual completo de la app (feat-007) sigue pendiente como feature
      propia: no se verificó persistencia tras cerrar/reabrir la app.

## Notes for Next Session

Arrancá con `./init.sh`. Si el usuario pide más cambios de diseño, primero
pedile una captura de pantalla concreta antes de adivinar — dos rediseños
completos ya se descartaron por falta de esa referencia visual.
