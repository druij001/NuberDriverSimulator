package nuber.students;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The core Dispatch class that instantiates and manages everything for Nuber
 * 
 * @author james
 *
 */
public class NuberDispatch {

	/**
	 * The maximum number of idle drivers that can be awaiting a booking 
	 */
	private final int MAX_DRIVERS = 999;
	
	private boolean logEvents = false;
	private HashMap<String, NuberRegion> regions;
	private BlockingQueue<Driver> drivers;
	private AtomicInteger bookingsAwaitingDriver;
	
	/**
	 * Creates a new dispatch objects and instantiates the required regions and any other objects required.
	 * It should be able to handle a variable number of regions based on the HashMap provided.
	 * 
	 * @param regionInfo Map of region names and the max simultaneous bookings they can handle
	 * @param logEvents Whether logEvent should print out events passed to it
	 */
	public NuberDispatch(HashMap<String, Integer> regionInfo, boolean logEvents)
	{
		this.logEvents = logEvents;
		this.regions = new HashMap<String, NuberRegion>();
		this.drivers = new ArrayBlockingQueue<Driver>(MAX_DRIVERS);
		this.bookingsAwaitingDriver = new AtomicInteger(0);
		
		// Create a HashMap of regions
		this.logEvent(null, "Creating ".concat(String.valueOf(regionInfo.size()).concat(" regions")));
		
		for (Entry<String, Integer> r : regionInfo.entrySet()) {
			this.regions.put(r.getKey(), new NuberRegion(this, r.getKey(), r.getValue()));
		}	
		
		this.logEvent(null, "Done creating ".concat(String.valueOf(regionInfo.size()).concat(" regions")));
	}
	
	/**
	 * Adds drivers to a queue of idle driver.
	 *  
	 * Must be able to have drivers added from multiple threads.
	 * 
	 * @param The driver to add to the queue.
	 * @return Returns true if driver was added to the queue
	 */
	public boolean addDriver(Driver newDriver)
	{
		// Using offer instead of add to prevent capacity exception
		return drivers.offer(newDriver);
	}
	
	/**
	 * Gets a driver from the front of the queue
	 *  
	 * Must be able to have drivers added from multiple threads.
	 * 
	 * @return A driver that has been removed from the queue
	 * @throws InterruptedException 
	 */
	public Driver getDriver() throws InterruptedException
	{
		this.bookingsAwaitingDriver.incrementAndGet();

		Driver d = this.drivers.take();
		
        this.bookingsAwaitingDriver.getAndDecrement();
		
		return d;
	}

	/**
	 * Prints out the string
	 * 	    booking + ": " + message
	 * to the standard output only if the logEvents variable passed into the constructor was true
	 * 
	 * @param booking The booking that's responsible for the event occurring
	 * @param message The message to show
	 */
	public void logEvent(Booking booking, String message) {
		
		if (!logEvents) return;
			
		System.out.println(booking + ": " + message);
		
	}

	/**
	 * Books a given passenger into a given Nuber region.
	 * 
	 * Once a passenger is booked, the getBookingsAwaitingDriver() should be returning one higher.
	 * 
	 * If the region has been asked to shutdown, the booking should be rejected, and null returned.
	 * 
	 * @param passenger The passenger to book
	 * @param region The region to book them into
	 * @return returns a Future<BookingResult> object
	 */
	public Future<BookingResult> bookPassenger(Passenger passenger, String region) {
		NuberRegion r = regions.get(region);
		
		//Return null if an invalid string was passed in
		if(r == null) {
			System.out.println("Oops. An invalid region was given for the booking. It has not been created");
			return null;
		}
				
		// Otherwise book the passenger in and return the result
		return r.bookPassenger(passenger);
	}

	/**
	 * Gets the number of non-completed bookings that are awaiting a driver from dispatch
	 * 
	 * Once a driver is given to a booking, the value in this counter should be reduced by one
	 * 
	 * @return Number of bookings awaiting driver, across ALL regions
	 */
	public int getBookingsAwaitingDriver()
	{
		return bookingsAwaitingDriver.getAcquire();
	}
	
	/**
	 * Tells all regions to finish existing bookings already allocated, and stop accepting new bookings
	 */
	public void shutdown() {
		this.logEvent(null, "The system is shutting down");
		for (Entry<String, NuberRegion> r : this.regions.entrySet()) {
			r.getValue().shutdown();
		}
	}
}
