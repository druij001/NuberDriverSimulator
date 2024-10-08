package nuber.students;

public class BookingResult {

	public int jobID;
	public Passenger passenger;
	public Driver driver;
	public long tripDuration;
	
	/** Holds information about the result of a booking
	 * @param jobID
	 * @param passenger
	 * @param driver
	 * @param tripDuration
	 */
	public BookingResult(int jobID, Passenger passenger, Driver driver, long tripDuration)
	{
		this.jobID = jobID;
		this.passenger = passenger;
		this.driver = driver;
		this.tripDuration = tripDuration;
	}
	
}
