package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.calculation.DefaultPricingStrategy;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.validation.ValidationServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class TicketServiceImplTest {
	@Mock
	private TicketPaymentService paymentService;

	@Mock
	private SeatReservationService reservationService;

	private TicketServiceImpl ticketService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		ticketService = new TicketServiceImpl(	new ValidationServiceImpl(),
			paymentService,
			reservationService,
			new DefaultPricingStrategy());
	}

	@DisplayName("should purchase successfully")
	@Test
	void shouldPurchaseTicketsSuccessfully() {
		TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
		TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

		assertDoesNotThrow(() -> ticketService.purchaseTickets(1L, adult, child));

		verify(paymentService).makePayment(1L, 65); // 2*25 + 1*15
		verify(reservationService).reserveSeat(1L, 3); // adults + children
	}

	@DisplayName("should failed purchase without adults")

	@Test
	void shouldRejectChildWithoutAdult() {
		TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

		InvalidPurchaseException exception = assertThrows(
			InvalidPurchaseException.class,
			() -> ticketService.purchaseTickets(1L, child)
		);

		assertTrue(exception.getMessage().contains("Can not purchase child or infant tickets without adult tickets"));

		verifyNoInteractions(paymentService);
		verifyNoInteractions(reservationService);
	}

	@DisplayName("should failed null ticket request")
	@Test
	void shouldRejectNullRequests() {
		InvalidPurchaseException exception = assertThrows(
			InvalidPurchaseException.class,
			() -> ticketService.purchaseTickets(1L, (TicketTypeRequest[]) null)
		);

		assertTrue(exception.getMessage().contains("At least a one ticket request"));

		verifyNoInteractions(paymentService);
		verifyNoInteractions(reservationService);
	}



	@DisplayName("should call payment service with correct amount ")
	@Test
	void shouldCallPaymentServiceWithCorrectAmount() {
		TicketTypeRequest[] tickets = {
			new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
			new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2)
		};
		ticketService.purchaseTickets(1L, tickets);
		verify(paymentService).makePayment(1L, 2*25 + 2*15);
	}


	@DisplayName("should reserve correct number of seats")
	@Test
	void shouldCallSeatReservationServiceWithCorrectSeats() {
		TicketTypeRequest[] tickets = {
			new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
			new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3),
			new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2)
		};
		ticketService.purchaseTickets(1L, tickets);
		verify(reservationService).reserveSeat(1L, 5); // infants not counted
	}

}
