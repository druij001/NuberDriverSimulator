package nuber.students;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * Booking represents the overall "job" for a passenger getting to their destination.
 * 
 * It begins with a passenger, and when the booking is commenced by the region 
 * responsible for it, an available driver is allocated from dispatch. If no driver is 
 * available, the booking must wait until one is. When the passenger arrives at the destination,
 * a BookingResult object is provided with the overall information for the booking.
 * 
 * The Booking must track how long it takes, from the instant it is created, to when the 
 * passenger arrives at their destination. This should be done using Date class' getTime().
 * 
 * Booking's should have a globally unique, sequential ID, allocated on their creation. 
 * This should be multi-thread friendly, allowing bookings to be created from different threads.
 * 
 * @author james
 *
 */
public class Booking implements Callable<BookingResult>{
	
	private static final AtomicInteger counter = new AtomicInteger();
	
	NuberDispatch dispatch;
	Passenger passenger;
	String driverName;
	int id;
		
	/**
	 * Creates a new booking for a given Nuber dispatch and passenger, noting that no
	 * driver is provided as it will depend on whether one is available when the region 
	 * can begin processing this booking.
	 * 
	 * @param dispatch
	 * @param passenger
	 */
	public Booking(NuberDispatch dispatch, Passenger passenger)
	{
		this.dispatch = dispatch;
		this.passenger = passenger;
		this.driverName = null;
		
		id = counter.incrementAndGet();
	}
	
	/**
	 * At some point, the Nuber Region responsible for the booking can start it (has free spot),
	 * and calls the Booking.call() function, which:
	 * 1.	Asks Dispatch for an available driver
	 * 2.	If no driver is currently available, the booking must wait until one is available. 
	 * 3.	Once it has a driver, it must call the Driver.pickUpPassenger() function, with the 
	 * 			thread pausing whilst as function is called.
	 * 4.	It must then call the Driver.driveToDestination() function, with the thread pausing 
	 * 			whilst as function is called.
	 * 5.	Once at the destination, the time is recorded, so we know the total trip duration. 
	 * 6.	The driver, now free, is added back into Dispatch�s list of available drivers. 
	 * 7.	The call() function the returns a BookingResult object, passing in the appropriate 
	 * 			information required in the BookingResult constructor.
	 *
	 * @return A BookingResult containing the final information about the booking 
	 */
	
	@Override
	public BookingResult call() {
		
		Driver driver = null;
		LocalDateTime startTime = getCurrentTimestamp();
		
			// Attempt to get driver
			try {
				dispatch.logEvent(this, "Starting booking, getting driver");
				driver = this.dispatch.getDriver();
				if(driver != null)
					this.driverName = driver.name;
			} catch(InterruptedException e) {
				dispatch.logEvent(this, "The booking was interrupted while waiting for a driver");
			}

		
		// Pick up passenger (cancel trip if thread is interrupted)
		try {
			dispatch.logEvent(this, "Driving, on way to passenger");
			driver.pickUpPassenger(this.passenger);
		} catch (InterruptedException e) {
			dispatch.logEvent(this, "The booking was interrupted while picking up the passenger");
			dispatch.addDriver(driver);
			return null;
		}
		

		
		// Drive to destination (Cancel trip if thread is interrupted)
		try {
			dispatch.logEvent(this, "Collected passenger, on way to destination");
			driver.driveToDestination();
		} catch(InterruptedException e) {
			dispatch.logEvent(this, "The booking was interrupted while driving to the destination");
			dispatch.addDriver(driver);
			return null;
		}
		
		// Driver has arrived at destination
		LocalDateTime endTime = getCurrentTimestamp();
		long totalTime = Duration.between(startTime, endTime).toMillis();
		dispatch.logEvent(this, "At destination, driver is now free");

		// Add the driver back to the dispatch
		dispatch.addDriver(driver);
		
		return new BookingResult(id, this.passenger, driver, totalTime);
	}
	
	private LocalDateTime getCurrentTimestamp() {
		return LocalDateTime.now();
	}
	
	/***
	 * Should return the:
	 * - booking ID, 
	 * - followed by a colon, 
	 * - followed by the driver's name (if the driver is null, it should show the word "null")
	 * - followed by a colon, 
	 * - followed by the passenger's name (if the passenger is null, it should show the word "null")
	 * 
	 * @return The compiled string
	 */
	@Override
	public String toString()
	{	
		String returnString = "";
		returnString = returnString.concat(String.valueOf(this.id)).concat(":");
		returnString = returnString.concat(this.driverName == null ? "null" : this.driverName).concat(":");
		returnString = returnString.concat(this.passenger.name == null ? "null" : this.passenger.name);
		return returnString;
	}

}
