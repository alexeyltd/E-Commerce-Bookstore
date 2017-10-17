package ecommerce.project.bookstore;

import ecommerce.project.bookstore.entities.User;
import ecommerce.project.bookstore.entities.security.Role;
import ecommerce.project.bookstore.entities.security.UserRole;
import ecommerce.project.bookstore.services.UserService;
import ecommerce.project.bookstore.utility.SecurityUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class BookstoreApplication implements CommandLineRunner {

	@Autowired
	private UserService userService;

	@Override
	public void run(String... strings) throws Exception {
		User user = new User();
		user.setFirstName("Al");
		user.setLastName("Yes");
		user.setUsername("J");
		user.setPassword(SecurityUtility.passwordEncoder().encode("p"));
		user.setEmail("a@gmail.com");
		Set<UserRole> userRoles = new HashSet<>();
		Role role = new Role();
		role.setRoleId(1);
		role.setName("ROLE_USER");
		userRoles.add(new UserRole(user, role));

		userService.createUser(user, userRoles);
	}

	public static void main(String[] args) {
		SpringApplication.run(BookstoreApplication.class, args);
	}
}
