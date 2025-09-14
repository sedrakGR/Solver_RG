1. Use user guide to operate with solver
Use mongo 3.4 version
To run mongodb with specific db (specified with folder), extract the folder, and run `mongod.exe --dbpath <pathToFolder>`, where <pathToFolder> is the path to the db folder
Java version USED 11

Running on Windows (used to work in other systems as well, but now not tested)



2. The following are levels identified for chess classifiers

1. The several levels of chess classifiers reorganized

0. level
    figure type, color, coords
1. level AR1s/CR1s
    figure, field, pawn, knight, etc.
2. simple classifiers like
    figure values, phalanx, actions like moves and attacks, lines (vertical and horizontal), doubled pawn
3. composite classifiers
    field under attack, field under attack of pawn,  etc.
4. composite
    Check, / Mate / Stalemate


Accessing a classifier will trigger logging the text in console