package dev.armenderoian.route;

import dev.armenderoian.OrderNinja;
import io.javalin.Javalin;
import io.javalin.http.Context;

public interface SiteRouter {

	/**
	 * Configures the router with the Javalin instance.
	 *
	 * @param javalin The Javalin instance.
	 */
	static void configure(Javalin javalin) {

	}

	/**
	 * Redirect the user to the main site.
	 *
	 * @param ctx The context of the web request.
	 */
	static void redirect(Context ctx) {
		ctx.redirect("https://orderninja.armenderoian.dev/");
	}

	/**
	 * Handles Javalin/HTTP exceptions.
	 *
	 * @param exception The exception.
	 * @param ctx       The Javalin instance.
	 */
	static void handleException(Exception exception, Context ctx) {
		ctx.status(500).result("Internal Server Error");
		OrderNinja.getLogger().warn("An exception occurred while handling a request.", exception);
	}
}