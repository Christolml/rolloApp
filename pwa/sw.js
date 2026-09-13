// Service worker: cachea el app shell para que la app abra sin conexión.
// Al cambiar cualquier archivo del shell, subir CACHE para que se reinstale.

const CACHE = 'rolloapp-v1';

const SHELL = [
  './',
  './index.html',
  './manifest.webmanifest',
  './css/styles.css',
  './js/app.js',
  './js/calculator.js',
  './js/format.js',
  './js/storage.js',
  './icons/icon.svg',
  './icons/icon-192.png',
  './icons/icon-512.png',
  './fonts/poppins_regular.ttf',
  './fonts/poppins_medium.ttf',
  './fonts/poppins_semibold.ttf',
  './fonts/poppins_bold.ttf',
];

self.addEventListener('install', (evento) => {
  evento.waitUntil(
    caches.open(CACHE).then((cache) => cache.addAll(SHELL)).then(() => self.skipWaiting()),
  );
});

self.addEventListener('activate', (evento) => {
  evento.waitUntil(
    caches
      .keys()
      .then((claves) =>
        Promise.all(claves.filter((clave) => clave !== CACHE).map((clave) => caches.delete(clave))),
      )
      .then(() => self.clients.claim()),
  );
});

self.addEventListener('fetch', (evento) => {
  const peticion = evento.request;
  if (peticion.method !== 'GET') return;

  evento.respondWith(
    caches.match(peticion).then((enCache) => {
      if (enCache) return enCache;
      return fetch(peticion)
        .then((respuesta) => {
          // Guarda lo que se vaya pidiendo (mismo origen) para la próxima visita.
          if (respuesta.ok && new URL(peticion.url).origin === location.origin) {
            const copia = respuesta.clone();
            caches.open(CACHE).then((cache) => cache.put(peticion, copia));
          }
          return respuesta;
        })
        .catch(() => {
          // Sin red: para una navegación, devolver el shell.
          if (peticion.mode === 'navigate') return caches.match('./index.html');
          return Response.error();
        });
    }),
  );
});
