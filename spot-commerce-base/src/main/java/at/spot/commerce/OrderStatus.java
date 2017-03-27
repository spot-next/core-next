package at.spot.commerce;

/**
 * Describes the status of an order
 */
public enum OrderStatus {
	OPEN, APPROVAL_PENDING, APPROVED, APPROVAL_REJECTED, CANCELLED, UNKNOWN;
}
