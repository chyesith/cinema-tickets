package uk.gov.dwp.uc.pairtest.calculation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.Ticket;
import uk.gov.dwp.uc.pairtest.domain.TicketType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultPricingStrategyTest {
	private DefaultPricingStrategy pricingStrategy;

	@BeforeEach
	void setUp() {
		pricingStrategy = new DefaultPricingStrategy();
	}

	@Test
	void shouldCalculateTotalPriceCorrectly() {
		int total = pricingStrategy.calculatePrice(
			new Ticket(TicketType.ADULT ,2),  // 2 * 25 = 50
			new Ticket(TicketType.CHILD ,1),  // 2 * 25 = 50
			new Ticket(TicketType.INFANT ,1) // 2 * 25 = 50
		);
		assertEquals(65, total);
	}

	@Test
	void shouldReturnZeroForNoTickets() {
		int total = pricingStrategy.calculatePrice();
		assertEquals(0, total);
	}
}
