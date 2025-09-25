package uk.gov.dwp.uc.pairtest.domain;

public enum TicketType {
	ADULT(25 , true),
	CHILD(15 , true),
	INFANT(0 , false);

	private final int price;
	private final boolean requiredSeat;

	TicketType(int price, boolean requiredSeat) {
		this.requiredSeat = requiredSeat;
		this.price = price;
	}

	public int price() {
		return price;
	}


	public boolean requiredSeat() {
		return requiredSeat;
	}
}
