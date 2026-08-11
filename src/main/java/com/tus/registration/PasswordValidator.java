package com.tus.registration;

public class PasswordValidator {

    public static boolean checkPasswordsMatch(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }
}