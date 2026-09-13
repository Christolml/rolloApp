// Render y navegación de la PWA. Tres vistas, como en la app Android:
// Agregar, Comparar y Detalle (con el simulador).

import { calcular, simular } from './calculator.js';
import { aEnteroONull, aNumeroONull, formatoMoneda, formatoMonedaCorta } from './format.js';
import * as almacen from './storage.js';

const vista = document.getElementById('vista');
const panelTitulo = document.getElementById('panel-titulo');
const panelCuerpo = document.getElementById('panel-cuerpo');
const btnVolver = document.getElementById('btn-volver');
const dialogo = document.getElementById('dialogo-eliminar');
const aviso = document.getElementById('aviso');

const ICONO_CARRITO =
  '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M7 18a2 2 0 100 4 2 2 0 000-4zm10 0a2 2 0 100 4 2 2 0 000-4zM7.2 14.6l.9-1.6h7.4a2 2 0 001.8-1.1l3.2-5.8-1.7-1-3.2 5.9H8.5L4.3 2H1v2h2l3.6 7.6-1.4 2.4c-.7 1.3.2 3 1.8 3h12v-2H7.4a.25.25 0 01-.2-.4z"/></svg>';
const ICONO_SIMULADO =
  '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3 17.25V21h3.75L17.8 9.9l-3.75-3.75L3 17.25zM20.7 7.04a1 1 0 000-1.41l-2.34-2.34a1 1 0 00-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/></svg>';
const ICONO_BORRAR =
  '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M6 19a2 2 0 002 2h8a2 2 0 002-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/></svg>';

