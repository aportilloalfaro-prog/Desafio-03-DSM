# Mundo Explorer

App Android (Kotlin) del **Desafío Práctico #03** de Desarrollo de Software para Móviles, Escuela de Computación, Universidad Don Bosco.

Muestra una lista de regiones y los países de cada una. Al elegir un país abre su detalle con los datos generales, sus ciudades principales con el clima actual y un mapa interactivo con un marcador por ciudad.

**Autores:** Anderson Portillo y Nahum

## Enlaces

- **Repositorio:** [github.com/aportilloalfaro-prog/Desafio-03-DSM](https://github.com/aportilloalfaro-prog/Desafio-03-DSM)
- **Descargar el APK:** [app-debug.apk (v1.0)](https://github.com/aportilloalfaro-prog/Desafio-03-DSM/releases/download/v1.0/app-debug.apk)
- **Página de la release:** [Mundo Explorer v1.0](https://github.com/aportilloalfaro-prog/Desafio-03-DSM/releases/tag/v1.0)

## Funcionalidades

1. Lista de regiones (África, América, Asia, Europa y Oceanía).
2. Países de cada región con nombre, bandera y capital.
3. Detalle del país: nombre común y oficial, capital, región, subregión, población, área, códigos ISO, monedas, idiomas y coordenadas.
4. Hasta 10 ciudades principales por país, con la capital destacada, y el clima actual de cada una (temperatura en °C y °F, condición, ícono, viento, humedad y hora de actualización).
5. Mapa interactivo con un marcador por ciudad que muestra su nombre y su clima. Al tocar una ciudad de la lista, el mapa se centra en ella.
6. Indicadores de carga y mensajes de error con botón para reintentar.

## APIs utilizadas

| API | Uso | Autenticación |
|---|---|---|
| REST Countries (v5) | Datos del país | Bearer token (encabezado `Authorization`) |
| GeoNames | Ciudades principales | Nombre de usuario |
| WeatherAPI | Clima actual de cada ciudad | API key |

## Arquitectura (MVC)

- **Modelo** (`model`): data classes de las respuestas y servicios Retrofit de las tres APIs.
- **Vista** (`view`): Activities, layouts XML y adapters del RecyclerView.
- **Controlador** (`controller`): consultan las APIs, manejan los errores y entregan los resultados a la vista.

## Tecnologías

Kotlin, Retrofit, Gson, corrutinas, Coil (imágenes) y osmdroid (mapa con OpenStreetMap).

## Manejo de errores

La app maneja credenciales inválidas, falta de conexión, límite de solicitudes, tiempo de espera agotado, país sin ciudades y ubicación no encontrada. Si falla el clima de una ciudad, el resto de la pantalla sigue funcionando.

## Configurar las credenciales

Las claves no están en el repositorio.

1. Abre `local.properties` (en la raíz del proyecto) y agrega al final las tres variables que aparecen en `local.properties.example`: