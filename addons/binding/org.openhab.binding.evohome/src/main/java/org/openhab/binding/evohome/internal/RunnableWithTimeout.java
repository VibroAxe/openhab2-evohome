package org.openhab.binding.evohome.internal;

import java.util.concurrent.TimeoutException;

/**
 * Provides an interface for a delegate that can throw a timeout
 * 
 * @author Jasper van Zuijlen - Initial contribution
 *
 */
public interface RunnableWithTimeout {

    public abstract void run() throws TimeoutException;

}
