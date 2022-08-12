package com.foton.okla.controller;

import java.util.HashMap;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foton.okla.model.Dish;
import com.foton.okla.model.OklaUser;
import com.foton.okla.repository.DishRepository;
import com.foton.okla.repository.UserRepository;
import com.foton.okla.service.UserService;
import com.foton.okla.util.JwtUtils;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private DishRepository dishRepo;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtUtils jwtUtils;

	@GetMapping("/api/users")
	public Page<OklaUser> getDishes(@RequestParam("page") int page) {
		Pageable pageable = PageRequest.of(page, 10);
		return userRepo.findAll(pageable);
	}

	@GetMapping("/api/user")
	public Optional<OklaUser> getDish(@RequestParam("id") long id) {
		return userRepo.findById(id);
	}

	@GetMapping("/api/user-dishes")
	public Page<Dish> getUserDishes(@RequestParam("userId") long userId, @RequestParam("page") int page) {
		Pageable pageable = PageRequest.of(page, 10);
		return dishRepo.findByCreatorId(userId, pageable);
	}
	
	@PostMapping("/api/update-user")
	public HashMap<String, Object> updateUser(@RequestHeader("userId") long userId,
			@RequestBody @Valid OklaUser newUser, HttpServletResponse response) {
		HashMap<String, Object> resMap = new HashMap<>();

		if (!userRepo.existsById(userId)) {
			response.setStatus(404);
			resMap.put("message", "User not found");
			return resMap;
		}

		OklaUser oldUser = userRepo.getOne(userId);

		userService.populateUser(oldUser, newUser);

		userRepo.save(oldUser);

		resMap.put("userId", oldUser.getId());
		return resMap;
	}

}
