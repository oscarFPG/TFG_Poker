# TFG_Poker
# Referencia cliente-servidor con sockets juego 4 en raya: https://github.com/oscarFPG/4EnRaya_Sockets
# Integrantes:

- Óscar Fabian Pineda German
- Carla Toapanta Taipe
- Valeria Corina Pulido Lozada

# Requisitos del proyecto
- Java SDK 21
- JavaFX 21

# Casos de uso
Modelo de repodruccion de partidas : https://www.chess.com/game/live/148153400091?username=coach_fearless_king2&move=69
Añadir bots y jugaodres se podra solo EN PREPARTIDA
Se permite jugadores y bots en todas las modalidades

## Modo casual
### No se pueden escoger las cartas de los jugadores, ni los de la mesa
### Partida de poker clasica
### Sin retroceder moviminntos
### Permita guardar el historial en cualquier momento pero no cargarlo 
  
## Modo avanzado
### No se pueden escoger las cartas de los jugadores, ni los de la mesa
### Botones de cargar una partida anterior SOLO EN PREPARTIDA
### Retroceder movimientos (parar de "reproducir automaticamente" la partida cargada, retoceder, avanzar)
### Permita guardar el historial en cualquier momento

## Modo constructor
### Si se pueden escoger las cartas de los jugadores y las de la mesa
## Botones de cargar una partida anterior SOLO EN PREPARTIDA
### Retroceder movimientos (Botones de cargar, parar, retoceder, retroceder x2, avanzar, avanzar x2)
### Permita guardar el historial en cualquier momento


# Patrones de diseño utilizados

- Singleton
- Builders /Cosntructores
- Tipos de cartas ---> abstract factory
- Comandos para el fold, check ..etc
- Observer ----> para las estadisticas
- Adapter ---> partidas json

# 18/09/2025
- Diagrama de clases general de la lógica del juego
- Creación proyecto en GitHub
- Organización de tareas en Projects
- Revisión proyectos ya realizados por Valeria y Óscar
  ![diagrama1](images/diagrama1.jpg)
  ![diagrama2](images/diagrama2.jpg)

# 23/09/2025
- Configuración del proyecto con wrapper de Maven, Java 21, JavaFX 21 y VSCode

# 29/09/2025
- Diagrama de clase más completo (https://lucid.app/lucidchart/d3f7ede4-6c95-4eab-ac28-97cf29b15127/edit?invitationId=inv_d0d21d1d-be79-4cfb-9e71-16f0b3f42e50)
![classDiagram](images/classDiagram.png)

# 02/10/2025
## CASOS DE USO:
- Repartir/devolver cartas: jugador y mesa
- Asignar roles
- Eliminar/añadir jugadores
- Configurar parámetros de la partida
- Jugar una mano: apostar y opciones de ronda (check, bet, call, raise, fold)
- Gestionar ganador en cada ronda
  
# 09/10/2025
- Lista de tareas para sprint-backlog
  
# 16/10/2025
- Bucle de jugar mano, hacer jugada, rotar, asignar roles : Pendientes de review
- Correccion de devolver cartas
  
# 22/10/2025
- Primer debug del proyecto
- Clases para mostrar el estado del juego en el debug
- Lista de comando para realizar jugada
  
# 05/11/2025
- Configuracion basica para la interfaz java fx
- Visualizar logica por consola, (comandos)
- Algoritmo evaluador jugadas


# CANVAS
https://www.canva.com/design/DAG0pwSg_LM/2e3t-G-t7Hn5cMWBzcePAw/edit?utm_content=DAG0pwSg_LM&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton


# IDEAS
- Poner botones de repodrucir / parar / continuar / volver atras / siguiente accion como si fuera un video pero interactivo.
  Las partidas pueden reproducirse (al guardar una partida y cargarla por ejemplo) incicia desde cero, cada accion es un estado de la partida, y el usuario puede decidir si continuar a las siguienteses acciones y detenerse en un momento especifico, tomar una decision y cambiar el curso de la partida o simplemente seguir las acciones hasta llegar añ final sin ningun cambio.
- Modo creador --> Será posible recerear una partida escogiendo las cartas de todos losjugadors, las cartas de la mesa y las acciones que se haran en el juego a cada momento.
  Toda partida jugada sera registrada ya sea aleatoria o simulada, siempre tendra la opcion "guardar partida" y luego "cargar partida" respectivamente. ( Y si el jugador quiere conitnuar la partida aleatoriamente a partir de un momento)
- Bot que te suguiera la siguiente accion
- Bot personalizado ( parametros que el usuario quiere que tenga el bot)
