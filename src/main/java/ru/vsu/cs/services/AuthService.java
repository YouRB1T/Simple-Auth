package ru.vsu.cs.services;

public interface AuthService {
    boolean authenticate(String usernameOrEmail, String password);
}
