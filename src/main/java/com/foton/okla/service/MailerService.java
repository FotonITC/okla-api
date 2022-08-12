package com.foton.okla.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.foton.okla.model.OklaUser;



@Service
public class MailerService {

	private JavaMailSender javaMailSender;

	@Autowired
	private TemplateEngine templateEngine;

	@Autowired
	public MailerService(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public void sendResetPasswordMail(OklaUser user, String fromMail, String token, String domain)
			throws MessagingException {

		final Context ctx = new Context();
		ctx.setVariable("OklaUser", user);
		ctx.setVariable("token", token);
		ctx.setVariable("domain", domain);
		
		final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

		message.setSubject("Rest password email from Okla");
		message.setFrom(fromMail);
		message.setTo(user.getEmail());

		final String htmlContent = templateEngine.process("/emails/password-reset.html", ctx);
		message.setText(htmlContent, true);

		javaMailSender.send(mimeMessage);

	}
	
	public void sendConfirmationMail(OklaUser user, String fromMail, String token, String domain)
			throws MessagingException {

		final Context ctx = new Context();
		ctx.setVariable("OklaUser", user);
		ctx.setVariable("token", token);
		ctx.setVariable("domain", domain);
		final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

		message.setSubject("Confirmation email from Okla");
		message.setFrom(fromMail);
		message.setTo(user.getEmail());

		final String htmlContent = templateEngine.process("/emails/email-confirmation.html", ctx);
		message.setText(htmlContent, true);

		javaMailSender.send(mimeMessage);

	}
}
