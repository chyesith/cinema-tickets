package uk.gov.dwp.uc.pairtest.validation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

public interface TicketValidationService {
	void validate(Long accountId, TicketTypeRequest... ticketTypeRequests);

}
