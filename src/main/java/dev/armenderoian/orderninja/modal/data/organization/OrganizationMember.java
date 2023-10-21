package dev.armenderoian.orderninja.modal.data.organization;

import dev.armenderoian.orderninja.modal.data.User;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Reference;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class OrganizationMember {

	@Reference
	private User user;
	private List<String> roles;

}