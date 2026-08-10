# TFG_Poker

# Generar Javadoc
- Desde tfgpoker/
  - Crear Javadoc de server
    - mvn -pl server javadoc:javadoc
- Abrir el archivo de la carpeta tfgpoker/server/documentation/javadoc/apidocs/index.html


# Indice
Seccion [integrantes](#integrantes)\
Seccion [requisitos del proyecto](#requisitos-del-proyecto)\
Seccion [guía de instalación](#guia-instalación)\
Seccion [modos de uso](#modos-de-uso)\
Seccion [diagramas(HACER)](#diagramas)\
Seccion [canvas](#canvas)\
Seccion [interfaz](#interfaz)\
Seccion [ideas](#ideas)\
Seccion [patrones de diseño empleados](#patrones-de-diseño-empleados)

# Integrantes
- Óscar Fabian Pineda German
- Carla Toapanta Taipe
- Valeria Corina Pulido Lozada

# Requisitos del proyecto
- Java SDK 21
- JavaFX 21

# Guia instalación
Hacer guía de instalación y ejecución de nuestro proyecto
1. Instalar Java SDK 21
> [!IMPORTANT]
> Debe configurarse previamente la variable de entorno `JAVA_HOME`
2. Siguiente paso
3. Siguiente paso
4. Siguiente paso

# Modos de uso(Cambiar!! Centrarnos en la inclusión de bots, no los modos de juego)
- Modelo de repodruccion de partidas de Chess.com
- Ejemplo: https://www.chess.com/game/live/148153400091?move=0&username=coach_fearless_king2

  ## Modo casual
    - Se permite jugadores y/o bots
    - Solo se pueden añadir jugadores y/o bots <ins>en prepartida</ins>
    - El administrador <ins>NO PUEDE</ins> seleccionar las cartas de la mesa o de los jugadores
    - Partida de poker clásica
    - No se pueden deshacer movimientos
    - Permite guardar el historial de movimientos en cualquier momento <ins>pero no cargarlo</ins>
  
  ## Modo avanzado
    - Se permite jugadores y/o bots
    - Solo se pueden añadir jugadores y/o bots <ins>en prepartida</ins>
    - El administrador <ins>NO PUEDE</ins> seleccionar las cartas de la mesa o de los jugadores
    - El administrador puede deshacer/rehacer movimientos de cualquier jugador
    - Cargar repetición solo <int>en prepartida</ins>
    - Botones de control de repetición (cargar, parar, retoceder, retroceder x2, avanzar, avanzar x2) 
    - Permite guardar el historial de movimientos en cualquier momento
    - Al cargar una partida y "reproducirla" el jugador con turno puede realizar un movimiento distinto al de la partida reproducida y jugar una nueva "rama" de esta, a partir de la cual esta es una partida "nueva" y distinta. De la misma forma, se puede volver al ultimo punto en el que la partida estaba siendo "reproducida" y continuar con su reproducción.

  ## Modo constructor
    - Se permite jugadores y/o bots
    - Solo se pueden añadir jugadores y/o bots <ins>en prepartida</ins>
    - El administrador <ins>SÍ que puede</ins> seleccionar las cartas de la mesa o de los jugadores
    - El administrador puede deshacer/rehacer movimientos de cualquier jugador
    - Cargar repetición solo <int>en prepartida</ins>
    - Botones de control de repetición (cargar, parar, retoceder, retroceder x2, avanzar, avanzar x2) 
    - Permite guardar el historial de movimientos en cualquier momento
    - Al cargar una partida y "reproducirla" el jugador con turno puede realizar un movimiento distinto al de la partida reproducida y jugar una nueva "rama" de esta, a partir de la cual esta es una partida "nueva" y distinta. De la misma forma, se puede volver al ultimo punto en el que la partida estaba siendo "reproducida" y continuar con su reproducción.

# Diagramas
- Diagrama de clase
- Digrama de secuencia cliente-servidor
- Diagrama de componentes

# Canvas
Diagrama de clase: https://www.canva.com/design/DAG0pwSg_LM/2e3t-G-t7Hn5cMWBzcePAw/edit?utm_content=DAG0pwSg_LM&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton \
Diagrama de secuencia client-servidor: https://lucid.app/lucidchart/f9ab4fa7-5727-42d4-9d75-b75961c8ec07/edit?invitationId=inv_c586c0df-b514-4305-80bf-8c6dccd676c5

# Interfaz
## Esquema interfaces
- INTERFAZ PRINCIPAL: CARLA
- CREDITOS: CARLA
- SELECCIONAR MODO DE JUEGO: OSCAR
- SELECCIONAR PARTIDA PUBLICA/PRIVADA: VALERIA
- UNIRSE A PARTIDA: OSCAR
- CREAR PARTIDA: CARLA
- CONFIGURACION DE PARTIDA: CARLA
- PREPARTIDA(ESPERANDO A JUGADORES): OSCAR
- VISTA EN PARTIDA: OSCAR
- BOTON JUGADAS: CARLA
- HISTORIAL PARTIDAS CARGADAS: CARLA
- FORMATO/COLORES: VALERIA

## Oscar: https://docs.google.com/presentation/d/1YZSDqGb1XTa3UW3WH0HIk1tJuKWi6cKmR3BGTJ0WJ68/edit?usp=sharing
## Carla: https://www.canva.com/design/DAG4-bkXV3g/rrCtnFoGoazWZRvlErGPMg/view?utm_content=DAG4-bkXV3g&utm_campaign=designshare&utm_medium=link2&utm_source=uniquelinks&utlId=h82762ed43d
## Valeria: https://www.canva.com/design/DAG5ozfLFcY/nyCNiNNGe0UVs5OQ7FaYrg/edit

## Plantilla oficial interfaz: https://ucomplutense-my.sharepoint.com/:p:/r/personal/carlatoa_ucm_es/Documents/PLANTILLA%20POKER.pptx?d=w39d7c2dc428d45c79c11ddaaadb6830a&csf=1&web=1&e=xgrq4b

# Ideas
- Bot que te suguiera la siguiente accion
- Bot personalizado (parametros que el usuario quiere que tenga el bot)

# Patrones de diseño empleados
- Singleton -> clase global Evaluator debido a la lectura de archivos de de gran tamaño
- Command -> Comandos para las acciones del jugador(check, fold, raise y call)
- MVC(Modelo Vista-Controlador) -> Separación entre modelo(juego poker) y su representación(GUI con JavaFX)
- Cliente-Servidor -> División entre las acciones y responsabilidades de los clientes y el servidor al que se conectan
