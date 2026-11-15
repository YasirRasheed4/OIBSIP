# ATM Interface (Java, Console)

Oasis Infobyte SIP - Java Development - Task 3

A console ATM simulation built with object-oriented design.

## Features
- Login with User ID + PIN, access denied after 3 wrong attempts
- Menu: Transaction History, Withdraw, Deposit, Transfer, Quit
- "Insufficient Funds" check before every withdrawal and transfer
- All transactions stored in an `ArrayList<Transaction>` and shown in history
- Input validation (non-numeric, zero/negative amounts, unknown recipient, self-transfer)

## Classes
| Class | Role |
|---|---|
| `Main` | Entry point, creates demo accounts |
| `ATM` | Console UI: login, menu, actions |
| `Bank` | Stores accounts, authenticates, performs transfers |
| `Account` | Private balance/PIN, deposit/withdraw, history list |
| `Transaction` | One logged operation (type, amount, balance after, time) |

## Run
```
cd src
javac *.java
java Main
```
Requires JDK 11+.

## Demo accounts
| User ID | PIN | Opening balance |
|---|---|---|
| user1001 | 1234 | 5000 |
| user1002 | 4321 | 2500 |
| user1003 | 1111 | 10000 |
