# Procedural-Environment-Simulator
<img width="1776" height="995" alt="image" src="https://github.com/user-attachments/assets/f3f2cd2c-8cb4-4117-b1fb-f2d1de0f1efe" />

A Java-based procedural environment simulator that demonstrates dynamic terrain generation, day-night cycles, and interactive environment objects. It is designed as a modular engine for experimenting with game mechanics and procedural world generation.

---

## Features

**Procedural Terrain Generation**  
- Uses Perlin noise to generate smooth and natural-looking terrain.  
- Blocks with collision physics prevent intersection and simulate solid ground.  
- Terrain can be generated dynamically over a range of coordinates.  

**Day-Night Cycle**  
- Smooth transitions between day and night using interpolated lighting.  
- Adjustable cycle duration and customizable lighting intensity.  

**Dynamic Sky and Sun**  
- Sky and Sun objects change dynamically based on the time of day.  
- Supports callbacks for updates and smooth transitions.  

**Interactive Environment Objects**  
- Trees, flora, and other objects are procedurally placed and interact with the terrain.  
- Modular system allows adding new objects easily.  

**Modular Game Engine**  
- `PepseGameManager` initializes and manages all game objects and updates.  
- Transition system for animating properties over time.  
- Object-oriented design for flexibility and extensibility.

---

## Technologies Used

- Java  
- DanoGameLab 2D Game Engine  
- Perlin noise for procedural terrain  
- OOP design patterns for modularity and extensibility  

---

## Key Classes:
- GameObject – Base class for all objects in the world.
- Block – Represents terrain blocks with collision physics.
- Terrain – Generates procedural terrain dynamically.
- Transition – Animates object properties over time.
- Sun/Night/Sky – Handles dynamic lighting and sky appearance.
- Chunk - a class responsible for creating and handling the game terrain chunks, such that only the relevant chunks are loaded at a given time.

---
