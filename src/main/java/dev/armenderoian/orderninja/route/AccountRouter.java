package dev.armenderoian.orderninja.route;

import com.google.gson.JsonObject;
import dev.armenderoian.orderninja.modal.data.User;
import dev.armenderoian.orderninja.util.*;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.UUID;

import static dev.armenderoian.orderninja.util.HttpUtils.*;

public class AccountRouter {

	/**
	 * Configures the router with the Javalin instance.
	 *
	 * @param javalin The Javalin instance.
	 */
	public static void configure(Javalin javalin) {
		javalin.post("/accounts/register", AccountRouter::register);
		javalin.get("/accounts/login", AccountRouter::login);
		javalin.get("/accounts/fetch", AccountRouter::fetch);
	}

	/**
	 * Attempts to register a new user.
	 *
	 * @param ctx The context of the web request.
	 */
	static void register(Context ctx) {
		try {
			var body = EncodingUtils.GSON.fromJson(ctx.body(), JsonObject.class);
			String username = body.get("username").getAsString();
			String password = body.get("password").getAsString();

			if (DatabaseUtils.find(User.class, "username", username)) {
				ctx.status(400).result("Username already exists.");
				return;
			}

			// Prepare the password for storage.
			var encodedPwd = EncodingUtils.encodePassword(password);

			// Create the user.
			User user = new User()
					.setUserId(UUID.randomUUID().toString())
					.setUsername(username)
					.setPassword(encodedPwd);

			// Save the user.
			user.save();

			// Create and return token
			var token = AccountUtils.generateToken(user);
			ctx.status(200).json(SUCCESS(JObject.c()
					.add("token", token)
					.gson()));
		} catch (NullPointerException exception) {
			ctx.status(400).json(INVALID_ARGUMENTS());
		} catch (Exception exception) {
			ctx.status(500).json(INTERNAL_ERROR());
			throw exception;
		}
	}

	/**
	 * Attempts to log in a user.
	 *
	 * @param ctx The context of the web request.
	 */
	static void login(Context ctx) {
		try {
			var auth = HttpUtils.getAuthorization(ctx);
			if (auth == null) return;

			String token = null;
			User user;
			if (auth.type() == AuthorizationType.BASIC) {
				String username = auth.payload().split(":")[0];
				String password = auth.payload().split(":")[1];

				user = DatabaseUtils.fetch(User.class, "username", username);
				if (user == null) {
					ctx.status(404).json(NO_RESULTS());
					return;
				}

				if (!EncodingUtils.comparePassword(password, user.getPassword())) {
					ctx.status(401).json(NO_AUTHORIZATION());
					return;
				}
			} else if (auth.type() == AuthorizationType.BEARER) {
				token = auth.payload();
				user = AccountUtils.getUser(token);
				if (user == null) {
					ctx.status(400).json(NO_RESULTS());
					return;
				}
			} else {
				ctx.status(403);
				return;
			}

			// Create and return token
			if (token == null) token = AccountUtils.generateToken(user);
			ctx.status(200).json(SUCCESS(JObject.c()
					.add("token", token)
					.gson()));
		} catch (NullPointerException exception) {
			ctx.status(400).json(INVALID_ARGUMENTS());
		} catch (Exception exception) {
			ctx.status(500).json(INTERNAL_ERROR());
			throw exception;
		}
	}

	/**
	 * Fetches a user by their ID or the current user.
	 *
	 * @param ctx The context of the web request.
	 */
	static void fetch(Context ctx) {
		var token = HttpUtils.getAuthorization(ctx);
		var userId = ctx.queryParam("userId");

		// Check if either a token or ID was provided.
		if (token == null && userId == null) {
			ctx.status(400).json(INVALID_ARGUMENTS("No token or ID was provided."));
			return;
		}

		// Fetch the user from the database;
		var authorizedUser = AccountUtils.getUser(token != null ? token.payload() : "");
		var requestedUser = User.getUserById(userId);
		if (authorizedUser == null && requestedUser == null) {
			ctx.status(404).json(NO_RESULTS());
			return;
		}

		// Return the user.
		var user = requestedUser != null ? requestedUser : authorizedUser;
		ctx.status(200).json(SUCCESS(JObject.c()
				.add("user", user)
				.gson()));
	}

}
