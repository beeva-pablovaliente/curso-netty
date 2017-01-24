package com.beeva.formacion.netty.handler;

import com.beeva.formacion.netty.model.Person;
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

        Person p = new Person();
        p.setName(msg.retain().toString(Charset.forName("UTF-8")));
        p.setAge(20);

        ctx.writeAndFlush(p).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Exception Caught: " + cause.getMessage());
        ctx.channel().close();
    }
}
