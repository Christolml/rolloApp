# Session Progress Log

## Current State

**Last Updated:** 2026-09-16
**Active Feature:** ninguna — feat-018 (zoom de la foto en el detalle) cerrada
y probada en las dos plataformas.

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
      version=2, Coil). Probada de punta a punta con el celular desbloqueado:
      capturar, guardar, ver en Comparar/Detalle, eliminar.
- [x] feat-017 — Foto de producto en la PWA (`<input capture>` + canvas).
- [x] feat-018 — Zoom de la foto en el detalle: tocarla la agranda (220dp/px),
      tocar afuera la vuelve a su tamaño y lugar, en Android y en la PWA.

### What's Next

1. Probar a mano la corrección de orientación EXIF con una foto real en
   horizontal (la prueba de hoy usó el lente tapado, así que no había nada
   que rotar) — bajo riesgo, la lógica es la misma que ya cubre el downsampling
   probado.
2. feat-007: verificación manual end-to-end en el dispositivo (persistencia tras
   cerrar y reabrir la app Android) — sigue pendiente.
3. Si alguna vez hace falta distribuir la app Android fuera del celular del
   usuario, evaluar un release firmado (hoy solo hay APK debug, a propósito).

## Blockers / Risks

- [x] ~~feat-016 sin probar a mano~~ — resuelto (2026-09-13): con el celular
      desbloqueado se instaló la APK con `adb install -r` sobre la app ya
      existente (con datos reales) y se probó de punta a punta: botón de
      cámara → cámara nativa de Samsung sin pedir permisos → foto capturada
      se ve como miniatura en "Agregar" (con overlay de retomar) → guardar →
      se ve en "Comparar" (40dp) y en el Detalle (64dp) → eliminar borra la
      entrada y su archivo sin crashear (confirmado por `logcat`). Único caso
      sin cubrir: la foto de prueba salió negra (lente tapado a propósito),
      así que la corrección de orientación EXIF en una foto horizontal real
      queda como próximo paso de bajo riesgo, no como bloqueo.
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
- **Zoom sin diálogo ni overlay de pantalla completa**: la imagen crece "en su
  lugar" dentro del layout existente (Android: `animateDpAsState` sobre el
  mismo `Modifier.size`; PWA: transición CSS sobre el mismo `<img>`). Se pidió
  así explícitamente ("vuelve a su tamaño y lugar original"), y evita la
  complejidad de gestionar una ventana/diálogo aparte.
- **`MiniaturaFoto` separa `onClick` de `mostrarAccionCamara`**: antes ambos
  estaban acoplados (si tenía `onClick`, mostraba el ícono de "retomar foto").
  Hacía falta desacoplarlos para poder darle `onClick` (zoom) en el detalle
  sin sugerir que ahí se puede retomar la foto.
- **Colapsar con `pointerInput`/`detectTapGestures` a nivel de pantalla
  (Android) y un listener delegado con `closest()` (PWA)**, no un diálogo con
  scrim: en Compose, un tap sobre un `clickable` hijo (la foto, botones, el
  campo de texto) se consume ahí y nunca llega al detector de la pantalla, así
  que conviven sin gestos en conflicto — confirmado a mano tipeando en el
  campo del simulador con la lógica de colapso activa.

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
- [x] feat-016: prueba manual de la cámara en el dispositivo — capturas
      revisadas en Agregar/Comparar/Detalle, más el borrado sin crash.
- [x] feat-018: probado a mano en el SM_S918U1 sobre una entrada real (papel
      MAX) y en la PWA con clicks programáticos vía Chrome headless — tocar
      la foto la agranda, tocar afuera la achica, en las dos plataformas.
- [ ] feat-007 (persistencia de la app Android tras cerrar y reabrir) pendiente.

## Notes for Next Session

Arrancá leyendo `CLAUDE.md`. Si el usuario pide cambios de diseño, pedile una
captura antes de adivinar: dos rediseños completos se descartaron por falta de
esa referencia. Para probar interacciones de la PWA sin dispositivo, el truco
que funcionó bien: un iframe + `MouseEvent` disparado por script en vez de
depender del protocolo de depuración completo (más simple de armar en bash).
