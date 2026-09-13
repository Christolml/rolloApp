// Los mismos casos que PaperPriceCalculatorTest y FormattingTest en Kotlin:
// si las dos versiones de la app no dan el mismo número, es un bug.
//
//   node --test pwa/js/calculator.test.mjs

import { test } from 'node:test';
import assert from 'node:assert/strict';

import { calcular, esEntradaValida, simular } from './calculator.js';
import { formatoMoneda, formatoMonedaCorta } from './format.js';

test('desglose de un paquete típico', () => {
  const d = calcular(12.0, 4, 300);
  assert.equal(d.precioPorRollo, 3.0);
  assert.equal(d.precioPorHoja, 0.01);
  assert.equal(d.precioPor100Hojas, 1.0);
});

test('desglose con decimales no exactos', () => {
  const d = calcular(5.99, 6, 250);
  assert.ok(Math.abs(d.precioPorRollo - 0.9983333) < 1e-6);
  assert.ok(Math.abs(d.precioPorHoja - 0.0039933) < 1e-6);
});

test('entradas inválidas devuelven null', () => {
  assert.equal(calcular(12.0, 0, 300), null);
  assert.equal(calcular(12.0, 4, 0), null);
  assert.equal(calcular(-1, 4, 300), null);
  assert.equal(calcular(Number.NaN, 4, 300), null);
  assert.equal(calcular(Number.POSITIVE_INFINITY, 4, 300), null);
});

test('esEntradaValida distingue datos usables de incompletos', () => {
  assert.equal(esEntradaValida(12.0, 4, 300), true);
  assert.equal(esEntradaValida(12.0, 4, 0), false);
});

test('simulación mantiene el precio por hoja', () => {
  const s = simular(0.01, 500, 4);
  assert.equal(s.precioPorRolloSimulado, 5.0);
  assert.equal(s.precioPaqueteSimulado, 20.0);
});

test('simular con las mismas hojas reproduce los valores originales', () => {
  const d = calcular(12.0, 4, 300);
  const s = simular(d.precioPorHoja, 300, 4);
  assert.ok(Math.abs(s.precioPorRolloSimulado - d.precioPorRollo) < 1e-9);
  assert.ok(Math.abs(s.precioPaqueteSimulado - 12.0) < 1e-9);
});

test('una simulación guardada conserva el precio por hoja al recalcularla', () => {
  const original = calcular(4500, 16, 320);
  const s = simular(original.precioPorHoja, 300, 16);
  const recalculado = calcular(s.precioPaqueteSimulado, 16, 300);
  assert.ok(Math.abs(recalculado.precioPorHoja - original.precioPorHoja) < 1e-12);
});

test('simulación con datos inválidos devuelve null', () => {
  assert.equal(simular(0.01, 0, 4), null);
  assert.equal(simular(0.01, 500, 0), null);
  assert.equal(simular(-1, 500, 4), null);
  assert.equal(simular(Number.NaN, 500, 4), null);
});

test('el precio por hoja cierra contra el de 100 hojas', () => {
  // El caso que originó el fix de formato: con 2 decimales mostraba "$0.88"
  // junto a "$87.89", y 0.88 x 100 = 88.00.
  const d = calcular(4500, 16, 320);
  assert.equal(formatoMoneda(d.precioPorHoja), '$0.8789');
  assert.equal(formatoMoneda(d.precioPor100Hojas), '$87.89');
});

test('formato de importes', () => {
  assert.equal(formatoMoneda(281.25), '$281.25');
  assert.equal(formatoMoneda(1.375), '$1.38');
  assert.equal(formatoMoneda(0.003993333), '$0.0040');
  assert.equal(formatoMoneda(0.00005), '$0.000050');
  assert.equal(formatoMonedaCorta(4500), '$4500.00');
  assert.equal(formatoMoneda(Number.NaN), '—');
  assert.equal(formatoMonedaCorta(Number.NaN), '—');
});
