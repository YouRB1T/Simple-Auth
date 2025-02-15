package ru.vsu.cs.services;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.vsu.cs.daos.RoleDAO;
import ru.vsu.cs.daos.UserDAO;
import ru.vsu.cs.services.daos.UserServiceDAO;

@Getter
@NoArgsConstructor
public class UserService {
    private final UserDAO userDAO = new UserDAO();
    private final UserServiceDAO userServiceDAO = new UserServiceDAO();


}
