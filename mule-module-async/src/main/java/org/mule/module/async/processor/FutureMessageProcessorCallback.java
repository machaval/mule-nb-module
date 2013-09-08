/**
 *
 */
package org.mule.module.async.processor;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;

public class FutureMessageProcessorCallback implements MessageProcessorCallback
{

    private final MuleEventFuture future;

    public FutureMessageProcessorCallback(MuleEventFuture future)
    {
        this.future = future;
    }

    @Override
    public void onSuccess(MuleEvent event)
    {
        future.set(event);
    }

    @Override
    public void onException(MuleEvent event, MuleException e)
    {
        future.set(e);
    }
}
