package nuber.students;

import java.util.HashMap;

public class AssignmentDriver {

	
	public static void main(String[] args) throws Exception {

		//turn this or off to enable/disable output from the dispatch's logEvent function
		//use the logEvent function to print out debug output when required.
		boolean logEvents = true;
		
		HashMap<String, Integer> testRegions = new HashMap<String, Integer>();
		testRegions.put("Test Region", 50);
		

		
		/**
		 * This driver has a number of different sections that you can uncomment as you progress through the assignment
		 * Once you have completed all parts, you should be able to run this entire function uncommented successfully
		 */

		Passenger testPassenger = new Passenger("Alex", 100);

		Driver testDriver = new Driver("Barbara", 100);
		try {
			//should store the passenger, and then sleep the thread for as long as the driver's random timeout takes
			testDriver.pickUpPassenger(testPassenger);

			//should sleep the thread for as long as the passenger's random timeout takes
			testDriver.driveToDestination();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//test creating a dispatch object
		NuberDispatch dispatch = new NuberDispatch(testRegions, logEvents);
		
		//create two new bookings
		Booking b1 = new Booking(dispatch, testPassenger);
		Booking b2 = new Booking(dispatch, testPassenger);
		
		//test creating a new region
		NuberRegion region = new NuberRegion(dispatch, "Test Region", 10);

		//test adding a driver to dispatch
		dispatch.addDriver(testDriver);
		
		//test booking a single passenger
		dispatch.bookPassenger(testPassenger, "Test Region");

		//shutdown the dispatch when it's done
		dispatch.shutdown();
		
		// Test booking a passenger after dispatch has been shut down
		dispatch.bookPassenger(testPassenger, "Test Region");

		
		
		
		
		//create NuberDispatch for given regions and max simultaneous jobs per region
		//once you have the above running, you should be able to uncomment the Simulations below to start to put everything together
		
		HashMap<String, Integer> regions = new HashMap<String, Integer>();
		regions.put("North", 100);
		regions.put("South", 100);
		
		//new Simulation(regions, 1, 10, 1000, logEvents); // 1 driver
		new Simulation(regions, 5, 10, 2000, logEvents); // 5 drivers
		//new Simulation(regions, 10, 10, 1000, logEvents); // same drivers as passengers
		//new Simulation(regions, 10, 100, 1000, logEvents); // significantly more drivers than passengers
		//new Simulation(regions, 1, 50, 500, logEvents); // 1 driver attempting 50 passengers
		//new Simulation(regions, 5, 10, 10000, logEvents); // loooong delays
		//new Simulation(regions, 5, 5000, 2, false); // short delay & lots more passengers than drivers
		//new Simulation(regions, 0, 2, 20, logEvents); // no drivers
		//new Simulation(regions, 2000, 2, 20, logEvents); // lots of drivers and only a couple of passengers
		//new Simulation(regions, 400, 2000, 500, false); // more drivers than area limits
	}

}
