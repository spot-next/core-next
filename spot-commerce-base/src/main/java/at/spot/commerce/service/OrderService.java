package at.spot.commerce.service;

import java.util.List;

import at.spot.commerce.enumeration.OrderStatus;
import at.spot.commerce.exception.OrderCancellationException;
import at.spot.commerce.model.customer.Customer;
import at.spot.commerce.model.order.Order;
import at.spot.commerce.strategy.impl.OrderIdGeneratorStrategy;

/**
 * This service provides functionality around the order object.
 */
public interface OrderService {

	/**
	 * Creates a new order order for the given customer. The order id is
	 * generated using the {@link OrderIdGeneratorStrategy}.
	 * 
	 * @param customer
	 * @return
	 */
	Order createNewOrder(Customer customer);

	/**
	 * Returns all orders for the given status that fit the filter criteria.
	 * 
	 * @param customer
	 * @param filter
	 * @return
	 */
	List<Order> getOrders(Customer customer, OrderStatus... filter);

	/**
	 * Gets an order by its ID.
	 * 
	 * @param orderId
	 * @return
	 */
	Order getOrderById(String orderId);

	/**
	 * Creates a new order based on the given one. A new order will be created,
	 * but the order entries, and settings will be the same. <br />
	 * Prices and other conditions may change though, depending on the service
	 * implementation.
	 * 
	 * @param sourceOrder
	 * @return
	 */
	Order reorder(Order sourceOrder);

	/**
	 * Cancels the given order:
	 * 
	 * @param order
	 * @throws OrderCancellationException
	 */
	void cancelOrder(Order order) throws OrderCancellationException;
}
