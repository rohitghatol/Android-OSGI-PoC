/**
 * 
 */
package com.test.serviceimpl1;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import com.test.service.Greetings;
/**
 * @author rohit
 *
 */
public class Activator implements BundleActivator {

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext ctx) throws Exception {
		System.out.println("Impl ->start()");
		ctx.registerService(Greetings.class.getName(),
				new GreetingsImpl(), null);
		System.out.println("Impl <-start()");

	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext arg0) throws Exception {
		System.out.println("Impl -> stop()");

	}

}
