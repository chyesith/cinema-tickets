package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.calculation.PricingStrategy;
import uk.gov.dwp.uc.pairtest.domain.Ticket;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.mapper.TicketMapper;
import uk.gov.dwp.uc.pairtest.validation.TicketValidationService;

import java.util.Arrays;

public class TicketServiceImpl implements TicketService {
    private final TicketValidationService ticketValidationService;
    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;
    private final PricingStrategy pricingStrategy;

    public TicketServiceImpl(TicketValidationService ticketValidationService, TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService, PricingStrategy pricingStrategy) {
        this.ticketValidationService = ticketValidationService;
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
        this.pricingStrategy = pricingStrategy;
    }
    /**
     * Should only have private methods other than the one below.
     */

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        this.ticketValidationService.validate(accountId, ticketTypeRequests);

        Ticket[] tickets = Arrays.stream(ticketTypeRequests).map(TicketMapper::toTicket).toArray(Ticket[]::new);
        int totalPrice = pricingStrategy.calculatePrice(tickets);
        int noOfSeats = Arrays.stream(tickets)
                .filter(ticket -> ticket.type().requiredSeat()).mapToInt(Ticket::quantity).sum();
        ticketPaymentService.makePayment(accountId, totalPrice);
        seatReservationService.reserveSeat(accountId, noOfSeats);
    }

}
