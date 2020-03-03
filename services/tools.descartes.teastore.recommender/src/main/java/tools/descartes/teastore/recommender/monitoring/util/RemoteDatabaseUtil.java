package tools.descartes.teastore.recommender.monitoring.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import tools.descartes.teastore.entities.Category;
import tools.descartes.teastore.entities.Order;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.entities.User;
import tools.descartes.teastore.persistence.repository.DataGenerator;
import tools.descartes.teastore.registryclient.Service;
import tools.descartes.teastore.registryclient.rest.LoadBalancedCRUDOperations;

public class RemoteDatabaseUtil {
	private static final int MAX_ITEMS_PER_ORDER = 10;
	private static final double PREFFERED_CATEGORY_CHANCE = 0.825;
	
	private Random random = new Random(5);
	
	public void clearDatabase() {
		try {
			List<Order> orders = LoadBalancedCRUDOperations.getEntities(Service.PERSISTENCE, "orders", Order.class, -1,
					-1);

			orders.forEach(e -> {
				LoadBalancedCRUDOperations.deleteEntity(Service.PERSISTENCE, "orders", Order.class, e.getId());
			});

			generateOrders(DataGenerator.SMALL_DB_MAX_ORDERS_PER_USER, DataGenerator.SMALL_DB_PRODUCTS_PER_CATEGORY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateOrders(int maxOrdersPerUser, int productsPerCategory) {
		LoadBalancedCRUDOperations.getEntities(Service.PERSISTENCE, "users", User.class, -1, -1).parallelStream()
				.forEach(user -> {
					for (int i = 0; i < random.nextInt(maxOrdersPerUser + 1); i++) {
						Order order = new Order();
						order.setAddressName(user.getRealName());
						String eastWest = " East ";
						if (random.nextDouble() > 0.5) {
							eastWest = " West ";
						}
						String northSouth = " North";
						if (random.nextDouble() > 0.5) {
							northSouth = " South";
						}
						order.setAddress1(random.nextInt(9000) + eastWest + random.nextInt(9000) + northSouth);
						order.setAddress2(
								"District " + random.nextInt(500) + ", Utopia, " + (10000 + random.nextInt(40000)));
						order.setCreditCardCompany("MasterCard");
						if (random.nextDouble() > 0.5) {
							order.setCreditCardCompany("Visa");
						}
						order.setCreditCardExpiryDate(
								LocalDate.ofYearDay(LocalDateTime.now().getYear() + 1 + random.nextInt(10),
										1 + random.nextInt(363)).format(DateTimeFormatter.ISO_LOCAL_DATE));
						order.setTime(LocalDateTime
								.of(LocalDateTime.now().getYear() - random.nextInt(10), 1 + random.nextInt(10),
										1 + random.nextInt(24), random.nextInt(23), random.nextInt(59))
								.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
						order.setUserId(user.getId());
						order.setCreditCardNumber(
								fourDigits() + " " + fourDigits() + " " + fourDigits() + " " + fourDigits());
						long orderId = LoadBalancedCRUDOperations.sendEntityForCreation(Service.PERSISTENCE, "orders",
								Order.class, order);

						Order createdOrder = LoadBalancedCRUDOperations.getEntity(Service.PERSISTENCE, "orders",
								Order.class, orderId);

						long price = 0;

						List<Category> categories = LoadBalancedCRUDOperations.getEntities(Service.PERSISTENCE,
								"categories", Category.class, -1, -1);

						Category preferred = categories.get(random.nextInt(categories.size()));
						for (int j = 0; j < 1 + random.nextInt(MAX_ITEMS_PER_ORDER); j++) {
							OrderItem item = generateOrderItem(createdOrder, preferred, productsPerCategory);
							price += item.getQuantity() * item.getUnitPriceInCents();

							LoadBalancedCRUDOperations.sendEntityForCreation(Service.PERSISTENCE, "orderitems",
									OrderItem.class, item);
						}
						createdOrder.setTotalPriceInCents(price);

						LoadBalancedCRUDOperations.sendEntityForUpdate(Service.PERSISTENCE, "orders", Order.class,
								orderId, createdOrder);
					}
				});
	}

	private OrderItem generateOrderItem(Order order, Category preferred, int productsPerCategory) {
		OrderItem item = new OrderItem();
		item.setOrderId(order.getId());
		item.setQuantity(random.nextInt(7));
		Category itemCategory = preferred;
		if (random.nextDouble() > PREFFERED_CATEGORY_CHANCE) {
			List<Category> categories = LoadBalancedCRUDOperations.getEntities(Service.PERSISTENCE, "categories",
					Category.class, -1, -1);
			itemCategory = categories.get(random.nextInt(categories.size()));
		}
		Product product = LoadBalancedCRUDOperations.getEntities(Service.PERSISTENCE, "products", Product.class,
				"category", itemCategory.getId(), random.nextInt(productsPerCategory), 1).get(0);
		item.setProductId(product.getId());
		item.setUnitPriceInCents(product.getListPriceInCents());
		return item;
	}

	private String fourDigits() {
		return String.valueOf(1000 + random.nextInt(8999));
	}

}
