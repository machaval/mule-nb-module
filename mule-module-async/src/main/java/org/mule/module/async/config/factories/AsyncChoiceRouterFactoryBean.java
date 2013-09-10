/**
 *
 */
package org.mule.module.async.config.factories;

import org.mule.api.processor.MessageProcessor;
import org.mule.module.async.router.AsyncChoiceRouter;
import org.mule.routing.MessageProcessorFilterPair;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.springframework.beans.factory.FactoryBean;

public class AsyncChoiceRouterFactoryBean implements FactoryBean
{

    private MessageProcessor defaultProcessor;
    private Collection<MessageProcessorFilterPair> conditionalMessageProcessors;
    private final Map<QName, Object> annotations = new ConcurrentHashMap<QName, Object>();

    public AsyncChoiceRouterFactoryBean()
    {
        super();
    }

    public void setDefaultRoute(MessageProcessorFilterPair conditionalProcessor)
    {
        defaultProcessor = conditionalProcessor.getMessageProcessor();
    }

    public void setRoutes(Collection<MessageProcessorFilterPair> conditionalMessageProcessors)
    {
        this.conditionalMessageProcessors = conditionalMessageProcessors;
    }

    public Object getObject() throws Exception
    {
        final AsyncChoiceRouter router = new AsyncChoiceRouter();

        router.setDefaultRoute(defaultProcessor);

        for (final MessageProcessorFilterPair mpfp : conditionalMessageProcessors)
        {
            router.addRoute(mpfp.getMessageProcessor(), mpfp.getFilter());
        }

        return router;
    }

    @Override
    public Class<?> getObjectType()
    {
        return AsyncChoiceRouter.class;
    }


    public boolean isSingleton()
    {
        return true;
    }


}