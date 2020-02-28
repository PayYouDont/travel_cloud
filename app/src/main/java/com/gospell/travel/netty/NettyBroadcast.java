package com.gospell.travel.netty;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class NettyBroadcast {
    private Bootstrap bootstrap;
    private EventLoopGroup group = new NioEventLoopGroup ();
    private Channel channel;

    public NettyBroadcast(InetSocketAddress address) {
        bootstrap = new Bootstrap ();
        bootstrap.group (group).channel (NioDatagramChannel.class).option (ChannelOption.SO_BROADCAST, true).handler (new BroadcastChannelInitializer (address));
    }

    public void run(Object msg) {
        channel = bootstrap.bind (0).syncUninterruptibly ().channel ();
        System.out.println ("LogEventBroadcaster running");
        while (true) {
            System.out.println ("Client IP is " + channel);
            channel.writeAndFlush (msg);
            try {
                Thread.sleep (1000);
            } catch (InterruptedException e) {
                Thread.interrupted ();
                break;
            }
        }
    }

    public void destroy() {
        if (channel != null) channel.close ();
        group.shutdownGracefully ();
    }
}
