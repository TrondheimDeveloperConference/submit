package no.javazone.services;

import no.javazone.session.AuthenticatedUser;
import no.javazone.representations.EmailAddress;
import no.javazone.representations.Token;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class AuthenticationService {

	// TODO: move this to persistent storage
	private Map<Token, EmailAddress> tokens = new HashMap<>();

	public Token createTokenForEmail(EmailAddress email) {
		Token token = Token.generate();
		tokens.put(token, email);
		return token;
	}

	public Optional<AuthenticatedUser> validateToken(Token token) {
		return ofNullable(tokens.getOrDefault(token, null)).map(AuthenticatedUser::new);
	}

}
