package com.gospell.travel.netty;

import java.net.InetSocketAddress;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class BroadcastChannelInitializer extends ChannelInitializer<Channel> {
    private InetSocketAddress address;

    public BroadcastChannelInitializer(InetSocketAddress address) {
        this.address = address;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline ().addLast (new StringDecoder (CharsetUtil.UTF_8));
        channel.pipeline ().addLast (new StringEncoder (CharsetUtil.UTF_8));
        channel.pipeline ().addLast (new MessageToMessageEncoder<String> () {
            @Override
            protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) {
                byte[] bytes = msg.getBytes (CharsetUtil.UTF_8);
                ByteBuf buf = ctx.alloc ().buffer (bytes.length).writeBytes (bytes);
                out.add (new DatagramPacket (buf, address));
            }
        });
    }
}
