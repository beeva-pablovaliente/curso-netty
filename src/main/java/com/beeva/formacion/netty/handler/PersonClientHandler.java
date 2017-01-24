package com.beeva.formacion.netty.handler;

import com.beeva.formacion.netty.model.Person;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;

/**
 * Created by Beeva Architecture Team
 */
public class PersonClientHandler extends SimpleChannelInboundHandler<Person>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Person msg) throws Exception {
        System.out.println("Nombre: " + msg.getName());
        System.out.println("Edad: " + msg.getAge());
    }
}
