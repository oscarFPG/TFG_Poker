# TFG_Poker

# Integrantes:

- Óscar Fabian Pineda German
- Carla Toapanta Taipe
- Valeria Corina Pulido Lozada

# Requisitos del proyecto
- Java SDK 21
- JavaFX 21

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
  ![diagrama1](resources/diagrama1.jpg)
  ![diagrama2](resources/diagrama2.jpg)

# 23/09/2025
- Configuración del proyecto con wrapper de Maven, Java 21, JavaFX 21 y VSCode

# 29/09/2025
- Diagrama de clase más completo (https://lucid.app/lucidchart/d3f7ede4-6c95-4eab-ac28-97cf29b15127/edit?invitationId=inv_d0d21d1d-be79-4cfb-9e71-16f0b3f42e50)
![classDiagram](resources/classDiagram.png)

# 02/10/2025
## CASOS DE USO:
- Repartir/devolver cartas: jugador y mesa
- Asignar roles
- Eliminar/añadir jugadores
- Configurar parámetros de la partida
- Jugar una mano: apostar y opciones de ronda (check, bet, call, raise, fold)
- Gestionar ganador en cada ronda
# 09/10/2025

- [ ] *Logica juego (Game)
- [ ] 1. Inicia Juego
- [ ] 2. Se asignan roles
- [ ] 3. Dealer reparte 2 cartas
- [ ] 4. Dealer reparte cartas mesa
- [ ] 5. Small_blind hace apuesta (obligatoria)
- [ ] 6. Big_blind hace apuesta (obligatoria)
- [ ] 7. Under_the_gun hace apuesta (libre) --> y resto de jugadores
- [ ] 8. Hcaer jugada (Fold, All-in, Igualar, Raise)
- [ ] 9. Comprobar bote ( si -> continuo punto 10, no -> vuelvo a punto 7)
- [ ] 10. EL FLOP ( Voltear 3 primeras cartas)
- [ ] 11. Apuestas desde el jugador situado a la izquierda del dealer
- [ ] 12. EL TURN (Voltear siguiente carta, 4)
- [ ] 13. Apuestas desde el jugador situado a la izquierda del dealer
- [ ] 14. EL RIVER (Voltear siguiente carta, 5)
- [ ] 15. Apuestas finales
- [ ] 16. ~Mostrar cartas/ o no~
- [ ] 17. Escoger Ganador --> dar lote (jugadas)
- [ ] 18. Dealer se rota -->
- [ ] 19. Inicio de nuevo segun contexto
