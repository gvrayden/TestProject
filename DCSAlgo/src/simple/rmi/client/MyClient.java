package simple.rmi.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import simple.rmi.common.Pi;
import simple.rmi.server.MyServer;

/**
 * Simple client that prompts the user for input and computes PI accordingly.
 * 
 * @author Jonathan Engelsma (http://themobilemontage.com)
 *
 */
public class MyClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		boolean done = false;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		// allocate an instance to our server wrapper.
		MyServer theServer = new MyServer();

		// prompt the user for the number of decimal points, and then compute PI accordingly.
		try {
			while (!done) {
				System.out.print("Compute PI to how many decimal points?");
				String command = reader.readLine();
				if(command==null || command.equalsIgnoreCase("quit")) {
					done = true;
				} else {
					try {
						int digits = Integer.parseInt(command);
						Pi piComputation = new Pi(digits);	
						BigDecimal theResult = theServer.computePi(piComputation);
						System.out.println("Congratulations!  The answer is: " + theResult);
					} catch (NumberFormatException e) {
						System.out.println("Whoops, that was not a valid integer value.  Please try again.");
					}

				}
			}
		} catch (IOException e) {
			System.out.println("Uh oh, something bad happened.  We better get out of here.");
		}
	}

}
