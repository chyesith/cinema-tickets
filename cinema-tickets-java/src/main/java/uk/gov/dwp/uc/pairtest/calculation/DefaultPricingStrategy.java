package uk.gov.dwp.uc.pairtest.calculation;

import uk.gov.dwp.uc.pairtest.domain.Ticket;

import java.util.Arrays;

public class DefaultPricingStrategy implements PricingStrategy{
	@Override
	public int calculatePrice(Ticket... tickets) {
		return Arrays.stream(tickets).mapToInt(ticket -> ticket.quantity() * ticket.type().price()).sum();
	}
}
