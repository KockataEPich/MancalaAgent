# MancalaAgent

A bot who playes the game ManKalah.
The bot uses the Min-Max technique to attain the best possible move and it compliments that with Alpha-Beta pruning for faster calculations.

In order to maximise the efficiency of the processors, the bot spawns a maximum of 7 new threads for the first possible move and each thread calculates a
separate possible tree.
