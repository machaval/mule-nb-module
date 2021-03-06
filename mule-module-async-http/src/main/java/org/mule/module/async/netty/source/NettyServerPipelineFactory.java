package org.mule.module.async.netty.source;

import static org.jboss.netty.channel.Channels.*;

import org.mule.module.async.internal.MuleEventFactory;
import org.mule.module.async.processor.AsyncMessageProcessor;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

public class NettyServerPipelineFactory implements ChannelPipelineFactory
{

    private AsyncMessageProcessor asyncMessageProcessor;
    private MuleEventFactory muleEventFactory;

    public NettyServerPipelineFactory(AsyncMessageProcessor asyncMessageProcessor, MuleEventFactory muleEventFactory)
    {
        this.asyncMessageProcessor = asyncMessageProcessor;
        this.muleEventFactory = muleEventFactory;
    }

    public ChannelPipeline getPipeline() throws Exception
    {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

        // Uncomment the following line if you want HTTPS
        //SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
        //engine.setUseClientMode(false);
        //pipeline.addLast("ssl", new SslHandler(engine));

        pipeline.addLast("decoder", new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        //pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content compression.
        pipeline.addLast("deflater", new HttpContentCompressor());
        pipeline.addLast("handler", new NettyServerHandler(muleEventFactory, asyncMessageProcessor));
        return pipeline;
    }
}