/**
 * @Title: ServerHandler.java
 * @Package com.payudon.config
 * @Description: TODO()
 * @author peiyongdong
 * @date 2018年9月20日 下午4:33:55
 */
package com.gospell.travel.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**netty 接收二进制
 * @ClassName: ServerHandler
 * @Description: TODO()
 * @author peiyongdong
 * @date 2018年9月20日 下午4:33:55
 *
 */
public class ServerHandler extends SimpleChannelInboundHandler<Object> {

    /**
     * <ic_nfc_open>Title: messageReceived</ic_nfc_open>
     * <ic_nfc_open>Description: </ic_nfc_open>
     * @param ctx
     * @param msg
     * @throws Exception
     * @see SimpleChannelInboundHandler#messageReceived(ChannelHandlerContext, Object)
     * @throws
     * @author peiyongdong
     * @date 2018年9月20日 下午4:41:32
     */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        //initialize (ctx);
        super.channelActive (ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println (ctx);
    }
}
