package com.beeva.formacion.netty.server;

import com.beeva.formacion.netty.handler.ByteBufServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by Beeva Architecture Team
 */
public class Server {

    public static void main(String[] args) throws InterruptedException{

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(8080)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ByteBufServerHandler());
                    }
                });

        try {
            ChannelFuture cf = b.bind().sync();
            cf.addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println("Server Up");
                }
            });

            cf.channel().closeFuture().sync();
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
