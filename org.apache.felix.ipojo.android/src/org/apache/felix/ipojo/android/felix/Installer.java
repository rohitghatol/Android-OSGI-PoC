package org.apache.felix.ipojo.android.felix;

import java.io.InputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import android.content.res.Resources;

public class Installer implements BundleActivator {

	public static final String IPOJO_PATH = "file:///ipojo.jar";
	public static final String INTERFACES_PATH = "file:///interfaces.jar";
	public static final String IMPL_PATH = "file:///impl.jar";
	public static final String CLIENT_PATH = "file:///client.jar";
	private Resources res;

	public Installer(Resources res) {
		this.res = res;
	}

	@Override
	public void start(BundleContext arg0) throws Exception {

		InputStream is0 = res.openRawResource(R.raw.ipojo);
		System.out.println("---> Copied ipojo.jar");
		Bundle bundle0 = arg0.installBundle(IPOJO_PATH, is0);
		System.out.println("--->Installed ipojo Bundle");
		bundle0.start();
		System.out.println("--->Started ipojo  Bundle");

		InputStream is1 = res.openRawResource(R.raw.interfaces);
		System.out.println("---> Copied interfaces.jar");
		Bundle bundle1 = arg0.installBundle(INTERFACES_PATH, is1);
		System.out.println("--->Installed interfaces Bundle");
		bundle1.start();
		System.out.println("--->Started interfaces  Bundle");

//		InputStream is2 = res.openRawResource(R.raw.impl);
//		System.out.println("---> Copied impl.jar");
//		Bundle bundle2 = arg0.installBundle(IMPL_PATH, is2);
//		System.out.println("--->Installed impl Bundle");
//		bundle2.start();
//		System.out.println("--->Started impl  Bundle");
//
//		InputStream is3 = res.openRawResource(R.raw.client);
//		System.out.println("---> Copied client.jar");
//		Bundle bundle3 = arg0.installBundle(CLIENT_PATH, is3);
//		System.out.println("--->Installed client Bundle");
//		bundle3.start();
//		System.out.println("--->Start client  Bundle");

	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		// TODO Auto-generated method stub

	}

}
