package dev.armenderoian;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.armenderoian.modal.Modals;
import dev.armenderoian.route.SiteRouter;
import dev.armenderoian.util.Command;
import dev.armenderoian.util.EncodingUtils;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.json.JavalinGson;
import io.javalin.plugin.bundled.CorsPluginConfig;
import lombok.Getter;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOError;
import java.util.Arrays;

public class OrderNinja {

	private static final long startTime = System.currentTimeMillis();
	private static boolean started = false;

	// Configure logging
	static {
		System.setProperty("logback.configurationFile", "logback.xml");
	}

	@Getter
	private static final Logger logger = LoggerFactory.getLogger("OrderNinja Backend");
	@Getter
	private static final Javalin javalin = Javalin.create(OrderNinja::configureJavalin); //TODO: Configure Javalin

	private static LineReader lineReader = null;

	@Getter
	private static MongoClient mongoClient;
	@Getter
	private static Datastore datastore;


	public static void main(String[] args) {
		try {
			{
				// Load process configuration
				Config.load();
				logger.info("Configuration loaded.");
			}

			{
				// Start the console.
				OrderNinja.getConsole();
				new Thread(OrderNinja::startConsole).start();

				logger.info("Console started.");
			}

			{
				// Create a MongoDB client.
				var mongoClient = OrderNinja.mongoClient =
						MongoClients.create(Config.get().getMongoUri());

				// Create mapper options.
				var options = MapperOptions.builder()
						.mapSubPackages(true)
						.storeEmpties(true)
						.storeNulls(true)
						.build();

				var datastore = OrderNinja.datastore =
						Morphia.createDatastore(mongoClient, "order_ninja", options);
				// Configure the mapper.
				datastore.getMapper().map(Modals.MODALS);
				// Ensure indexes.
				datastore.ensureIndexes();

				logger.info("Connected to database.");
			}

			{
				var javalin = OrderNinja.getJavalin();

				// Set the exception handler.
				javalin.exception(Exception.class, SiteRouter::handleException);

				// Configure routers
				SiteRouter.configure(javalin);

				// Start the server.
				javalin.start(Config.get().getPort());
				logger.info("Javalin started on 0.0.0.0:{}", Config.get().getPort());
			}

			// Log startup time.
			logger.info("Startup took {}ms.", System.currentTimeMillis() - OrderNinja.startTime);
			OrderNinja.started = true;
		} catch (Exception e) {
			logger.error("An error occurred while starting OrderNinja.", e);
		}
	}

	/**
	 * Configures the Javalin instance.
	 */
	private static void configureJavalin(JavalinConfig config) {
		// Configure the JSON mapper.
		config.jsonMapper(new JavalinGson(EncodingUtils.GSON));

		// Configure CORS.
		config.plugins.enableCors(container ->
				container.add(CorsPluginConfig::anyHost));
	}

	/**
	 * @return The terminal line reader.
	 * Creates a new line reader if not already created.
	 */
	public static LineReader getConsole() {
		// Check if the line reader exists.
		if (OrderNinja.lineReader == null) {
			Terminal terminal = null;
			try {
				// Create a native terminal.
				terminal = TerminalBuilder.builder()
						.jna(true).build();
			} catch (Exception ignored) {
				try {
					// Fallback to a dumb JLine terminal.
					terminal = TerminalBuilder.builder()
							.dumb(true).build();
				} catch (Exception ignored1) {
				}
			}

			// Set the line reader.
			OrderNinja.lineReader = LineReaderBuilder.builder()
					.terminal(terminal).build();
		}

		return OrderNinja.lineReader;
	}

	/**
	 * Starts the line reader.
	 */
	public static void startConsole() {
		String input = null;
		var isLastInterrupted = false;
		var logger = OrderNinja.getLogger();

		while (true) {
			try {
				input = OrderNinja.lineReader.readLine("> ");
			} catch (UserInterruptException ignored) {
				if (!isLastInterrupted) {
					isLastInterrupted = true;
					logger.info("Press Ctrl-C again to shutdown.");
					continue;
				} else {
					Runtime.getRuntime().exit(0);
				}
			} catch (EndOfFileException ignored) {
				continue;
			} catch (IOError e) {
				logger.error("An IO error occurred while trying to read from console.", e);
				return;
			}

			isLastInterrupted = false;

			try {
				// Parse the input.
				var split = input.split(" ");
				var label = split[0].trim().toLowerCase();
				var arguments = Arrays.copyOfRange(split, 1, split.length);

				// Invoke the command.
				Command.execute(label, Arrays.asList(arguments));
			} catch (Exception e) {
				logger.warn("An error occurred while trying to invoke command.", e);
			}
		}
	}
}