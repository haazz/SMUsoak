package com.smusoak.restapi.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

	private final JavaMailSender mailSender;
	
	public void sendMail(String toMail, String title, String text) {
		SimpleMailMessage mailForm = createMailForm(toMail, title, text);
		try {
			mailSender.send(mailForm);
		} catch (RuntimeException e) {
			log.debug("MailService.sendEmail exception occur toEmail: {}, " +
					"title: {}, text: {}", toMail, title, text);
		}
	}

	// 메일 데이터 세팅
    private SimpleMailMessage createMailForm(String toEmail, String title, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail);
		message.setSubject(title);
		message.setText(text);

		return message;
    }
}
