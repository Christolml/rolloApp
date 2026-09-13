// Port 1:1 de android/app/src/main/java/com/rolloapp/app/domain/PaperPriceCalculator.kt
// Cualquier cambio de fórmula acá tiene que hacerse también allá, y viceversa:
// las dos versiones de la app tienen que dar exactamente el mismo número.

/** `precio / rollosPorPaquete` */
export function precioPorRollo(precio, rollosPorPaquete) {
  return precio / rollosPorPaquete;
}

/** `precioPorRollo / hojasPorRollo` */
export function precioPorHoja(precioRollo, hojasPorRollo) {
  return precioRollo / hojasPorRollo;
}

/** `precioPorHoja * 100` */
export function precioPor100Hojas(precioHoja) {
  return precioHoja * 100;
}

/** Indica si los datos de un paquete permiten calcular precios unitarios. */
export function esEntradaValida(precio, rollosPorPaquete, hojasPorRollo) {
  return (
    Number.isFinite(precio) &&
    precio >= 0 &&
    Number.isInteger(rollosPorPaquete) &&
    rollosPorPaquete > 0 &&
    Number.isInteger(hojasPorRollo) &&
    hojasPorRollo > 0
  );
}

/**
 * Desglose completo de un paquete.
 * Devuelve `null` si alguno de los datos no es utilizable.
 */
export function calcular(precio, rollosPorPaquete, hojasPorRollo) {
  if (!esEntradaValida(precio, rollosPorPaquete, hojasPorRollo)) return null;

  const porRollo = precioPorRollo(precio, rollosPorPaquete);
  const porHoja = precioPorHoja(porRollo, hojasPorRollo);
  return {
    precioPorRollo: porRollo,
    precioPorHoja: porHoja,
    precioPor100Hojas: precioPor100Hojas(porHoja),
  };
}

/**
 * Cuánto costaría un rollo —y el paquete completo— si el mismo producto
 * trajera `hojasHipoteticas` hojas por rollo, manteniendo el precio por hoja.
 *
 * Devuelve `null` si los datos no son utilizables.
 */
export function simular(precioHoja, hojasHipoteticas, rollosPorPaquete) {
  if (!Number.isFinite(precioHoja) || precioHoja < 0) return null;
  if (!Number.isInteger(hojasHipoteticas) || hojasHipoteticas <= 0) return null;
  if (!Number.isInteger(rollosPorPaquete) || rollosPorPaquete <= 0) return null;

  const porRolloSimulado = precioHoja * hojasHipoteticas;
  return {
    hojasHipoteticas,
    precioPorRolloSimulado: porRolloSimulado,
    precioPaqueteSimulado: porRolloSimulado * rollosPorPaquete,
  };
}
