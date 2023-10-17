package dev.armenderoian.orderninja.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Reader;
import java.net.URL;
import java.util.Base64;

public interface EncodingUtils {
	Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.disableHtmlEscaping()
			.serializeNulls()
			.create();

	/**
	 * Converts an object to JSON.
	 *
	 * @param object The object to convert.
	 * @return The JSON.
	 */
	static JsonElement toJson(Object object) {
		return GSON.toJsonTree(object);
	}

	/**
	 * Decodes an object from JSON.
	 *
	 * @param json The JSON to decode.
	 * @return The decoded object.
	 */
	static JsonObject jsonDecode(byte[] json) {
		return GSON.fromJson(new String(json), JsonObject.class);
	}

	/**
	 * Decodes an object from JSON.
	 *
	 * @param reader The reader to read from.
	 * @param type   The type of the object.
	 * @return The decoded object.
	 */
	static <T> T jsonDecode(Reader reader, Class<T> type) {
		return GSON.fromJson(reader, type);
	}

	/**
	 * Decodes an object from JSON.
	 *
	 * @param json The JSON to decode.
	 * @param type The type of the object.
	 * @return The decoded object.
	 */
	static <T> T jsonDecode(byte[] json, Class<T> type) {
		return GSON.fromJson(new String(json), type);
	}

	/**
	 * Decodes an object from JSON.
	 *
	 * @param json The JSON to decode.
	 * @param type The type of the object.
	 * @return The decoded object.
	 */
	static <T> T jsonDecode(String json, Class<T> type) {
		return GSON.fromJson(json, type);
	}

	/**
	 * Decodes an object from JSON.
	 *
	 * @param object The JSON to decode.
	 * @param type   The type of the object.
	 * @return The decoded object.
	 */
	static <T> T jsonDecode(JsonElement object, Class<T> type) {
		return GSON.fromJson(object, type);
	}

	/**
	 * Encodes an object to JSON.
	 *
	 * @param object The object to encode.
	 * @return The encoded JSON.
	 */
	static String jsonEncode(Object object) {
		return GSON.toJson(object);
	}

	/**
	 * Encodes an object to JSON.
	 *
	 * @param object The object to encode.
	 * @return The encoded JSON.
	 */
	static String toString(Object object) {
		return EncodingUtils.jsonEncode(object);
	}

	/**
	 * Encodes bytes to Base64.
	 *
	 * @param bytes The bytes to encode.
	 * @return The encoded string.
	 */
	static String base64Encode(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	/**
	 * Decodes Base64 to bytes.
	 *
	 * @param string The string to decode.
	 * @return The decoded bytes.
	 */
	static byte[] base64Decode(String string) {
		return Base64.getDecoder().decode(string);
	}

	/**
	 * Validates a URL.
	 *
	 * @param url The URL to validate.
	 * @return Whether the URL is valid.
	 */
	static boolean isValidUrl(String url) {
		try {
			new URL(url).toURI();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
