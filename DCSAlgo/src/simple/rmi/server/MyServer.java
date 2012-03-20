package simple.rmi.server;
import java.math.BigDecimal;

import simple.rmi.common.ComputePi;
import simple.rmi.common.Pi;

/**
 * Simple "server" wrapper that calls the Pi computation in-process.
 * 
 * @author Jonathan Engelsma (http://themobilemontage.com)
 *
 */
public class MyServer implements ComputePi {

	public BigDecimal computePi(Pi val) {
		return val.execute();
	}

}
