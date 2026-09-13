// Port 1:1 de android/app/src/main/java/com/rolloapp/app/ui/Formatting.kt

/**
 * Formatea un importe como moneda.
 *
 * Los precios unitarios chicos van con más decimales, no solo para que no
 * queden en "$0.00": redondear el precio por hoja a 2 decimales hace que las
 * cifras derivadas no cierren en pantalla. Con un paquete de $4500 / 16 rollos
 * / 320 hojas, el precio por hoja real es 0.87890625; mostrado como "$0.88"
 * invita a multiplicar por 100 y esperar "$88.00", pero el precio por 100 hojas
 * real es $87.89. Mostrando "$0.8789" las dos cifras cierran.
 */
export function formatoMoneda(valor) {
  if (!Number.isFinite(valor)) return '—';
  const absoluto = Math.abs(valor);
  let decimales;
  if (absoluto === 0) decimales = 2;
  else if (absoluto < 0.0001) decimales = 6;
  else if (absoluto < 1) decimales = 4;
  else decimales = 2;
  return '$' + valor.toFixed(decimales);
}

/** Siempre dos decimales: precios de paquete y de rollo. */
export function formatoMonedaCorta(valor) {
  return Number.isFinite(valor) ? '$' + valor.toFixed(2) : '—';
}

/** Acepta coma o punto como separador decimal. */
export function aNumeroONull(texto) {
  const limpio = String(texto).trim().replace(',', '.');
  if (limpio === '') return null;
  const valor = Number(limpio);
  if (!Number.isFinite(valor) || valor < 0) return null;
  return valor;
}

/** Entero positivo o `null`. */
export function aEnteroONull(texto) {
  const limpio = String(texto).trim();
  if (limpio === '' || !/^\d+$/.test(limpio)) return null;
  const valor = Number(limpio);
  return Number.isInteger(valor) ? valor : null;
}
