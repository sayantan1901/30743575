package main.java.Exceptions;

public class DatabaseOperationException extends Exception{


    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
