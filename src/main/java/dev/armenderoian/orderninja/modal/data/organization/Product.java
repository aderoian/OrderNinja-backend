package dev.armenderoian.orderninja.modal.data.organization;

import dev.morphia.annotations.Entity;
import lombok.Data;

@Data
@Entity
public class Product {

	private String name;
	private String description;
	private String price;
	private String imageUrl;
	private Availability availability;

	public enum Availability {
		IN_STOCK,
		OUT_OF_STOCK,
		PRE_ORDER,
		NOT_AVAILABLE
	}
}
