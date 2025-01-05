package com.backend.puntoxpress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class PuntoxpressApplication {

	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hashedPassword = "$2a$04$AWBR8hIv.PpmHW1u7drRjuSvV2wcNLV2jLb7FGZSyizoWRn3uqdaO";
		String plainPassword = "tron123"; // Replace with the plain password you're checking.

		boolean isMatch = encoder.matches(plainPassword, hashedPassword);
		System.out.println("Password match: " + isMatch);

		SpringApplication.run(PuntoxpressApplication.class, args);
	}

}
