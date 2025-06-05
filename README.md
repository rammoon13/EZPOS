# EZPOS

**EZPOS** es una aplicacion de punto de venta (POS) diseñada para dispositivos Android. Permite gestionar pedidos, inventario y el historial de ventas desde una interfaz intuitiva y rapida.

![Logo](app/src/main/res/drawable/iconoezpos.png)

## Caracteristicas principales

- **Autenticacion**: registro e inicio de sesion mediante un sistema de usuarios almacenados localmente.
- **Base de datos SQLite**: cada usuario tiene su propia base de datos con los productos, pedidos y detalles correspondientes.
- **Navegacion inferior**: acceso directo a Pedidos, Inventario e Historial.
- **Gestor de productos**: añadir, editar o eliminar articulos con soporte para imagenes desde la galeria o la camara.
- **Creacion de pedidos**: seleccionar productos, calcular el total y registrar el pago.
- **Historial**: busqueda por cliente y filtros por fecha, ademas de opciones para deshacer entregas o borrar pedidos.
- **Exportacion/Importacion**: copia la base de datos para realizar backups o compartir los datos.
- **Tutoriales**: cada pantalla muestra una guia rapida la primera vez que se accede.
- **Easter Egg**: pulsa prolongadamente en la pantalla principal para descubrir un mensaje especial.

## Estructura del proyecto

```
app/
  src/main/java/com/example/ezpos/        Codigo fuente de la aplicacion
  src/main/res/                           Recursos (layouts, strings, iconos)
  build.gradle                            Configuracion del modulo
```

El codigo esta organizado en paquetes que separan actividades, fragmentos, modelos de datos y utilidades de base de datos. Las dependencias se gestionan mediante Gradle y estan definidas en `gradle/libs.versions.toml`.

## Requisitos

- **Java 11** (configurado en `build.gradle`).
- **Gradle 8.11.1** (wrapper incluido).
- **Android SDK**: `compileSdk 35` y `minSdk 24`.

## Compilacion rapida

1. Clona este repositorio.
2. Abre el proyecto en Android Studio o ejecuta:

```bash
./gradlew assembleDebug
```

3. Instala el APK generado en `app/build/outputs/apk/` en tu dispositivo.

## Uso basico

1. **Registro**: crea un usuario desde la pantalla de registro.
2. **Inicio de sesion**: ingresa tus credenciales para acceder al menu principal.
3. **Pedidos**: consulta pedidos pendientes o crea uno nuevo.
4. **Inventario**: administra productos y exporta/importa la base de datos si es necesario.
5. **Historial**: revisa ventas anteriores y filtra por fecha o cliente.

## Contribuciones

Las aportaciones son bienvenidas! Puedes enviar *pull requests* o crear *issues* con sugerencias y mejoras.

## Licencia

Este proyecto se distribuye sin una licencia explicita. Consulta con el autor antes de reutilizar el codigo.

