# Session Progress Log

## Current State

**Last Updated:** 2026-09-13
**Active Feature:** ninguna — feat-015 cerrada. El repo tiene las dos versiones
de la app y la PWA está publicada.

## Status

### What's Done

- [x] feat-001 a feat-006 — App Android completa: toolchain, Room, calculadora
      con tests, UI Compose, APK debug, instalado en un Samsung real.
- [x] feat-008 — Harness de documentación.
- [x] feat-009 — Rediseño "HUD de precisión": **rechazado** por el usuario.
- [x] feat-010 — Reversión a Material 3 convencional.
- [x] feat-011 — Precio por hoja con 4 decimales bajo $1 (la aritmética estaba
      bien; lo que fallaba era el formato).
- [x] feat-012 — Rediseño final sobre una imagen de referencia que dio el
      usuario (panel teal, tarjetas, chips, Poppins).
- [x] feat-013 — El proyecto Gradle pasa a `android/`.
- [x] feat-014 — Versión PWA en `pwa/`.
- [x] feat-015 — Publicada en https://christolml.github.io/rolloApp/

### What's Next

1. feat-007: verificación manual end-to-end en el dispositivo (persistencia tras
   cerrar y reabrir la app Android) — sigue pendiente.
2. Si alguna vez hace falta distribuir la app Android fuera del celular del
   usuario, evaluar un release firmado (hoy solo hay APK debug, a propósito).

## Blockers / Risks

- [ ] **Las dos versiones pueden desviarse.** La aritmética vive duplicada en
      Kotlin y en JS. Cada una tiene sus tests con los mismos casos, pero nada
      obliga a tocar las dos: es responsabilidad de quien edite.
- [ ] No hay emulador ni Android Studio: toda verificación de UI de Android
      depende de un celular físico conectado por adb.
- [ ] `local.properties` no está en git; `android/init.sh` lo recrea solo.

## Decisions Made

- **Repo público** `Christolml/rolloApp`: GitHub Pages gratis no publica desde
  repos privados. Por eso `.claude/`, `.agents/` y `skills-lock.json` se
  agregaron al `.gitignore` antes del primer push.
- **PWA sin framework ni build step**: la app es chica y así el deploy es
  copiar archivos; no hay `node_modules` ni build que se pueda romper.
- **Rutas relativas en toda la PWA**: el sitio se sirve bajo `/rolloApp/`, no
  en la raíz del dominio, así que cualquier ruta absoluta rompería en
  producción.
- **El workflow publica solo `pwa/`** y corre antes los tests del port: si el
  port se desvía de la versión Kotlin, no se despliega.
- **Los datos no se comparten** entre las dos versiones (Room vs. localStorage).
  Se descartó exportar/importar JSON: el usuario lo pidió explícitamente así.
- **Poppins empaquetada también en la PWA** (628 KB): que la app se vea igual
  sin conexión vale ese peso; es lo único pesado del bundle.

## Evidence of Completion

- [x] Android: `cd android && ./gradlew testDebugUnitTest assembleDebug` →
      20 tests verdes y APK generado, después del movimiento de carpeta.
- [x] PWA: `node --test pwa/js/calculator.test.mjs` → 10 tests verdes.
- [x] PWA renderizada y revisada con capturas (Chrome headless) en las tres
      pantallas, con datos sembrados: mismos números que la app Android.
- [x] Sitio publicado respondiendo 200 en todas sus piezas (HTML, manifest, JS,
      CSS, íconos, fuentes).
- [ ] feat-007 (persistencia de la app Android tras cerrar y reabrir) pendiente.

## Notes for Next Session

Arrancá leyendo `CLAUDE.md`: explica la estructura de dos carpetas y la regla
importante de que la aritmética está duplicada. Si el usuario pide cambios de
diseño, pedile una captura antes de adivinar: dos rediseños completos se
descartaron por falta de esa referencia, y el tercero salió bien justamente
porque mandó una imagen.
