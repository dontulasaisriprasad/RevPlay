package com.revplay.dao;

import com.revplay.model.User;
import com.revplay.exception.CustomException;
import java.util.List;

public interface UserDAO {
    User registerUser(User user) throws CustomException;
    User loginUser(String username, String password) throws CustomException;
    User getUserById(int userId) throws CustomException;
    User getUserByUsername(String username) throws CustomException;
    User getUserByEmail(String email) throws CustomException;
    boolean updateUser(User user) throws CustomException;
    boolean deleteUser(int userId) throws CustomException;
    boolean changePassword(int userId, String newPassword) throws CustomException;
    List<User> getAllUsers() throws CustomException;
    List<User> searchUsers(String keyword) throws CustomException;
    boolean deactivateUser(int userId) throws CustomException;
    boolean activateUser(int userId) throws CustomException;
}