package dev.armenderoian.orderninja;

import com.auth0.jwt.interfaces.RSAKeyProvider;
import dev.armenderoian.orderninja.util.EncodingUtils;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

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
	private EncodingKeyPairPaths keyPaths = new EncodingKeyPairPaths();

	private EncodingKeyProvider keyProvider = new EncodingKeyProvider();

	@Data
	private static class EncodingKeyPairPaths {
		private String publicKey;
		private String privateKey;
	}

	private static class EncodingKeyProvider implements RSAKeyProvider {

		private RSAPublicKey publicKey;
		private RSAPrivateKey privateKey;

		@Override
		public RSAPublicKey getPublicKeyById(String keyId) {
			if (publicKey == null) {
				try {
					publicKey = EncodingUtils.readRSAPublicKey(Path.of(Config.get().getKeyPaths().getPublicKey()));
				} catch (Exception e) {
					OrderNinja.getLogger().error("Failed to load public key.", e);
				}
			}

			return publicKey;
		}

		@Override
		public RSAPrivateKey getPrivateKey() {
			if (privateKey == null) {
				try {
					privateKey = EncodingUtils.readRSAPrivateKey(Path.of(Config.get().getKeyPaths().getPrivateKey()));
				} catch (Exception e) {
					OrderNinja.getLogger().error("Failed to load private key.", e);
				}
			}

			return privateKey;
		}

		@Override
		public String getPrivateKeyId() {
			return null;
		}
	}
}