// Equivalente web de PaperRepository + Room: el historial vive en localStorage,
// en el navegador de este dispositivo. No se comparte con la app Android.

import { calcular } from './calculator.js';

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

export function guardar({
  marca,
  precio,
  rollosPorPaquete,
  hojasPorRollo,
  esSimulado = false,
  foto = null,
}) {
  const paquetes = leerCrudo();
  paquetes.push({
    id: crypto.randomUUID(),
    marca: marca.trim() || 'Sin marca',
    precio,
    rollosPorPaquete,
    hojasPorRollo,
    fecha: Date.now(),
    esSimulado,
    foto,
  });
  return escribir(paquetes);
}

export function eliminar(id) {
  return escribir(leerCrudo().filter((paquete) => paquete.id !== id));
}
