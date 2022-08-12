package com.foton.okla.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.foton.okla.model.OklaUser;
import com.foton.okla.model.PasswordResetToken;
import com.foton.okla.repository.PasswordTokenRepository;
import com.foton.okla.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	PasswordTokenRepository passwordTokenRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	public void populateUser(OklaUser oldUser, OklaUser newUser){
		oldUser.setFirstName(newUser.getFirstName());
		oldUser.setLastName(newUser.getLastName());
		oldUser.setPhone(newUser.getPhone());
		oldUser.setImage(newUser.getImage());
		oldUser.setPassword(newUser.getPassword());
		oldUser.setGender(newUser.getGender());
		oldUser.setEmail(newUser.getEmail());
	}
	
	public void deleteUser(OklaUser user) {
		 PasswordResetToken token = passwordTokenRepo.findByUser(user);
		 passwordTokenRepo.delete(token);
		 userRepo.delete(user);
	}
	
	public void createPasswordResetTokenForUser(OklaUser user, String token) {
	    PasswordResetToken myToken = new PasswordResetToken(token, user);
	    passwordTokenRepo.save(myToken);
	}

	public void changeUserPassword(OklaUser user, String password) {
	    user.setPassword(passwordEncoder.encode(password));
	    userRepo.save(user);
	}
	
	public OklaUser getUserByPasswordResetToken(String token){
		return passwordTokenRepo.findByToken(token).getUser();
	}
}
