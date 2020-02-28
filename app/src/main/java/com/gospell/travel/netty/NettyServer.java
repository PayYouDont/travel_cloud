
package com.gospell.travel.netty;

import android.util.Log;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author peiyongdong
 * @ClassName: NettyServer
 * @Description: TODO()
 * @date 2018年9月20日 下午4:30:07
 */
public class NettyServer {
    private final EventLoopGroup bossGroup = new NioEventLoopGroup ();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup ();

    private Channel channel;

    /**
     * 启动服务
     */
    public ChannelFuture run(InetSocketAddress address) {
        ChannelFuture f = null;
        try {
            ServerBootstrap bootstrap = new ServerBootstrap ();
            bootstrap.group (bossGroup, workerGroup).channel (NioServerSocketChannel.class)
                    .childHandler (new ServerChannelInitializer ()).option (ChannelOption.SO_BACKLOG, 128)
                    .childOption (ChannelOption.SO_KEEPALIVE, true);
            f = bootstrap.bind (address).syncUninterruptibly ();
            channel = f.channel ();
        } catch (Exception e) {
            Log.e (getClass ().getName (), e.getMessage (),e);
        } finally {
            if (f != null && f.isSuccess ()) {
                Log.d (getClass ().getName (), "Netty server listening " + address.getHostName () + " on port " + address.getPort () + " and ready for connections...");
            } else {
                Log.d (getClass ().getName (), "Netty server start up Error!");
            }
        }

        return f;
    }

    public void destroy() {
        Log.d (getClass ().getName (), "Shutdown Netty Server...");
        if (channel != null) {
            channel.close ();
        }
        workerGroup.shutdownGracefully ();
        bossGroup.shutdownGracefully ();
        Log.d (getClass ().getName (), "Shutdown Netty Server Success!");
    }
}
