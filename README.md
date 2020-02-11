# Gomoko AI
```
An Artifical Intelligence that plays Gomoko

Based on the minimax with alpha-beta pruning algorithm.
```

## Getting Started
```
As simple as clone onto your local machine then run GomokuReferee class in any Java IDE.
```

## Algorithms

The Minimax with alpha-beta pruning algorithm allows the evaluation of a board and how good it is for the current player to get there, on the assumption the opponent plays optimally.
By using alpha-beta pruning I can make the algorithm much more efficient. Alpha-beta pruning tries to decrease the number of boards that are evaluated by the minimax algorithm, it does this by passing two values as parameters to the minimax algorithm to determine if that area of enquiry is worth continuing down.
To increase the efficiency of the minimax algorithm I converted all the rows, columns and diagonals into strings to speed up the runtime, compared to going through the board each time to score.

## Strategy

The strategy is inside the evaluation function. This function scores the board based on how good or bad the board is.
To determine how good or bad the board is I used a weighted distribution. The evaluation function looks at each string and scores it based on how many strategic combinations that string can have with five in a row being the greatest. The least threatening combinations e.g. “b_w_b” will have the least value.
To increase the performance of the evaluation function I have included spaces in the string by using “_” this allows the function to score streaks with empty spaces either/both sides, for example “bb_bbb”.

This strategy works very well but during some board states it can run inefficiently by looking at positions that humans can see are not worth it. To counteract this I created an array holding the empty positions near previously placed pieces, because this is most likely to be a highly valuable combination.

### Prerequistes

```
Java IDE e.g. Netbeans, Eclipse or IntelliJ
```

### Installing
```
Download this repo to your local computer and load up using the Java IDE.
```

## Contributing
Feel free to contribute by submitting pull requests.

## License
This project is licensed under the MIT License - Read the LICENSE.md file
