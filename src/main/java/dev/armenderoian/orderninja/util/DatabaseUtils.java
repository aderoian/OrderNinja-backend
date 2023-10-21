package dev.armenderoian.orderninja.util;

import dev.armenderoian.orderninja.OrderNinja;
import dev.armenderoian.orderninja.interfaces.DatabaseObject;
import dev.morphia.query.filters.Filters;

public interface DatabaseUtils {

	/**
	 * Fetches a database object by a parameter.
	 *
	 * @param type  The type of object to fetch.
	 * @param param The parameter to search for.
	 * @param value The value of the parameter.
	 * @return The object, or null if not found.
	 */
	static <T extends DatabaseObject<?>> T fetch(
			Class<T> type,
			String param, Object value
	) {
		return OrderNinja.getDatastore().find(type)
				.filter(Filters.eq(param, value))
				.first();
	}

	/**
	 * Checks if a database object exists.
	 *
	 * @param type  The type of object to check.
	 * @param param The parameter to search for.
	 * @param value The value of the parameter.
	 * @return True if the object exists, false otherwise.
	 */
	static boolean find(
			Class<? extends DatabaseObject<?>> type,
			String param, Object value
	) {
		return DatabaseUtils.fetch(type, param, value) != null;
	}


}
