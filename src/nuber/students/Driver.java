package nuber.students;

import java.util.Random;

public class Driver extends Person{
	
	private Passenger passenger;
	
	public Driver(String driverName, int maxSleep)
	{
		super(driverName, maxSleep);
	}
	
	/**
	 * Stores the provided passenger as the driver's current passenger and then
	 * sleeps the thread for between 0-maxDelay milliseconds.
	 * 
	 * @param newPassenger Passenger to collect
	 * @throws InterruptedException
	 */
	public void pickUpPassenger(Passenger newPassenger) throws InterruptedException
	{
		// Store provided passenger as driver's current passenger
		passenger = newPassenger;
		
		//Sleep thread for 0-maxDelay milliseconds
		Thread.sleep(generateWaitTime(maxSleep));
	}

	/**
	 * Sleeps the thread for the amount of time returned by the current 
	 * passenger's getTravelTime() function
	 * 
	 * @throws InterruptedException
	 */
	public void driveToDestination() throws InterruptedException{
		Thread.sleep(passenger.getTravelTime());
	}
	
	 public static int generateWaitTime(int max) {
	        Random rand = new Random();
	        return rand.nextInt(max+1);
	    }
	
}
