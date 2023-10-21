package dev.armenderoian.orderninja.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.armenderoian.orderninja.modal.data.User;
import io.javalin.http.Context;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Map;

import static dev.armenderoian.orderninja.util.HttpUtils.NO_AUTHORIZATION;

public interface AccountUtils {

	JWTVerifier JET_VERIFIER = JWT.require(EncodingUtils.JWTAlgorithm)
			.withIssuer("OrderNinja")
			.build();

	static String generateToken(User user) {
		return generateToken(user, 1, ChronoUnit.DAYS);
	}

	static String generateToken(User user, int expiresIn) {
		return generateToken(user, expiresIn, ChronoUnit.SECONDS);
	}

	static String generateToken(User user, int expiresIn, TemporalUnit unit) {
		return JWT.create()
				.withIssuer("OrderNinja")
				.withIssuedAt(Instant.now())
				.withExpiresAt(Instant.now().plus(expiresIn, unit))
				.withClaim("userId", user.getUserId())
				.withPayload(Map.of(
						"userId", user.getUserId(),
						"username", user.getUsername()
				))
				.sign(EncodingUtils.JWTAlgorithm);
	}

	/**
	 * Attempts to verify a user token.
	 *
	 * @param token The token to verify.
	 * @return The decoded token, or null if invalid.
	 */
	static @Nullable DecodedJWT verifyToken(String token) {
		try {
			return JET_VERIFIER.verify(token);
		} catch (JWTVerificationException e) {
			return null;
		}
	}

	/**
	 * Attempts to fetch a user by their token.
	 * This method pulls the token from a HTTP request.
	 *
	 * @param ctx The context of the request.
	 * @return The user, or null if not fount.
	 */
	static @Nullable User getUser(Context ctx) {
		// Try reading the token.
		var token = HttpUtils.getToken(ctx);
		if (token == null) {
			ctx.status(401).json(NO_AUTHORIZATION());
			return null;
		}

		// Try fetching the user with the token.
		var user = AccountUtils.getUser(token);
		if (user == null) {
			ctx.status(401).json(NO_AUTHORIZATION());
			return null;
		}

		return user;
	}

	/**
	 * Attempts to fetch a user by their token.
	 *
	 * @param token The user's token.
	 * @return The user, or null if not fount.
	 */
	static @Nullable User getUser(String token) {
		// Verify and read the token.
		var tokenInfo = verifyToken(token);
		if (tokenInfo == null) return null;

		// Fetch the user by their ID.
		var userId = tokenInfo.getClaim("userId").asString();
		return User.getUserById(userId);
	}

}
