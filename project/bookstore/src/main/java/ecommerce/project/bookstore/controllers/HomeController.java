package ecommerce.project.bookstore.controllers;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import ecommerce.project.bookstore.services.impl.UserSecurityService;
import ecommerce.project.bookstore.entities.User;
import ecommerce.project.bookstore.entities.security.PasswordResetToken;
import ecommerce.project.bookstore.entities.security.Role;
import ecommerce.project.bookstore.entities.security.UserRole;
import ecommerce.project.bookstore.services.UserService;
import ecommerce.project.bookstore.utility.MailConstructor;
import ecommerce.project.bookstore.utility.SecurityUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HomeController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserSecurityService userSecurityService;

//	@Autowired
//	private JavaMailSender mailSender;

	@Autowired
	private MailConstructor mailConstructor;

	@RequestMapping("/")
	public String index() {
		return "index";
	}

	@RequestMapping("/myAccount")
	public String myAccount() {
		return "myAccount";
	}

	@RequestMapping("/login")
	public String login(Model model) {
		model.addAttribute("classActiveLogin", true);
		return "myAccount";
	}

	@RequestMapping("/forgetPassword")
	public String forgetPassword(Model model, HttpServletRequest request, @ModelAttribute("email") String userEmail) {

		model.addAttribute("classActiveForgetPassword", true);

		User user = userService.findByEmail(userEmail);

		if (user == null) {
			model.addAttribute("emailNotExist", true);
			return "myAccount";
		}

		return "myAccount";
	}

	@RequestMapping(value = "/newUser", method = RequestMethod.POST)
	public String newUserPost(HttpServletRequest request, @ModelAttribute("email") String userEmail, @ModelAttribute("username") String username, Model model) throws Exception {

		model.addAttribute("classActiveNewAccount", true);
		model.addAttribute("email", userEmail);
		model.addAttribute("username", username);

		if (userService.findByUsername(username) != null) {
			model.addAttribute("usernameExists", true);
			return "myAccount";
		}

		if (userService.findByEmail(userEmail) != null) {
			model.addAttribute("emailExists", true);
			return "myAccount";
		}

		User user = new User();
		user.setUsername(username);
		user.setEmail(userEmail);

		String password = SecurityUtility.randomPassword();

		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);

		user.setPassword(encryptedPassword);

		Role role = new Role();
		role.setRoleId(1);
		role.setName("ROLE_USER");
		Set<UserRole> userRoles = new HashSet<>();
		userRoles.add(new UserRole(user, role));
		userService.save(user);

		String token = UUID.randomUUID().toString();
		userService.createPasswordResetTokenForUser(user, token);

		String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

		SimpleMailMessage email = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user, password);

//		mailSender.send(email);

		model.addAttribute("forgetPasswordEmailSent", true);

		return "myAccount";

	}

	@RequestMapping("/newUser")
	public String newUser(Model model, Locale locale,
	                      @RequestParam("token") String token) {
		PasswordResetToken passwordResetToken = userService.getPasswordResetToken(token);

		if (passwordResetToken == null) {
			String message = "Invalid Token";
			model.addAttribute("message", message);
			return "redirect:badRequest";
		}

		User user = passwordResetToken.getUser();
		String username = user.getUsername();

		UserDetails userDetails = userSecurityService.loadUserByUsername(username);

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);

		model.addAttribute("user", user);

		model.addAttribute("classActiveEdit", true);
		return "myProfile";
	}

}
