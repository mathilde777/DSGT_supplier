package be.kuleuven.caffeinerestservice.exceptions;

public class CodeNotCorrectException extends RuntimeException{
    public CodeNotCorrectException(String code){super("Code given did not match verification code");
    }
}


