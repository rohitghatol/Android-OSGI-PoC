/**
 * 
 */
package com.test.client;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.test.service.Greetings;
/**
 * @author rohit
 *
 */
public class Client implements BundleActivator {

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext ctx) throws Exception {
		System.out.println("Client ->start()");
		String name = Greetings.class.getName();
		System.out.println("-->"+name);
		ServiceReference ref =
			ctx.getServiceReference(name);
		if(null!=ref){
		 String message= ((Greetings) ctx.getService(ref)).sayHello();
		 System.out.println(message);
		}
		else{
			System.out.println("Context is Null ");
		}
		ctx.ungetService(ref);
		
		

	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext arg0) throws Exception {
		System.out.println("Client -> stop()");

	}

}
