# TFG_Poker

# Indice
Seccion [Integrantes](#integrantes)\
Seccion [Requisitos del proyecto](#requisitos-del-proyecto)\
Seccion [Guía de instalación](#guia-instalación)\
Seccion [Modos de uso](#modos-de-uso)\
Seccion [Canvas](#canvas)\
Seccion [Ideas](#ideas)\
Seccion [Patrones de diseño](#patrones-de-diseño)\
Seccion [Diario](#diario)


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

# Modos de uso
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

# Canvas
Diagrama de clase: https://www.canva.com/design/DAG0pwSg_LM/2e3t-G-t7Hn5cMWBzcePAw/edit?utm_content=DAG0pwSg_LM&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton \
Diagrama de secuencia client-servidor: https://lucid.app/lucidchart/f9ab4fa7-5727-42d4-9d75-b75961c8ec07/edit?invitationId=inv_c586c0df-b514-4305-80bf-8c6dccd676c5

## Enlaces interfaces
### Oscar: https://docs.google.com/presentation/d/1YZSDqGb1XTa3UW3WH0HIk1tJuKWi6cKmR3BGTJ0WJ68/edit?usp=sharing
### Carla: https://www.canva.com/design/DAG4-bkXV3g/rrCtnFoGoazWZRvlErGPMg/view?utm_content=DAG4-bkXV3g&utm_campaign=designshare&utm_medium=link2&utm_source=uniquelinks&utlId=h82762ed43d
### Valeria: https://www.canva.com/design/DAG5ozfLFcY/nyCNiNNGe0UVs5OQ7FaYrg/edit

### Esquema
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


# Ideas
- Bot que te suguiera la siguiente accion
- Bot personalizado (parametros que el usuario quiere que tenga el bot)

# Patrones de diseño
- Singleton
- Builders /Constructores
- Tipos de cartas ---> abstract factory
- Comandos para el fold, check ..etc
- Observer ----> para las estadisticas
- Adapter ---> partidas json

# Diario
  ## 18/09/2025
  - Diagrama de clases general de la lógica del juego
  - Creación proyecto en GitHub
  - Organización de tareas en Projects
  - Revisión proyectos ya realizados por Valeria y Óscar
    ![diagrama1](images/diagrama1.jpg)
    ![diagrama2](images/diagrama2.jpg)

  ## 23/09/2025
  - Configuración del proyecto con wrapper de Maven, Java 21, JavaFX 21 y VSCode

  ## 29/09/2025
  - Diagrama de clase más completo (https://lucid.app/lucidchart/d3f7ede4-6c95-4eab-ac28-97cf29b15127/edit?invitationId=inv_d0d21d1d-be79-4cfb-9e71-16f0b3f42e50)
  ![classDiagram](images/classDiagram.png)

  ## 02/10/2025
  ### CASOS DE USO
  - Repartir/devolver cartas: jugador y mesa
  - Asignar roles
  - Eliminar/añadir jugadores
  - Configurar parámetros de la partida
  - Jugar una mano: apostar y opciones de ronda (check, bet, call, raise, fold)
  - Gestionar ganador en cada ronda
    
  ## 09/10/2025
  - Lista de tareas para sprint-backlog
    
  ## 16/10/2025
  - Bucle de jugar mano, hacer jugada, rotar, asignar roles : Pendientes de review
  - Correccion de devolver cartas
    
  ## 22/10/2025
  - Primer debug del proyecto
  - Clases para mostrar el estado del juego en el debug
  - Lista de comando para realizar jugada
    
  ## 05/11/2025
  - Configuracion basica para la interfaz java fx
  - Visualizar logica por consola(comandos)
  - Algoritmo evaluador jugadas