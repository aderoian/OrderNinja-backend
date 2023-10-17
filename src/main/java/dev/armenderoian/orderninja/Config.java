package dev.armenderoian.orderninja;

import dev.armenderoian.orderninja.util.EncodingUtils;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;

@Data
public final class Config {

	private static Config INSTANCE = new Config();

	public static Config get() {
		return INSTANCE;
	}

	/**
	 * Loads the configuration from a file.
	 */
	@SneakyThrows
	public static void load() {
		var configFile = new File("config.json");

		if (!configFile.exists()) {
			// Save this configuration.
			Config.save();
		} else {
			// Load the configuration.
			Config.INSTANCE = EncodingUtils.jsonDecode(
					new FileReader(configFile), Config.class);

			// Check if the configuration is null.
			if (Config.INSTANCE == null) {
				Config.INSTANCE = new Config();
			}
		}
	}

	/**
	 * Saves the configuration.
	 */
	@SneakyThrows
	public static void save() {
		var configFile = new File("config.json");

		// Save the configuration.
		var json = EncodingUtils.jsonEncode(Config.INSTANCE);
		Files.write(configFile.toPath(), json.getBytes());
	}

	private int port = 3000;
	private String mongoUri = "mongodb://localhost:27017";
}