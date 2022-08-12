package com.foton.okla.controller;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.foton.okla.model.LoginRequest;
import com.foton.okla.model.OklaUser;
import com.foton.okla.model.PasswordRest;
import com.foton.okla.model.Role;
import com.foton.okla.repository.UserRepository;
import com.foton.okla.service.MailerService;
import com.foton.okla.service.SecurityService;
import com.foton.okla.service.UserService;
import com.foton.okla.util.JwtUtils;
import com.foton.okla.util.Utils;

@Controller
public class AccountController {

	@Value("${application.domain}")
	private String domain;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private UserService userService;

	@Autowired
	MailerService mailerService;

	@Value("${spring.mail.username}")
	private String fromMail;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	AuthenticationManager authenticationManager;

	@PostMapping("/api/sign-up")
	@ResponseBody
	public HashMap<String, Object> signUp(@RequestBody @Valid OklaUser user, HttpServletResponse response)
			throws MessagingException {
		HashMap<String, Object> resMap = new HashMap<>();

		OklaUser oldUser = userRepo.findByEmail(user.getEmail());

		if (oldUser != null) {

			if (oldUser.isActivated()) {
				response.setStatus(409);
				resMap.put("message", "Email already exists");
				return resMap;
			} else {
				userService.deleteUser(oldUser);
			}

		}

		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		List<Role> roles = Arrays.asList(Role.USER);
		user.setRoles(roles);
		user.setActivated(false);

		OklaUser savedUser = userRepo.save(user);

		String token = UUID.randomUUID().toString();

		userService.createPasswordResetTokenForUser(savedUser, token);

		mailerService.sendConfirmationMail(user, fromMail, token, domain);

		resMap.put("message", "Confirmation email has been sent");
		resMap.put("userId", savedUser.getId());
		return resMap;
	}

	@GetMapping("/activate-account")
	public ModelAndView activateAccount(@RequestParam("token") String token) {
		String result = securityService.validatePasswordResetToken(token);
		ModelAndView MaV = new ModelAndView();

		if (result != null) {
			MaV.setViewName("/error/404");
			return MaV;
		}

		OklaUser user = userService.getUserByPasswordResetToken(token);

		user.setActivated(true);
		userRepo.save(user);

		MaV.setViewName("activate-account");
		return MaV;
	}

	@PostMapping("/api/sign-in")
	@ResponseBody
	public HashMap<String, Object> signIn(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		OklaUser user = userRepo.findByEmail(loginRequest.getEmail());

		HashMap<String, Object> resMap = new HashMap<>();

		if (!user.isActivated()) {
			response.setStatus(401);
			resMap.put("error", "account is not activated");
			return resMap;
		}

		String jwt = jwtUtils.generateJwtToken(authentication);

		resMap.put("jwt", jwt);
		resMap.put("jwt-type", "Bearer");
		resMap.put("id", user.getId());
		resMap.put("roles", user.getRoles());

		return resMap;
	}

	@GetMapping("/api/send-reset-password-email")
	@ResponseBody
	public HashMap<String, Object> forgetPassword(@RequestParam("email") String email, HttpServletResponse response)
			throws MessagingException {
		HashMap<String, Object> resMap = new HashMap<>();

		OklaUser user = userRepo.findByEmail(email);

		if (user == null) {
			response.setStatus(400);
			resMap.put("error", "Email does not exist");
			return resMap;
		}

		String token = UUID.randomUUID().toString();

		userService.createPasswordResetTokenForUser(user, token);

		mailerService.sendResetPasswordMail(user, fromMail, token, domain);

		resMap.put("message", "Reset password mail has been sent");
		return resMap;
	}

	@PostMapping(path = "/reset-password", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	@ResponseBody
	public ModelAndView resetPassword(@RequestBody String url,
			HttpServletResponse response) throws UnsupportedEncodingException {
		
		ModelAndView MaV = new ModelAndView();
		
		Map<String, String> params = Utils.splitQuery(url);
		
		HashMap<String, Object> resMap = new HashMap<>();

		String result = securityService.validatePasswordResetToken(params.get("token"));

		if (result != null) {
			response.setStatus(401);
			MaV.setViewName("/error/404");
			return MaV;
		}

		if (!params.get("newPassword").equals(params.get("confirmPassword"))) {
			response.setStatus(400);
			MaV.setViewName("/error/404");
			return MaV;
		}

		OklaUser user = userService.getUserByPasswordResetToken(params.get("token"));
		if (user == null) {
			response.setStatus(400);
			MaV.setViewName("/error/404");
			return MaV;
		}

		userService.changeUserPassword(user, params.get("newPassword"));
		resMap.put("message", "Password got changed");
		MaV.setViewName("/reset-password-complete");
		return MaV;
	}

	@GetMapping("/reset-password")
	public ModelAndView resetPasswordPage(@RequestParam("token") String token) {
		String result = securityService.validatePasswordResetToken(token);
		ModelAndView MaV = new ModelAndView();
		if (result != null) {
			MaV.setViewName("/error/404");
			return MaV;
		}

		MaV.setViewName("reset-password");
		MaV.addObject("token", token);
		return MaV;
	}
}
