package com.beeva.formacion.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;

/**
 * Created by Architecture Team
 */
public class ByteBufServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println(msg.toString(Charset.forName("UTF-8")));

        ctx.writeAndFlush(msg.alloc()).addListener(ChannelFutureListener.CLOSE);
    }
}
