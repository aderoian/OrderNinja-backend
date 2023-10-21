package dev.armenderoian.orderninja.interfaces;

import com.google.gson.JsonObject;
import dev.armenderoian.orderninja.OrderNinja;

@SuppressWarnings("unchecked")
public interface DatabaseObject<T> {

	/**
	 * Saves this object to the database.
	 */
	default T save() {
		OrderNinja.getDatastore().save(this);
		return (T) this;
	}

	/**
	 * Deletes this object from the database.
	 */
	default boolean delete() {
		return OrderNinja.getDatastore().delete(this).getDeletedCount() > 0;
	}

	/**
	 * @return A plain representation of this object.
	 */
	JsonObject explain();
}