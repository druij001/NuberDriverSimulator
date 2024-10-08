package nuber.students;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A single Nuber region that operates independently of other regions, other than getting 
 * drivers from bookings from the central dispatch.
 * 
 * A region has a maxSimultaneousJobs setting that defines the maximum number of bookings 
 * that can be active with a driver at any time. For passengers booked that exceed that 
 * active count, the booking is accepted, but must wait until a position is available, and 
 * a driver is available.
 * 
 * Bookings do NOT have to be completed in FIFO order.
 * 
 * @author James
 *
 */
public class NuberRegion {

	private int maxSimultaneousJobs;
	private NuberDispatch dispatch;
	public String regionName;
	private boolean isShutdown;
	
	private ExecutorService executorPool;
	
	/**
	 * Creates a new Nuber region
	 * 
	 * @param dispatch The central dispatch to use for obtaining drivers, and logging events
	 * @param regionName The regions name, unique for the dispatch instance
	 * @param maxSimultaneousJobs The maximum number of simultaneous bookings the region is allowed to process
	 */
	public NuberRegion(NuberDispatch dispatch, String regionName, int maxSimultaneousJobs)
	{
		this.maxSimultaneousJobs = maxSimultaneousJobs;
		this.regionName = regionName;
		this.dispatch = dispatch;
		this.isShutdown = false;
		this.executorPool = Executors.newFixedThreadPool(this.maxSimultaneousJobs);
	}
	
	/**
	 * Creates a booking for given passenger, and adds the booking to the 
	 * collection of jobs to process. Once the region has a position available, and a driver is available, 
	 * the booking should commence automatically. 
	 * 
	 * If the region has been told to shutdown, this function should return null, and log a message to the 
	 * console that the booking was rejected.
	 * 
	 * @param waitingPassenger
	 * @return a Future that will provide the final BookingResult object from the completed booking
	 */
	public Future<BookingResult> bookPassenger(Passenger waitingPassenger)
	{		
		if (this.isShutdown) {
			this.dispatch.logEvent(null, "booking for " + waitingPassenger.name + " has ben rejected");
			return null;
		}
		
		Booking booking = new Booking(dispatch, waitingPassenger);
		Future<BookingResult> result = executorPool.submit(booking);
		
		return result;
	}
	
	/**
	 * Called by dispatch to tell the region to complete its existing bookings and stop accepting any new bookings
	 */
	public void shutdown()
	{
		this.dispatch.logEvent(null, this.regionName + " is shutting down");
		executorPool.shutdown();
		this.isShutdown = true;
	}
}
