package com.bookmaker.user.singleton;

public class Logger {
    private static final Logger INSTANCE = new Logger();

    private Logger() {
    }

    public static Logger getInstance() {
        return INSTANCE;
    }

    public void info() {
        System.out.println("method called");
    }
}