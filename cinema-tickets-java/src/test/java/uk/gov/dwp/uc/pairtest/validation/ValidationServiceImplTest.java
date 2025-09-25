package uk.gov.dwp.uc.pairtest.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceImplTest {
	private ValidationServiceImpl validationService;

	@BeforeEach
	void setUp() {
		validationService = new ValidationServiceImpl();
	}


	@DisplayName(" should reject  null account Id")
	@Test
	void shouldThrowExceptionForNullAccountId() {
		TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
		InvalidPurchaseException exception = assertThrows(
			InvalidPurchaseException.class,
			() -> validationService.validate(null, request)
		);
		assertTrue(exception.getMessage().contains("Invalid account ID:"));

	}


	@DisplayName(" should reject  if there is a zero ticket quantity in ticket request")
	@Test
	void shouldThrowExceptionForZeroTickets() {
		TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);
		InvalidPurchaseException exception = assertThrows(
			InvalidPurchaseException.class,
			() -> validationService.validate(1L, request)
		);
		assertTrue(exception.getMessage().contains("At least a one ticket request should be provided in ticketRequest"));
	}

	static Stream<Arguments> invalidTicketRequests() {
		return Stream.of(
			Arguments.of((Object) new TicketTypeRequest[]{ new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2) }),
			Arguments.of((Object) new TicketTypeRequest[]{ new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2) }),
			Arguments.of((Object) new TicketTypeRequest[]{
				new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
				new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
			})
		);

	}




	@DisplayName("should reject if there is a infant ticket without adult ticket")
	@ParameterizedTest
	@MethodSource("invalidTicketRequests")
	void shouldThrowExceptionForChildOrInfantTicketsWithoutAdult(TicketTypeRequest... requests) {
		InvalidPurchaseException exception = assertThrows(
			InvalidPurchaseException.class,
			() -> validationService.validate(1L, requests)
		);
		assertTrue(exception.getMessage().contains("Can not purchase child or infant tickets without adult tickets"));
	}


	@DisplayName("should failed empty ticket request")
	@Test
	void shouldRejectEmptyRequests() {
		InvalidPurchaseException exception = assertThrows(
			InvalidPurchaseException.class,
			() -> validationService.validate(1L, new TicketTypeRequest[]{})
		);
		assertTrue(exception.getMessage().contains("At least a one ticket request should be provided in ticketRequest"));
	}


	@DisplayName("should reject if there are ticket more than max number")
	@Test
	void shouldThrowExceptionForIfMoreThanMaxNumberTickets() {
		TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);
		InvalidPurchaseException exception = assertThrows(
			InvalidPurchaseException.class,
			() -> validationService.validate(1L, request)
		);
		assertTrue(exception.getMessage().contains("Can not purchase more than"));
	}

	@DisplayName("should reject if there is a minus number of tickets")
	@ParameterizedTest
	@ValueSource(ints = {-5, -1})
	void shouldThrowExceptionForMinusNumbersOfTickets(int qty) {
		TicketTypeRequest[] requests = {new TicketTypeRequest(TicketTypeRequest.Type.ADULT, qty)};
		InvalidPurchaseException exception = assertThrows(
			InvalidPurchaseException.class,
			() -> validationService.validate(1L, requests)
		);
		assertTrue(exception.getMessage().contains("At least a one ticket request should be provided in ticketRequest"));
	}

	@DisplayName("should reject negative number tickets mixed with valid tickets")
	@Test
	void shouldThrowExceptionForNegativeTicketsMixedWithValidTickets() {
		TicketTypeRequest[] tickets = {
			new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
			new TicketTypeRequest(TicketTypeRequest.Type.CHILD, -1)
		};
		InvalidPurchaseException exception = assertThrows(
			InvalidPurchaseException.class,
			() -> validationService.validate(1L, tickets)
		);
		assertTrue(exception.getMessage().contains("Can not contain zero or negative number tickets"));
	}

	@DisplayName("should reject if 0 number of tickets combinations tickets")
	@Test
	void shouldThrowExceptionIfAllTicketsZero() {
		TicketTypeRequest[] requests = {
			new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0),
			new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 0),
			new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 0)
		};
		InvalidPurchaseException exception = assertThrows(
			InvalidPurchaseException.class,
			() -> validationService.validate(1L, requests)
		);
		assertTrue(exception.getMessage().contains("At least a one ticket request should be provided in ticketRequest"));
	}
	@DisplayName("should reject if there is a infant tickets exceed adult tickets")
	@Test
	void shouldFailIfInfantTicketsExceedsAdult() {
		TicketTypeRequest[] tickets = {
			new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
			new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3)
		};
		InvalidPurchaseException exception = assertThrows(
			InvalidPurchaseException.class,
			() -> validationService.validate(1L, tickets)
		);
		assertEquals("infant ticket should be same as adult ticket count", exception.getMessage());
	}

	@DisplayName("should pass max adult only tickets")
	@Test
	void shouldPassForMaximumAdultsOnly() {
		TicketTypeRequest[] requests = {
			new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 25)
		};
		assertDoesNotThrow(() -> validationService.validate(1L, requests));
	}


	@DisplayName("should pass with adult and infant count equals")
	@Test
	void shouldPassIfInfantEqualsAdult() {
		TicketTypeRequest[] tickets = {
			new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3),
			new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3)
		};
		assertDoesNotThrow(() -> validationService.validate(1L, tickets));
	}




}
