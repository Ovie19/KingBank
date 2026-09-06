package dev.fantasma.kingbank.utils;

import dev.fantasma.kingbank.dtos.request.RegisterUserRequest;
import dev.fantasma.kingbank.exception.InvalidRequestException;
import dev.fantasma.kingbank.exception.KingBankException;

public class Validator {

    private final static int MIN_USERNAME_LENGTH = 4;

    public static void validate(RegisterUserRequest request) throws KingBankException {
        if (request == null) throw new InvalidRequestException("Request cannot be null");
        validateUsername(request.getUsername());
    }

    private static void validateUsername(String username) throws  KingBankException {
        boolean usernameIsEmpty = username == null || username.isBlank();
        if (usernameIsEmpty) throw new InvalidRequestException("Username is required");

        username = username.trim();
        boolean usernameIsShort = username.length() < MIN_USERNAME_LENGTH;
        if (usernameIsShort) throw new InvalidRequestException("Username must be at least " + MIN_USERNAME_LENGTH + " characters");

        boolean usernameDoesNotStartWithLetter = !Character.isLetter(username.charAt(0));
        if (usernameDoesNotStartWithLetter) throw new InvalidRequestException("Username must start with a letter");

        boolean usernameContainsSpaces =  username.contains(" ");
        if (usernameContainsSpaces) throw new InvalidRequestException("Username cannot contain spaces");
    }
}
