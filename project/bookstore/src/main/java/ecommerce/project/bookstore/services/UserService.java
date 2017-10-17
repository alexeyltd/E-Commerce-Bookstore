package ecommerce.project.bookstore.services;

import ecommerce.project.bookstore.entities.User;
import ecommerce.project.bookstore.entities.security.PasswordResetToken;
import ecommerce.project.bookstore.entities.security.UserRole;

import java.util.Set;

public interface UserService {

	PasswordResetToken getPasswordResetToken(String token);

	void createPasswordResetTokenForUser(User user, String token);

	User findByUsername(String username);

	User findByEmail(String email);

	User createUser(User user, Set<UserRole> userRoles) throws Exception;

	User save(User user);
}
