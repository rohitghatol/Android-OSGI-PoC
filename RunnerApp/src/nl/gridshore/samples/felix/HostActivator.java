package nl.gridshore.samples.felix;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Created by IntelliJ IDEA.
 * User: jettro
 * Date: Feb 8, 2008
 * Time: 9:41:40 PM
 * Class to enable the host to communicate with the felix bundles
 */
public class HostActivator implements BundleActivator {
    private BundleContext context = null;

    public void start(BundleContext bundleContext) throws Exception {
        context = bundleContext;
    }

    public void stop(BundleContext bundleContext) throws Exception {
        context = null;
    }

    public Bundle[] getBundles() {
        Bundle[] bundles = null;
        if (context != null) {
            bundles = context.getBundles();
        }
        return bundles;
    }

}
