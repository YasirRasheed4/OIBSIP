# Number Guessing Game (Java, Console)

Oasis Infobyte SIP - Java Development - Task 2

## Features
- Random number generated with `java.util.Random` each round
- Hints: `Too High!`, `Too Low!`, `Correct!`
- Attempt counter shown on every prompt ("Attempt 3 of 7")
- Attempt limit: `You Lost!` message reveals the number
- Play Again (yes/no) after each round
- Score summary per round: "Round X - guessed in Y attempts"
- **Bonus:** difficulty levels - Easy (1-50, 10 attempts), Medium (1-100, 7), Hard (1-200, 5)
- Invalid or out-of-range input does not use up an attempt

## Run
```
cd src
javac NumberGuessingGame.java
java NumberGuessingGame
```
Requires JDK 11+.
