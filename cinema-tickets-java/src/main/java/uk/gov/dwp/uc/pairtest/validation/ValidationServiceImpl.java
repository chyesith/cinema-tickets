package uk.gov.dwp.uc.pairtest.validation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Arrays;

public class ValidationServiceImpl implements TicketValidationService {
	private static final int MAX_TICKETS = 25;

	@Override
	public void validate(Long accountId, TicketTypeRequest... ticketTypeRequests) {
		validateAccountId(accountId);
		validateRequest(ticketTypeRequests);
		int adultTickets = 0;
		int childTickets = 0;
		int infantTickets = 0;

		for (TicketTypeRequest request : ticketTypeRequests) {
			switch (request.getTicketType()) {
				case ADULT -> adultTickets += request.getNoOfTickets();
				case CHILD -> childTickets += request.getNoOfTickets();
				case INFANT -> infantTickets += request.getNoOfTickets();
			}
		}
		int totalTickets = adultTickets + childTickets + infantTickets;
		validateNoOfTickets(totalTickets);
		validateAdultPresence(adultTickets, childTickets, infantTickets);
		validateInfantTicketCountWithAdultTickets(adultTickets, infantTickets);
	}


	private void validateAccountId(Long accountId) {
		if (accountId == null || accountId < 0) {
			throw new InvalidPurchaseException("Invalid account ID:" + accountId);

		}
	}


	private void validateRequest(TicketTypeRequest... requests) {
		if (requests == null || requests.length == 0) {
			throw new InvalidPurchaseException("At least a one ticket request should be provided in ticketRequest");

		}
		boolean hasInvalidTickets = Arrays.stream(requests).anyMatch(req -> req.getNoOfTickets() <= 0);

		if (hasInvalidTickets) {
			throw new InvalidPurchaseException("Can not contain zero or negative number tickets");

		}


	}


	private void validateNoOfTickets(int totalTickets) {
		if (totalTickets <= 0) {
			throw new InvalidPurchaseException("At least a one ticket request should be provided in ticketRequest");
		}

		if (totalTickets > MAX_TICKETS) {
			throw new InvalidPurchaseException("Can not purchase more than" + MAX_TICKETS + "tickets");
		}
	}


	private void validateAdultPresence(int adultTicket, int childTickets, int infantTickets) {
		if (adultTicket == 0 && (childTickets > 0 || infantTickets > 0)) {
			throw new InvalidPurchaseException("Can not purchase child or infant tickets without adult tickets");
		}

	}

	//This is assumption because infant will be sitting on the adult's lap
	private void validateInfantTicketCountWithAdultTickets(int adultTicket, int infantTickets) {
		if (adultTicket < infantTickets) {
			throw new InvalidPurchaseException("infant ticket should be same as adult ticket count");
		}

	}
}
