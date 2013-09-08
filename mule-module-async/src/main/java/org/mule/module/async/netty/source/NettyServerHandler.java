package org.mule.module.async.netty.source;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.async.MuleEventFactory;
import org.mule.module.async.processor.MessageProcessorCallback;
import org.mule.module.async.processor.AsyncMessageProcessor;
import org.mule.transport.http.HttpConnector;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;

public class NettyServerHandler extends SimpleChannelUpstreamHandler
{


    private AsyncMessageProcessor asyncMessageProcessor;

    protected MuleEventFactory muleEventFactory = null;

    public NettyServerHandler(MuleEventFactory muleEventFactory, AsyncMessageProcessor asyncMessageProcessor)
    {
        this.muleEventFactory = muleEventFactory;
        this.asyncMessageProcessor = asyncMessageProcessor;
    }


    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent event)
    {
        final HttpRequest request = (HttpRequest) event.getMessage();

        System.out.println("NettyServerHandler.messageReceived");

        final Channel channel = event.getChannel();
        try
        {
            final MuleEvent muleEvent = muleEventFactory.create(request, Charset.defaultCharset().name());
            asyncMessageProcessor.process(muleEvent, new MessageProcessorCallback()
            {
                @Override
                public void onSuccess(MuleEvent result)
                {
                    try
                    {
                        final MuleMessage message = result.getMessage();
                        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                        response.setHeader("Content-Type", message.getOutboundProperty("Content-Type"));
                        Object outboundProperty = message.getOutboundProperty(HttpConnector.HTTP_HEADERS);
                        //if (outboundProperty instanceof Map)
                        //{
                        //    Set<Map.Entry<String, Object>> entries = ((Map<String, Object>) outboundProperty).entrySet();
                        //    for (Map.Entry<String, Object> entry : entries)
                        //    {
                        //        response.setHeader(entry.getKey(), entry.getValue());
                        //    }
                        //}
                        response.setContent(ChannelBuffers.copiedBuffer(message.getPayload().toString(), CharsetUtil.ISO_8859_1));
                        channel.write(response).addListener(ChannelFutureListener.CLOSE);
                    }
                    catch (Exception e)
                    {
                        //Todo Handle exception
                        e.printStackTrace();
                    }

                }

                @Override
                public void onException(MuleEvent event, MuleException e)
                {
                    channel.close();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
        System.out.println("NettyServerHandler.messageReceived + finished");

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception
    {
        System.out.println("NettyServerHandler.exceptionCaught");
        e.getCause().printStackTrace();
    }
}

