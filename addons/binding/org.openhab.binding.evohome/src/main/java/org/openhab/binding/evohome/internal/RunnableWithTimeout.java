package org.openhab.binding.evohome.internal;

import java.util.concurrent.TimeoutException;

public interface RunnableWithTimeout {

    public abstract void run() throws TimeoutException;

}
