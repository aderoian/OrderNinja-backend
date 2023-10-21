package dev.armenderoian.orderninja.modal;

import dev.armenderoian.orderninja.modal.data.User;
import dev.armenderoian.orderninja.modal.data.organization.Organization;

public interface Modals {
	Class<?>[] MODALS = {
			User.class, Organization.class
	};
}
