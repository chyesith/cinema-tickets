package uk.gov.dwp.uc.pairtest.mapper;

import uk.gov.dwp.uc.pairtest.domain.Ticket;
import uk.gov.dwp.uc.pairtest.domain.TicketType;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

public class TicketMapper {
	private TicketMapper() {

	}

	public static Ticket toTicket(TicketTypeRequest request) {
		return new Ticket(
			TicketType.valueOf(request.getTicketType().name()),
			request.getNoOfTickets()
		);
	}
}
