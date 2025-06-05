# EZPOS

**EZPOS** es una aplicaci\u00f3n de punto de venta (POS) dise\u00f1ada para dispositivos Android. Permite gestionar pedidos, inventario y el historial de ventas desde una interfaz intuitiva y r\u00e1pida.

![Logo](app/src/main/res/drawable/iconoezpos.png)

## Caracter\u00edsticas principales

- **Autenticaci\u00f3n**: registro e inicio de sesi\u00f3n mediante un sistema de usuarios almacenados localmente.
- **Base de datos SQLite**: cada usuario tiene su propia base de datos con los productos, pedidos y detalles correspondientes.
- **Navegaci\u00f3n inferior**: acceso directo a Pedidos, Inventario e Historial.
- **Gestor de productos**: a\u00f1adir, editar o eliminar art\u00edculos con soporte para im\u00e1genes desde la galer\u00eda o la c\u00e1mara.
- **Creaci\u00f3n de pedidos**: seleccionar productos, calcular el total y registrar el pago.
- **Historial**: b\u00fasqueda por cliente y filtros por fecha, adem\u00e1s de opciones para deshacer entregas o borrar pedidos.
- **Exportaci\u00f3n/Importaci\u00f3n**: copia la base de datos para realizar backups o compartir los datos.
- **Tutoriales**: cada pantalla muestra una gu\u00eda r\u00e1pida la primera vez que se accede.
- **Easter Egg**: pulsa prolongadamente en la pantalla principal para descubrir un mensaje especial.

## Estructura del proyecto

```
app/
  src/main/java/com/example/ezpos/        C\u00f3digo fuente de la aplicaci\u00f3n
  src/main/res/                           Recursos (layouts, strings, iconos)
  build.gradle                            Configuraci\u00f3n del m\u00f3dulo
```

El c\u00f3digo est\u00e1 organizado en paquetes que separan actividades, fragmentos, modelos de datos y utilidades de base de datos. Las dependencias se gestionan mediante Gradle y est\u00e1n definidas en `gradle/libs.versions.toml`.

## Requisitos

- **Java 11** (configurado en `build.gradle`).
- **Gradle 8.11.1** (wrapper incluido).
- **Android SDK**: `compileSdk 35` y `minSdk 24`.

## Compilaci\u00f3n r\u00e1pida

1. Clona este repositorio.
2. Abre el proyecto en Android Studio o ejecuta:

```bash
./gradlew assembleDebug
```

3. Instala el APK generado en `app/build/outputs/apk/` en tu dispositivo.

## Uso b\u00e1sico

1. **Registro**: crea un usuario desde la pantalla de registro.
2. **Inicio de sesi\u00f3n**: ingresa tus credenciales para acceder al men\u00fa principal.
3. **Pedidos**: consulta pedidos pendientes o crea uno nuevo.
4. **Inventario**: administra productos y exporta/importa la base de datos si es necesario.
5. **Historial**: revisa ventas anteriores y filtra por fecha o cliente.

## Contribuciones

\u00a1Las aportaciones son bienvenidas! Puedes enviar *pull requests* o crear *issues* con sugerencias y mejoras.

## Licencia

Este proyecto se distribuye sin una licencia expl\u00edcita. Consulta con el autor antes de reutilizar el c\u00f3digo.

