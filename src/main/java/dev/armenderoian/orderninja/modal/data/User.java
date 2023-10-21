package dev.armenderoian.orderninja.modal.data;

import com.google.gson.JsonObject;
import dev.armenderoian.orderninja.interfaces.DatabaseObject;
import dev.armenderoian.orderninja.modal.data.organization.Organization;
import dev.armenderoian.orderninja.util.DatabaseUtils;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
@Entity(value = "users", useDiscriminator = false)
public class User implements DatabaseObject<User> {

	/**
	 * Fetches a user by their ID.
	 *
	 * @param id The ID of the user.
	 * @return The user.
	 */
	public static User getUserById(String id) {
		return DatabaseUtils.fetch(
				User.class, "_id", id);
	}

	@Id
	private String userId;
	private String username;
	private String password;
	@Reference(ignoreMissing = true, lazy = true)
	private Map<String, Organization> organizations;

	@Override
	public JsonObject explain() {
		return null;
	}
}