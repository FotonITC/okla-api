package com.foton.okla.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foton.okla.model.Comment;
import com.foton.okla.model.Dish;
import com.foton.okla.model.OklaUser;
import com.foton.okla.repository.CommentRepository;
import com.foton.okla.repository.DishRepository;
import com.foton.okla.repository.UserRepository;
import com.foton.okla.util.JwtUtils;

@RestController
public class DishController {

	@Autowired
	private DishRepository dishRepo;

	@Autowired
	private CommentRepository commentRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private JwtUtils jwtUtils;

	@GetMapping("/api/dishes")
	public Page<Dish> getDishes(@RequestParam("page") int page) {
		Pageable pageable = PageRequest.of(page, 10);
		return dishRepo.findAll(pageable);
	}

	@GetMapping("/api/dish")
	public Optional<Dish> getDish(@RequestParam("id") long id) {
		return dishRepo.findById(id);
	}

	@PostMapping("/api/create-dish")
	public HashMap<String, Object> createDish(@RequestHeader("Authorization") String token,
			@RequestBody @Valid Dish dish, HttpServletResponse response) {
		HashMap<String, Object> resMap = new HashMap<>();
		String email = jwtUtils.getUserNameFromJwtToken(token.substring(7, token.length()));

		OklaUser user = userRepo.findByEmail(email);
		dish.setCreatorId(user.getId());
		dish.setFromDb(false);

		long dishId = dishRepo.save(dish).getId();
		resMap.put("dishId", dishId);
		return resMap;
	}

	@PostMapping("/api/update-dish")
	public HashMap<String, Object> updateDish(@RequestHeader("Authorization") String token,
			@RequestBody @Valid Dish dish, HttpServletResponse response) {
		HashMap<String, Object> resMap = new HashMap<>();

		String email = jwtUtils.getUserNameFromJwtToken(token.substring(7, token.length()));

		OklaUser user = userRepo.findByEmail(email);

		if (dish.getId() == null) {
			response.setStatus(400);
			resMap.put("error", "DishId is missing");
			return resMap;
		}
		if (!dishRepo.existsById(dish.getId())) {
			response.setStatus(404);
			resMap.put("error", "Dish not found");
			return resMap;
		}

		Dish oldDish = dishRepo.getOne(dish.getId());

		if (oldDish.getCreatorId() != user.getId()) {
			response.setStatus(401);
			resMap.put("error", "Not authorised");
			return resMap;
		}

		dish.setCreatorId(user.getId());
		dishRepo.save(dish);

		resMap.put("dishID", dish.getId());
		return resMap;
	}

	@DeleteMapping("/api/delete-dish")
	public HashMap<String, Object> deleteDish(@RequestHeader("Authorization") String token, @RequestParam long dishId,
			HttpServletResponse response) {
		HashMap<String, Object> resMap = new HashMap<>();

		String email = jwtUtils.getUserNameFromJwtToken(token.substring(7, token.length()));

		OklaUser user = userRepo.findByEmail(email);

		if (!userRepo.existsById(user.getId())) {
			response.setStatus(404);
			resMap.put("error", "User not found");
			return resMap;
		}

		if (!dishRepo.existsById(dishId)) {
			response.setStatus(400);
			resMap.put("error", "Dish not found");
			return resMap;
		}

		Dish dish = dishRepo.getOne(dishId);

		if (dish.getCreatorId() != user.getId()) {
			response.setStatus(401);
			resMap.put("error", "Not Authorized");
			return resMap;
		}

		dishRepo.deleteById(dishId);

		return resMap;
	}

	@GetMapping("/api/search/dishes")
	public Page<Dish> searchDishes(@RequestParam(value = "label", required = false) String label,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "ingredients", required = false) List<String> ingredients,
			@RequestParam("page") int page) {

		Pageable pageable = PageRequest.of(page, 10);

		if (label == null) {
			label = "";
		}

		if (type == null) {
			type = "";
		}

		if (ingredients == null) {
			ingredients = new ArrayList<String>();
		}

		return dishRepo.searchDishes(label, type, ingredients, ingredients.size(), pageable);
	}

	@GetMapping("/api/dish/comments")
	public Page<Comment> getDishComments(@RequestParam("page") int page, @RequestParam("dishId") long dishId) {

		Pageable pageable = PageRequest.of(page, 10);
		return commentRepo.findByDishId(dishId, pageable);
	}

	@GetMapping("/api/add-comment")
	public HashMap<String, Object> addDishComment(@RequestHeader("Authorization") String token,
			@RequestBody @Valid Comment comment, HttpServletResponse response) {

		HashMap<String, Object> resMap = new HashMap<>();

		String email = jwtUtils.getUserNameFromJwtToken(token.substring(7, token.length()));

		OklaUser user = userRepo.findByEmail(email);

		if (!userRepo.existsById(comment.getDishId())) {
			response.setStatus(404);
			resMap.put("error", "Dish not found");
			return resMap;
		}

		comment.setCreatorId(user.getId());

		long commentId = commentRepo.save(comment).getId();

		resMap.put("commentId", commentId);

		return resMap;
	}

	@PostMapping("/api/update-comment")
	public HashMap<String, Object> updateDishComment(@RequestHeader("Authorization") String token,
			@RequestBody @Valid Comment comment, HttpServletResponse response) {

		HashMap<String, Object> resMap = new HashMap<>();

		String email = jwtUtils.getUserNameFromJwtToken(token.substring(7, token.length()));

		OklaUser user = userRepo.findByEmail(email);
		
		if (comment.getId() == null) {
			response.setStatus(400);
			resMap.put("error", "CommentId is missing");
			return resMap;
		}
		if (!commentRepo.existsById(comment.getId())) {
			response.setStatus(404);
			resMap.put("error", "Comment not found");
			return resMap;
		}

		Comment oldComment = commentRepo.getOne(comment.getId());

		if (oldComment.getCreatorId() != user.getId()) {
			response.setStatus(401);
			resMap.put("error", "Not authorised");
			return resMap;
		}

		comment.setCreatorId(user.getId());
		commentRepo.save(comment);

		resMap.put("updated commentId", comment.getId());
		return resMap;
	}

	@DeleteMapping("/api/delete-comment")
	public HashMap<String, Object> deleteComment(@RequestHeader("Authorization") String token, @RequestParam long commentId,
			HttpServletResponse response) {
		HashMap<String, Object> resMap = new HashMap<>();

		String email = jwtUtils.getUserNameFromJwtToken(token.substring(7, token.length()));

		OklaUser user = userRepo.findByEmail(email);
		
		if (!commentRepo.existsById(commentId)) {
			response.setStatus(400);
			resMap.put("error", "Dish not found");
			return resMap;
		}

		Comment comment = commentRepo.getOne(commentId);

		if (comment.getCreatorId() != user.getId()) {
			response.setStatus(401);
			resMap.put("error", "Not Authorized");
			return resMap;
		}
		
		commentRepo.deleteById(commentId);

		return resMap;
	}
}
