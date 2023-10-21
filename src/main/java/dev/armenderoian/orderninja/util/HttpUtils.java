package dev.armenderoian.orderninja.util;

import com.google.gson.JsonObject;
import io.javalin.http.Context;
import org.jetbrains.annotations.Nullable;

public interface HttpUtils {

	static @Nullable RequestAuthorization getAuthorization(Context ctx) {
		var auth = ctx.header("Authorization");
		if (auth == null) {
			ctx.status(401).json(NO_AUTHORIZATION());
			return null;
		}

		var split = auth.split(" ");
		if (split.length != 2) {
			ctx.status(401).json(INVALID_TOKEN());
			return null;
		}

		var type = AuthorizationType.fromString(split[0]);
		if (type == null) {
			ctx.status(401).json(INVALID_TOKEN());
			return null;
		}

		return new RequestAuthorization(type, split[1]);
	}

	/**
	 * Attempts to pull the authorization token from the request.
	 *
	 * @param ctx The context of the request.
	 * @return The token, or null if not found.
	 */
	static @Nullable String getToken(Context ctx) {
		return getToken(getAuthorization(ctx));
	}

	static @Nullable String getToken(RequestAuthorization authorization) {
		if (authorization == null) return null;
		if (authorization.type() != AuthorizationType.BEARER) return null;

		var payload = authorization.payload();
		return payload.isEmpty() ? null : payload;
	}

	/**
	 * @return A 200 success.
	 */
	static JsonObject SUCCESS() {
		return JObject.c()
				.add("timestamp", System.currentTimeMillis())
				.add("code", 200)
				.add("message", "Success.")
				.gson();
	}

	/**
	 * @return A 200 success.
	 */
	static JsonObject SUCCESS(JsonObject data) {
		return JObject.c()
				.add("timestamp", System.currentTimeMillis())
				.add("code", 200)
				.add("message", "Success.")
				.addAll(data)
				.gson();
	}

	/**
	 * @return A 404 error.
	 */
	static JsonObject NO_RESULTS() {
		return JObject.c()
				.add("timestamp", System.currentTimeMillis())
				.add("code", 404)
				.add("message", "No results were found.")
				.gson();
	}

	/**
	 * @return A 400 error.
	 */
	static JsonObject INVALID_ARGUMENTS() {
		return JObject.c()
				.add("timestamp", System.currentTimeMillis())
				.add("code", 400)
				.add("message", "Invalid arguments were provided.")
				.gson();
	}

	/**
	 * @param reason The reason for the error.
	 * @return A 400 error.
	 */
	static JsonObject INVALID_ARGUMENTS(String reason) {
		return JObject.c()
				.add("timestamp", System.currentTimeMillis())
				.add("code", 400)
				.add("message", "Invalid arguments were provided.")
				.add("reason", reason)
				.gson();
	}

	/**
	 * @return A 400 error.
	 */
	static JsonObject INVALID_TOKEN() {
		return JObject.c()
				.add("timestamp", System.currentTimeMillis())
				.add("code", 400)
				.add("message", "Invalid token provided.")
				.gson();
	}

	/**
	 * @return A 401 error.
	 */
	static JsonObject NO_AUTHORIZATION() {
		return JObject.c()
				.add("timestamp", System.currentTimeMillis())
				.add("code", 401)
				.add("message", "No authorization provided.")
				.gson();
	}

	/**
	 * @return A 500 error.
	 */
	static JsonObject INTERNAL_ERROR() {
		return JObject.c()
				.add("timestamp", System.currentTimeMillis())
				.add("code", 500)
				.add("message", "Internal server error.")
				.gson();
	}

	enum AuthorizationType {
		BASIC,
		BEARER,
		UNSUPPORTED;

		public static AuthorizationType fromString(String string) {
			if (string == null) return null;
			return switch (string.toLowerCase()) {
				case "basic" -> BASIC;
				case "bearer" -> BEARER;
				default -> UNSUPPORTED;
			};
		}
	}

	record RequestAuthorization(AuthorizationType type, String payload) {
		@Override
		public String payload() {
			if (type == AuthorizationType.BASIC) {
				return new String(EncodingUtils.base64Decode(payload));
			} else {
				return payload;
			}
		}
	}
}
