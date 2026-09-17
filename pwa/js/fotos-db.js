// Almacenamiento de fotos en IndexedDB: a diferencia de localStorage (techo de
// ~5-10 MB para TODA la app), acá la cuota es muchísimo mayor y se guardan
// Blobs binarios directo, sin la inflación ~37% de codificarlos en base64.
// Cada foto se guarda con el mismo `id` que su paquete en storage.js.

const DB_NOMBRE = 'rolloapp-fotos';
const ALMACEN = 'fotos';

function abrirDb() {
  return new Promise((resolve, reject) => {
    const peticion = indexedDB.open(DB_NOMBRE, 1);
    peticion.onupgradeneeded = () => {
      peticion.result.createObjectStore(ALMACEN);
    };
    peticion.onsuccess = () => resolve(peticion.result);
    peticion.onerror = () => reject(peticion.error);
  });
}

export async function guardarFoto(id, blob) {
  const db = await abrirDb();
  return new Promise((resolve, reject) => {
    const tx = db.transaction(ALMACEN, 'readwrite');
    tx.objectStore(ALMACEN).put(blob, id);
    tx.oncomplete = () => resolve();
    tx.onerror = () => reject(tx.error);
  });
}

export async function leerFoto(id) {
  const db = await abrirDb();
  return new Promise((resolve, reject) => {
    const peticion = db.transaction(ALMACEN, 'readonly').objectStore(ALMACEN).get(id);
    peticion.onsuccess = () => resolve(peticion.result ?? null);
    peticion.onerror = () => reject(peticion.error);
  });
}

export async function borrarFoto(id) {
  const db = await abrirDb();
  return new Promise((resolve, reject) => {
    const tx = db.transaction(ALMACEN, 'readwrite');
    tx.objectStore(ALMACEN).delete(id);
    tx.oncomplete = () => resolve();
    tx.onerror = () => reject(tx.error);
  });
}
