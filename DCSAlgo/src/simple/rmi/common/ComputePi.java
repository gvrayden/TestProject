package simple.rmi.common;

import java.math.BigDecimal;

/**
 * Abstract interface definition for the server.
 * 
 * @author J. Engelsma (http://themobilemontage.com)
 *
 */
public interface ComputePi {
	BigDecimal computePi(Pi val);
}
