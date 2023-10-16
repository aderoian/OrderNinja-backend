package dev.armenderoian.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class Command {

	private Command() {
		throw new UnsupportedOperationException("This class cannot be instantiated.");
	}

	private static final Logger logger = LoggerFactory.getLogger("Command Output");
	private static final Map<String, Consumer<List<String>>> commands = new HashMap<>();

	/**
	 * Executes a command.
	 *
	 * @param command The command to execute.
	 * @param args    The arguments to pass to the command.
	 */
	public static void execute(String command, List<String> args) {
		var consumer = Command.commands.get(command);

		if (consumer != null) {
			consumer.accept(args);
		} else {
			logger.warn("Unknown command: " + command);
		}
	}
}
