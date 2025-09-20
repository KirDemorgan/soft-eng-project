package com.bookmaker.betting.command;

// Command Pattern - обработка ставок как команд
public interface BetCommand {
    void execute();
    void undo();
}