function escapar(texto) {
  return String(texto).replace(
    /[&<>"']/g,
    (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[c],
  );
}

function mostrarAviso(texto) {
  aviso.textContent = texto;
  aviso.hidden = false;
  clearTimeout(mostrarAviso.id);
  mostrarAviso.id = setTimeout(() => {
    aviso.hidden = true;
  }, 2600);
}

function filaMetrica(etiqueta, valor, { destacada = false, mejor = false } = {}) {
  const clases = ['metrica'];
  if (destacada) clases.push('metrica--destacada');
  if (mejor) clases.push('metrica--mejor');
  return `<div class="${clases.join(' ')}"><span>${etiqueta}</span><strong>${valor}</strong></div>`;
}

function pedirConfirmacion(entrada, alConfirmar) {
  document.getElementById('dialogo-titulo').textContent = `¿Eliminar ${entrada.marca}?`;
  const cancelar = document.getElementById('dialogo-cancelar');
  const confirmar = document.getElementById('dialogo-confirmar');

  const cerrar = () => {
    dialogo.close();
    confirmar.onclick = null;
    cancelar.onclick = null;
  };
  cancelar.onclick = cerrar;
  confirmar.onclick = () => {
    cerrar();
    alConfirmar();
  };
  dialogo.showModal();
}

// --- Vista: Agregar -------------------------------------------------------

function vistaAgregar() {
  panelTitulo.textContent = 'Agregar paquete';
  btnVolver.hidden = true;

  vista.innerHTML = `
    <h2 class="seccion">Datos del paquete</h2>
    <form id="form-paquete" novalidate>
      <div class="tarjeta tarjeta--amplia" style="display:grid;gap:12px">
        <label class="campo"><span>Marca</span>
          <input id="marca" type="text" autocomplete="off" enterkeyhint="next" />
        </label>
        <label class="campo" id="campo-precio"><span>Precio del paquete</span>
          <input id="precio" type="text" inputmode="decimal" enterkeyhint="next" />
        </label>
        <label class="campo" id="campo-rollos"><span>Rollos por paquete</span>
          <input id="rollos" type="text" inputmode="numeric" enterkeyhint="next" />
        </label>
        <label class="campo" id="campo-hojas"><span>Hojas por rollo</span>
          <input id="hojas" type="text" inputmode="numeric" enterkeyhint="done" />
        </label>
      </div>
      <button class="boton" id="guardar" type="submit" style="margin-top:16px" disabled>
        Guardar
      </button>
    </form>
  `;

  const campos = {
    marca: document.getElementById('marca'),
    precio: document.getElementById('precio'),
    rollos: document.getElementById('rollos'),
    hojas: document.getElementById('hojas'),
  };
  const guardar = document.getElementById('guardar');

  function marcarError(idCampo, input, hayError, mensaje) {
    const contenedor = document.getElementById(idCampo);
    contenedor.classList.toggle('campo--error', hayError);
    const previo = contenedor.querySelector('.campo__error');
    if (previo) previo.remove();
    if (hayError) {
      contenedor.insertAdjacentHTML('beforeend', `<p class="campo__error">${mensaje}</p>`);
    }
    input.setAttribute('aria-invalid', String(hayError));
  }

  function refrescar() {
    const precio = aNumeroONull(campos.precio.value);
    const rollos = aEnteroONull(campos.rollos.value);
    const hojas = aEnteroONull(campos.hojas.value);

    marcarError(
      'campo-precio',
      campos.precio,
      campos.precio.value.trim() !== '' && precio === null,
      'Ingresá un número válido',
    );
    marcarError(
      'campo-rollos',
      campos.rollos,
      campos.rollos.value.trim() !== '' && (rollos === null || rollos <= 0),
      'Ingresá un número mayor a cero',
    );
    marcarError(
      'campo-hojas',
      campos.hojas,
      campos.hojas.value.trim() !== '' && (hojas === null || hojas <= 0),
      'Ingresá un número mayor a cero',
    );

    const desglose =
      precio !== null && rollos !== null && hojas !== null
        ? calcular(precio, rollos, hojas)
        : null;

    panelCuerpo.innerHTML = `
      <p class="panel__etiqueta">Precio por hoja</p>
      <p class="panel__cifra">${desglose ? formatoMoneda(desglose.precioPorHoja) : '—'}</p>
      <div class="panel__secundarias">
        <div>
          <p class="panel__etiqueta">Por rollo</p>
          <strong>${desglose ? formatoMoneda(desglose.precioPorRollo) : '—'}</strong>
        </div>
        <div>
          <p class="panel__etiqueta">Por 100 hojas</p>
          <strong>${desglose ? formatoMoneda(desglose.precioPor100Hojas) : '—'}</strong>
        </div>
      </div>
    `;
    guardar.disabled = desglose === null;
    return { precio, rollos, hojas, desglose };
  }

  Object.values(campos).forEach((input) => input.addEventListener('input', refrescar));
  refrescar();

  document.getElementById('form-paquete').addEventListener('submit', (evento) => {
    evento.preventDefault();
    const { precio, rollos, hojas, desglose } = refrescar();
    if (!desglose) return;

    almacen.guardar({
      marca: campos.marca.value,
      precio,
      rollosPorPaquete: rollos,
      hojasPorRollo: hojas,
    });
    Object.values(campos).forEach((input) => (input.value = ''));
    refrescar();
    campos.marca.focus();
    mostrarAviso('Paquete guardado');
  });
}

// --- Vista: Comparar ------------------------------------------------------

function vistaComparar() {
  panelTitulo.textContent = 'Comparar';
  btnVolver.hidden = true;

  const entradas = almacen.listar();
  const mejor = entradas[0];

  panelCuerpo.innerHTML = mejor
    ? `<p class="panel__etiqueta">Mejor precio por hoja</p>
       <p class="panel__cifra">${formatoMoneda(mejor.precioPorHoja)}</p>
       <p class="panel__marca">${escapar(mejor.marca)}</p>`
    : '<p class="panel__etiqueta">Todavía no hay paquetes para comparar</p>';

  if (entradas.length === 0) {
    vista.innerHTML = `
      <div class="vacio">
        <h2>Todavía no hay paquetes</h2>
        <p>Agregá uno desde la pestaña "Agregar" para empezar a comparar.</p>
      </div>`;
    return;
  }

  vista.innerHTML = entradas
    .map((entrada, indice) => {
      const esMejor = indice === 0;
      return `
        <article class="paquete" data-id="${entrada.id}" role="button" tabindex="0">
          <div class="paquete__cabecera">
            <div class="chip-icono">${entrada.esSimulado ? ICONO_SIMULADO : ICONO_CARRITO}</div>
            <div class="paquete__datos">
              <div class="paquete__nombre">
                <h3>${escapar(entrada.marca)}</h3>
                ${esMejor ? '<span class="badge">Mejor precio</span>' : ''}
                ${entrada.esSimulado ? '<span class="badge">Simulado</span>' : ''}
              </div>
              <p class="paquete__resumen">
                ${formatoMonedaCorta(entrada.precio)} · ${entrada.rollosPorPaquete} rollos ·
                ${entrada.hojasPorRollo} hojas c/u
              </p>
            </div>
            <button class="borrar" type="button" data-borrar="${entrada.id}"
              aria-label="Eliminar ${escapar(entrada.marca)}">${ICONO_BORRAR}</button>
          </div>
          <hr />
          ${filaMetrica('Precio por rollo', formatoMoneda(entrada.precioPorRollo))}
          ${filaMetrica('Precio por hoja', formatoMoneda(entrada.precioPorHoja), {
            destacada: true,
            mejor: esMejor,
          })}
          ${filaMetrica('Precio por 100 hojas', formatoMoneda(entrada.precioPor100Hojas))}
        </article>`;
    })
    .join('');

  vista.querySelectorAll('[data-borrar]').forEach((boton) => {
    boton.addEventListener('click', (evento) => {
      evento.stopPropagation();
      const entrada = entradas.find((item) => item.id === boton.dataset.borrar);
      pedirConfirmacion(entrada, () => {
        almacen.eliminar(entrada.id);
        vistaComparar();
        mostrarAviso(`${entrada.marca} eliminado`);
      });
    });
  });

  vista.querySelectorAll('.paquete').forEach((tarjeta) => {
    const abrir = () => {
      location.hash = `#/detalle/${tarjeta.dataset.id}`;
    };
    tarjeta.addEventListener('click', abrir);
    tarjeta.addEventListener('keydown', (evento) => {
      if (evento.key === 'Enter' || evento.key === ' ') {
        evento.preventDefault();
        abrir();
      }
    });
  });
}

// --- Vista: Detalle + simulador -------------------------------------------

function vistaDetalle(id) {
  panelTitulo.textContent = 'Detalle';
  btnVolver.hidden = false;

  const entrada = almacen.porId(id);
  if (!entrada) {
    panelCuerpo.innerHTML = '';
    vista.innerHTML = '<div class="vacio"><h2>Esta entrada ya no existe.</h2></div>';
    return;
  }

  panelCuerpo.innerHTML = `
    <h2 style="font-size:22px">${escapar(entrada.marca)}</h2>
    <p class="panel__etiqueta" style="margin-top:12px">Precio por hoja</p>
    <p class="panel__cifra">${formatoMoneda(entrada.precioPorHoja)}</p>
  `;

  vista.innerHTML = `
    <div class="tarjeta tarjeta--amplia">
      <h2 class="seccion">Paquete</h2>
      <hr />
      ${filaMetrica('Precio del paquete', formatoMonedaCorta(entrada.precio))}
      ${filaMetrica('Rollos por paquete', entrada.rollosPorPaquete)}
      ${filaMetrica('Hojas por rollo', entrada.hojasPorRollo)}
      ${filaMetrica('Hojas totales', entrada.totalHojas)}
      <h2 class="seccion" style="margin-top:12px">Precios unitarios</h2>
      <hr />
      ${filaMetrica('Precio por rollo', formatoMoneda(entrada.precioPorRollo))}
      ${filaMetrica('Precio por hoja', formatoMoneda(entrada.precioPorHoja), {
        destacada: true,
        mejor: true,
      })}
      ${filaMetrica('Precio por 100 hojas', formatoMoneda(entrada.precioPor100Hojas))}
    </div>

    <h2 class="seccion">Simulador</h2>
    <p class="ayuda">
      ¿Cuánto costaría este mismo paquete si cada rollo trajera otra cantidad de
      hojas? Sirve para compararlo de igual a igual contra otra marca.
    </p>
    <label class="campo" id="campo-hipoteticas"><span>Hojas hipotéticas por rollo</span>
      <input id="hipoteticas" type="text" inputmode="numeric" />
    </label>
    <div class="tarjeta tarjeta--amplia" id="resultado-simulacion"></div>
    <button class="boton" id="guardar-simulacion" type="button" disabled>
      Guardar simulación
    </button>
  `;

  const input = document.getElementById('hipoteticas');
  const resultado = document.getElementById('resultado-simulacion');
  const boton = document.getElementById('guardar-simulacion');
  let simulacion = null;

  function refrescar() {
    const hojas = aEnteroONull(input.value);
    simulacion = hojas !== null ? simular(entrada.precioPorHoja, hojas, entrada.rollosPorPaquete) : null;

    const hayError = input.value.trim() !== '' && simulacion === null;
    document.getElementById('campo-hipoteticas').classList.toggle('campo--error', hayError);

    resultado.innerHTML = simulacion
      ? `<h2 class="seccion">Con ${simulacion.hojasHipoteticas} hojas por rollo</h2>
         <hr />
         ${filaMetrica(
           'Precio por rollo simulado',
           formatoMoneda(simulacion.precioPorRolloSimulado),
         )}
         ${filaMetrica(
           'Precio del paquete simulado',
           formatoMonedaCorta(simulacion.precioPaqueteSimulado),
           { destacada: true, mejor: true },
         )}
         <p class="ayuda" style="margin-top:8px">
           El precio por hoja no cambia: ${formatoMoneda(entrada.precioPorHoja)} por hoja.
         </p>`
      : `<p class="ayuda">${
          hayError
            ? 'Ingresá un número mayor a cero.'
            : 'Ingresá una cantidad de hojas para ver la simulación.'
        }</p>`;
    boton.disabled = simulacion === null;
  }

  input.addEventListener('input', refrescar);
  refrescar();

  boton.addEventListener('click', () => {
    if (!simulacion) return;
    almacen.guardar({
      marca: `${entrada.marca} (${simulacion.hojasHipoteticas} hojas)`,
      precio: simulacion.precioPaqueteSimulado,
      rollosPorPaquete: entrada.rollosPorPaquete,
      hojasPorRollo: simulacion.hojasHipoteticas,
      esSimulado: true,
    });
    input.value = '';
    refrescar();
    mostrarAviso('Simulación guardada');
  });
}

// --- Ruteo ----------------------------------------------------------------

function enrutar() {
  const ruta = location.hash || '#/agregar';
  const detalle = ruta.match(/^#\/detalle\/(.+)$/);

  document.querySelectorAll('.tabs__item').forEach((tab) => {
    const activo =
      (tab.dataset.tab === 'agregar' && ruta.startsWith('#/agregar')) ||
      (tab.dataset.tab === 'comparar' && (ruta.startsWith('#/comparar') || detalle));
    if (activo) tab.setAttribute('aria-current', 'page');
    else tab.removeAttribute('aria-current');
  });

  if (detalle) vistaDetalle(detalle[1]);
  else if (ruta.startsWith('#/comparar')) vistaComparar();
  else vistaAgregar();

  window.scrollTo(0, 0);
}

btnVolver.addEventListener('click', () => {
  location.hash = '#/comparar';
});

window.addEventListener('hashchange', enrutar);
enrutar();

if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('./sw.js').catch(() => {
      // Sin service worker la app sigue funcionando, solo pierde el modo offline.
    });
  });
}
