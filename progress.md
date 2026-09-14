# Session Progress Log

## Current State

**Last Updated:** 2026-09-13
**Active Feature:** ninguna cerrada del todo — feat-016 (foto Android) tiene
una verificación manual pendiente (ver Blockers). feat-017 (foto PWA) cerrada.

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
- [x] feat-016 — Foto de producto en Android (cámara opcional, Room migrado a
      version=2, Coil). Verificación automática completa; falta la manual con
      la cámara real (celular bloqueado durante la sesión).
- [x] feat-017 — Foto de producto en la PWA (`<input capture>` + canvas).

### What's Next

1. **Con el celular desbloqueado**, probar a mano la cámara: tomar una foto en
   vertical y en horizontal (corrección EXIF), cancelar una captura a mitad de
   camino, guardar un paquete sin foto, verla en "Comparar" y en el detalle, y
   guardar una simulación desde una entrada con foto (debe conservar su propia
   copia al borrar la original).
2. feat-007: verificación manual end-to-end en el dispositivo (persistencia tras
   cerrar y reabrir la app Android) — sigue pendiente.
3. Si alguna vez hace falta distribuir la app Android fuera del celular del
   usuario, evaluar un release firmado (hoy solo hay APK debug, a propósito).

## Blockers / Risks

- [ ] **feat-016 sin probar a mano**: el celular (SM_S918U1) tenía la pantalla
      con bloqueo seguro (PIN) durante toda la sesión y `adb` no puede
      desbloquearlo. Se instaló la APK nueva SOBRE la app ya existente (con
      datos reales) vía `adb install -r`, y se confirmó por `logcat` que la
      app arranca sin `FATAL EXCEPTION` ni `AndroidRuntime` — la migración de
      Room no rompió el arranque — pero el flujo de cámara en sí (tomar foto,
      cancelar, ver la miniatura) no se ejercitó todavía.
- [ ] **Las dos versiones pueden desviarse.** La aritmética vive duplicada en
      Kotlin y en JS. Cada una tiene sus tests con los mismos casos, pero nada
      obliga a tocar las dos: es responsabilidad de quien edite. La foto NO es
      lógica compartida (Room+archivos vs. localStorage+base64), eso es
      intencional, no una inconsistencia.
- [ ] No hay emulador ni Android Studio: toda verificación de UI de Android
      depende de un celular físico conectado por adb.
- [ ] `local.properties` no está en git; `android/init.sh` lo recrea solo.

## Decisions Made

- **Migración de Room real, nunca `fallbackToDestructiveMigration()`**: el
  usuario tiene datos guardados en su celular; agregar `fotoPath` sin migrar
  los habría borrado. `MIGRATION_1_2` hace `ALTER TABLE ... ADD COLUMN`.
- **Cámara vía `TakePicture()` + `FileProvider`, sin CameraX**: alcanza para
  esta app y no requiere pedir el permiso `CAMERA` (se delega a la app de
  cámara del sistema).
- **Foto en `filesDir/photos` (almacenamiento privado), no en la galería**:
  cero permisos de almacenamiento en cualquier versión de Android.
- **Downsampling a 1024px + JPEG 80 + corrección EXIF por rotación de
  píxeles**, tanto en Android (`BitmapFactory`) como en la PWA
  (`createImageBitmap` + canvas) — mismo criterio en las dos plataformas
  aunque la implementación sea distinta.
- **Simulaciones copian el archivo/string de foto, no comparten referencia**:
  así se puede borrar el original o la simulación sin romper a la otra.
- **PWA: foto como campo `foto` (data URL) dentro del mismo JSON de
  `localStorage`**, sin IndexedDB — mantiene `storage.js` síncrono. Trade-off
  documentado: cada foto pesa ~40-200 KB como string, aceptable para el uso
  personal de esta app, no para cientos de productos.
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
- **Poppins empaquetada también en la PWA** (628 KB): que la app se vea igual
  sin conexión vale ese peso; es lo único pesado del bundle.

## Evidence of Completion

- [x] Android: `cd android && ./gradlew testDebugUnitTest assembleDebug
      :app:lintDebug` → 20 tests verdes, APK generado, 0 errores de lint.
- [x] Migración de Room: `adb install -r` sobre la app ya instalada con datos
      reales → sin crash en logcat.
- [x] PWA: `node --test pwa/js/calculator.test.mjs` → 10 tests verdes.
- [x] PWA renderizada y revisada con capturas (Chrome headless) en las tres
      pantallas, con datos sembrados (incluida una entrada con foto): mismos
      números que la app Android, miniatura visible en "Comparar" y detalle.
- [x] Sitio publicado respondiendo 200 en todas sus piezas.
- [ ] feat-016: prueba manual de la cámara en el dispositivo — pendiente.
- [ ] feat-007 (persistencia de la app Android tras cerrar y reabrir) pendiente.

## Notes for Next Session

Arrancá leyendo `CLAUDE.md`. Si el celular está desbloqueado, lo primero es
terminar de probar la cámara (feat-016) antes de tocar cualquier otra cosa —
es la parte de esta sesión que quedó sin verificar a mano. Si el usuario pide
cambios de diseño, pedile una captura antes de adivinar: dos rediseños
completos se descartaron por falta de esa referencia.
