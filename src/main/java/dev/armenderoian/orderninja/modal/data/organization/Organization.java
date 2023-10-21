package dev.armenderoian.orderninja.modal.data.organization;

import com.google.gson.JsonObject;
import dev.armenderoian.orderninja.interfaces.DatabaseObject;
import dev.armenderoian.orderninja.modal.data.User;
import dev.armenderoian.orderninja.util.DatabaseUtils;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import lombok.Data;

import java.util.Map;

@Data
@Entity(value = "organizations", useDiscriminator = false)
public class Organization implements DatabaseObject<Organization> {

	/**
	 * Fetches an organization by its ID.
	 *
	 * @param id The ID of the organization.
	 * @return The user.
	 */
	public static User getOrganizationById(String id) {
		return DatabaseUtils.fetch(
				User.class, "_id", id);
	}

	@Id
	private String organizationId;
	private String displayName;
	private String description;
	private String iconUrl;
	@Reference
	private User owner;
	private Map<String, OrganizationMember> members;
	private Map<String, Product> products;

	@Override
	public JsonObject explain() {
		return null;
	}
}
