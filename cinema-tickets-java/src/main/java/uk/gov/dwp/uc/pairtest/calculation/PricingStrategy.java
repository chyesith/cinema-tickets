package uk.gov.dwp.uc.pairtest.calculation;

import uk.gov.dwp.uc.pairtest.domain.Ticket;

public interface PricingStrategy {
	int calculatePrice(Ticket... ticket);

}
