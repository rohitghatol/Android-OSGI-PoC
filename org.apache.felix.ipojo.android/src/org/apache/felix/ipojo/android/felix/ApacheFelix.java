package org.apache.felix.ipojo.android.felix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.framework.util.StringMap;
import org.apache.felix.ipojo.android.felix.view.ViewFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ApacheFelix extends Activity {
	public static final String FELIX_BUNDLES_DIR = "/sdcard/bundles";

	private static final String ANDROID_FRAMEWORK_PACKAGES = ("org.osgi.framework; version=1.4.0," +
	            "org.osgi.service.packageadmin; version=1.2.0," +
	            "org.osgi.service.startlevel; version=1.0.0," +
	            "org.osgi.service.url; version=1.0.0," +
	            "org.osgi.util.tracker," +
	            "android; " + 
	            "android.app;" + 
	            "android.content;" + 
	            "android.database;" + 
	            "android.database.sqlite;" + 
	            "android.graphics; " + 
	            "android.graphics.drawable; " + 
	            "android.graphics.glutils; " + 
	            "android.hardware; " + 
	            "android.location; " + 
	            "android.media; " + 
	            "android.net; " + 
	            "android.opengl; " + 
	            "android.os; " + 
	            "android.provider; " + 
	            "android.sax; " + 
	            "android.speech.recognition; " + 
	            "android.telephony; " + 
	            "android.telephony.gsm; " + 
	            "android.text; " + 
	            "android.text.method; " + 
	            "android.text.style; " + 
	            "android.text.util; " + 
	            "android.util; " + 
	            "android.view; " + 
	            "android.view.animation; " + 
	            "android.webkit; " + 
	            "android.widget; " + 
	            "com.google.android.maps; " + 
	            "com.google.android.xmppService; " + 
	            "javax.crypto; " + 
	            "javax.crypto.interfaces; " + 
	            "javax.crypto.spec; " + 
	            "javax.microedition.khronos.opengles; " + 
	            "javax.net; " + 
	            "javax.net.ssl; " + 
	            "javax.security.auth; " + 
	            "javax.security.auth.callback; " + 
	            "javax.security.auth.login; " + 
	            "javax.security.auth.x500; " + 
	            "javax.security.cert; " + 
	            "javax.sound.midi; " + 
	            "javax.sound.midi.spi; " + 
	            "javax.sound.sampled; " + 
	            "javax.sound.sampled.spi; " + 
	            "javax.sql; " + 
	            "javax.xml.parsers; " + 
	            "junit.extensions; " + 
	            "junit.framework; " + 
	            "org.apache.commons.codec; " + 
	            "org.apache.commons.codec.binary; " + 
	            "org.apache.commons.codec.language; " + 
	            "org.apache.commons.codec.net; " + 
	            "org.apache.commons.httpclient; " + 
	            "org.apache.commons.httpclient.auth; " + 
	            "org.apache.commons.httpclient.cookie; " + 
	            "org.apache.commons.httpclient.methods; " + 
	            "org.apache.commons.httpclient.methods.multipart; " + 
	            "org.apache.commons.httpclient.params; " + 
	            "org.apache.commons.httpclient.protocol; " + 
	            "org.apache.commons.httpclient.util; " + 
	            "org.bluez; " + 
	            "org.json; " + 
	            "org.w3c.dom; " + 
	            "org.xml.sax; " + 
	            "org.xml.sax.ext; " + 
	            "org.xml.sax.helpers; " + 
	            "version=1.0.0.m5-r15," +
	            "org.apache.felix.ipojo.android.felix.view").intern();
	
	private File m_cache;
	private Felix m_felix;
	private StringMap m_configMap;
	private ServiceTracker m_tracker; 
	
    /** Called when the activity is first created. 
     * @throws IOException */
    @Override
    public synchronized void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        PrintStream out = new PrintStream(new OutputStream(){
        	ByteArrayOutputStream output = new ByteArrayOutputStream();
			@Override
			public void write(int oneByte) throws IOException {
				output.write(oneByte);
				if (oneByte == '\n') {
					Log.v("out", new String(output.toByteArray()));
					output = new ByteArrayOutputStream();
				}
			}});
        System.setErr(out);
        System.setOut(out);
        m_configMap = new StringMap(false);
        m_configMap.put(FelixConstants.LOG_LEVEL_PROP, String.valueOf(Logger.LOG_DEBUG));

        // Configure the Felix instance to be embedded.
        m_configMap.put(FelixConstants.EMBEDDED_EXECUTION_PROP, "true");
        File bundles = new File(FELIX_BUNDLES_DIR);
        if (!bundles.exists()) {
        	if (!bundles.mkdirs()) {
        		throw new IllegalStateException("Unable to create bundles dir");
        	}
        }
        // Add core OSGi packages to be exported from the class path
        // via the system bundle.
        m_configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES, ANDROID_FRAMEWORK_PACKAGES);
        // Explicitly specify the directory to use for caching bundles.
		try {
			m_cache = File.createTempFile("felix-cache", null);
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
        m_cache.delete();
        m_cache.mkdirs();
        m_configMap.put(BundleCache.CACHE_PROFILE_DIR_PROP, m_cache.getAbsolutePath());
    }
    
    @Override
    public synchronized void onStart() {
    	super.onStart();
		setContentView(new View(this));
		Resources res = getResources();
    	try {
    		List<BundleActivator> activators = new ArrayList<BundleActivator>();
    		
    		// Install the bundle installer
            activators.add(new Installer(res));
    		

    		m_felix = new Felix(m_configMap, activators);
    		m_felix.start();
		} catch (BundleException ex) {
			throw new IllegalStateException(ex);
		}
		try {
			m_tracker = new ServiceTracker(m_felix.getBundleContext(), 
					m_felix.getBundleContext().createFilter("(" + Constants.OBJECTCLASS + "=" + ViewFactory.class.getName() + ")"), 
						new ServiceTrackerCustomizer() {

						@Override
						public Object addingService(ServiceReference ref) {
						    System.out.println("======= Service found !");
							final ViewFactory fac = (ViewFactory) m_felix.getBundleContext().getService(ref);
							if (fac != null) {
								runOnUiThread(new Runnable() {
									public void run() {
										setContentView(fac.create(ApacheFelix.this));
									}
								});
							}	
							return fac;
						}

						@Override
						public void modifiedService(ServiceReference ref,
								Object service) {
							// TODO Auto-generated method stub
							removedService(ref, service);
							addingService(ref);
						}

						@Override
						public void removedService(ServiceReference ref,
								Object service) {
							m_felix.getBundleContext().ungetService(ref);
							// TODO Auto-generated method stub
							runOnUiThread(new Runnable() {
								public void run() {
									setContentView(new View(ApacheFelix.this));
								}
							});
						}});
			m_tracker.open();
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
    public synchronized void onStop() {
        System.out.println("============= ON STOP ==========");
    	super.onStop();
    	m_tracker.close();
    	m_tracker = null;
    	m_felix.stopAndWait();
    	m_felix = null;
    }
    
    @Override
    public synchronized void onDestroy() {
    	super.onDestroy();
        System.out.println("============= ON DESTROY ==========");

    	delete(m_cache);
    	m_configMap = null;
    	m_cache = null;
    }
    
    private void delete(File target) {
    	if (target.isDirectory()) {
    		for (File file : target.listFiles()) {
    			delete(file);
    		}
    	}
    	target.delete();
    }
}