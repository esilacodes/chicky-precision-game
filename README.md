# chicky-precision-game
A strategic arcade game built with Java Swing featuring a LIFO stomach mechanic.
🐥 Chicky Precision
Chicky Precision is a strategic arcade game developed with Java Swing. The game challenges players to reach an exact target food count within a limited time while managing a unique "Stomach" system based on real-world data structures.

🕹️ Gameplay & Mechanics
Precision Counting: Your goal is to reach a randomly generated target sum perfectly. Precision is key—not one more, not one less!

Stomach System (LIFO): Every food item you consume is stored in a java.util.Stack. This follows the Last-In-First-Out logic, meaning the last item you ate is the first one you must remove.

Vomiting Mechanic: If you exceed the target, you can use your limited "Vomit" ability (Spacebar) to pop() the last consumed item from your stack.

Lethal Predators: Incoming chickens are deadly. A single collision results in an immediate Game Over.

Dynamic Visuals: The chick turns red if you overeat. The timer font scales and turns red during the final 10 seconds to increase tension.

⌨️ Controls
Arrow Keys / WASD: Movement (Up, Down, Left, Right)

Spacebar: Vomit (Removes the last item from the Stack)

🛠️ Technical Details
This project demonstrates core Java programming and data structure concepts:

Data Structure: Uses java.util.Stack to manage the collection order and the vomiting mechanic.

Collision Detection: Utilizes Rectangle.intersects() for precise hitbox management between the player, predators, and food.

Rendering: Powered by Graphics2D and javax.swing.Timer for smooth 60 FPS animation.

🚀 How to Run
Ensure you have JDK 8 or higher installed.

Clone or download the source code.

Compile and run via terminal:

javac maingame/*.java
java maingame.MainClass
