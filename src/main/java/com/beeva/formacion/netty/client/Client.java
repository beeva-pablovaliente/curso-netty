package com.beeva.formacion.netty.client;

import com.beeva.formacion.netty.handler.ByteBufClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;

/**
 * Created by Beeva Architecture Team
 */
public class Client {

    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup group = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress("localhost", 8080)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ByteBufClientHandler());
                    }
                });

        try{
            Channel channel = b.connect().sync().channel();

            channel.writeAndFlush(Unpooled.copiedBuffer("Hola Soy El Cliente", Charset.forName("UTF-8")))
                    .addListener(future -> {
                        System.out.println("Enviado: " + channel.id());
                        System.out.println(future.isSuccess());
                    });

            channel.closeFuture().sync();
        }
        finally {
            group.shutdownGracefully();
        }

    }

}
