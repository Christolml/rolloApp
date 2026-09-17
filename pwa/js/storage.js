// Equivalente web de PaperRepository + Room: los datos del paquete viven en
// localStorage, en el navegador de este dispositivo. No se comparte con la
// app Android. Las fotos viven aparte, en IndexedDB (ver fotos-db.js): en
// localStorage solo queda el flag `tieneFoto`.

import { calcular } from './calculator.js';
import { borrarFoto, guardarFoto } from './fotos-db.js';

const CLAVE = 'rolloapp.paquetes.v1';

function leerCrudo() {
  try {
    const texto = localStorage.getItem(CLAVE);
    if (!texto) return [];
    const datos = JSON.parse(texto);
    return Array.isArray(datos) ? datos : [];
  } catch {
    // localStorage puede fallar (modo privado, permisos) o el JSON puede estar
    // corrupto: se arranca vacío antes que romper la app.
    return [];
  }
}

function escribir(paquetes) {
  try {
    localStorage.setItem(CLAVE, JSON.stringify(paquetes));
    return true;
  } catch {
    return false;
  }
}

/**
 * Historial ordenado por precio por hoja ascendente, con los precios unitarios
 * ya calculados — el mismo orden y la misma forma que expone PaperViewModel.
 * No trae la foto en sí (es un Blob en IndexedDB): trae `tieneFoto` para que
 * quien renderice sepa si hay que pedirla con `leerFoto(id)`.
 */
export function listar() {
  return leerCrudo()
    .map((paquete) => {
      const desglose = calcular(
        paquete.precio,
        paquete.rollosPorPaquete,
        paquete.hojasPorRollo,
      );
      if (!desglose) return null;
      return {
        ...paquete,
        ...desglose,
        totalHojas: paquete.rollosPorPaquete * paquete.hojasPorRollo,
      };
    })
    .filter(Boolean)
    .sort((a, b) => a.precioPorHoja - b.precioPorHoja);
}

export function porId(id) {
  return listar().find((entrada) => entrada.id === id) ?? null;
}

/**
 * @param {Blob|null} foto foto ya redimensionada (ver redimensionarImagen en
 *   app.js). Se guarda en IndexedDB bajo el mismo id que el paquete; cada
 *   entrada es dueña de su propia copia, así una simulación y su original se
 *   pueden borrar de forma independiente.
 */
export async function guardar({
  marca,
  precio,
  rollosPorPaquete,
  hojasPorRollo,
  esSimulado = false,
  foto = null,
}) {
  const id = crypto.randomUUID();
  const paquetes = leerCrudo();
  paquetes.push({
    id,
    marca: marca.trim() || 'Sin marca',
    precio,
    rollosPorPaquete,
    hojasPorRollo,
    fecha: Date.now(),
    esSimulado,
    tieneFoto: foto != null,
  });
  const ok = escribir(paquetes);
  if (ok && foto != null) {
    await guardarFoto(id, foto);
  }
  return ok;
}

export async function eliminar(id) {
  const ok = escribir(leerCrudo().filter((paquete) => paquete.id !== id));
  if (ok) await borrarFoto(id);
  return ok;
}

/**
 * Una sola vez, al arrancar la app: convierte al nuevo esquema los paquetes
 * guardados con la versión anterior (foto como data URL adentro del JSON de
 * localStorage), para que ninguna foto ya guardada se pierda con el cambio a
 * IndexedDB. Si no hay nada que migrar, no hace nada.
 */
export async function migrarFotosLegacy() {
  const paquetes = leerCrudo();
  let huboCambios = false;

  for (const paquete of paquetes) {
    if (typeof paquete.foto !== 'string') continue;
    try {
      const blob = await (await fetch(paquete.foto)).blob();
      await guardarFoto(paquete.id, blob);
      paquete.tieneFoto = true;
    } catch {
      // Si la migración de esta foto en particular falla, se sigue con las
      // demás en vez de bloquear el arranque de la app.
      paquete.tieneFoto = false;
    }
    delete paquete.foto;
    huboCambios = true;
  }

  if (huboCambios) escribir(paquetes);
}
