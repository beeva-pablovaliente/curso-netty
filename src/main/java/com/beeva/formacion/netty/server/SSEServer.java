package com.beeva.formacion.netty.server;

import com.beeva.formacion.netty.handler.SSEServerHandler;
import com.beeva.formacion.netty.model.Event;
import com.sun.management.OperatingSystemMXBean;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Beeva Architecture Team
 */
public class SSEServer {

    public static void main(String[] args) throws InterruptedException{

        CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build();

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

        //Connected channels to send events to
        Set<Channel> channels = new HashSet<>();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(8080)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LoggingHandler(LogLevel.INFO))
                                .addLast(new HttpRequestDecoder())
                                .addLast("httpResponseEncoder", new HttpResponseEncoder())
                                .addLast("corsHandler", new CorsHandler(corsConfig))
                                .addLast(new HttpObjectAggregator(1048576))
                                .addLast("sseServerHandler", new SSEServerHandler(channels));
                    }
                });

        try {
            ChannelFuture cf = b.bind().sync();
            cf.addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println("SSE Server Up");
                }
            });

            // Initialize the Scheduler for Profiling Events
            workerGroup.scheduleAtFixedRate(() -> {
                sendSSEProfilingEventToChannels(channels);
            }, 100, 1000, TimeUnit.MILLISECONDS);


            cf.channel().closeFuture().sync();
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    private static void sendSSEProfilingEventToChannels(Set<Channel> channels) {

        //CPU
        double cpu = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getSystemCpuLoad() * 100;
        Event eventCpu = new Event()
                .withEvent("cpu").withData(Double.toString(cpu));

        //Memory
        long free  = ((OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean()).getFreePhysicalMemorySize();
        long total = ((OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
        String res = String.format("{\"free\" : %s, \"total\" : %s}", free, total);

        Event eventMemory = new Event()
                .withEvent("memory")
                .withData(res);

        //Send the events to all SSE channels
        channels.parallelStream()
                .forEach(channel -> {
                    channel.write(Unpooled.copiedBuffer(eventCpu.toString(), Charset.forName("UTF-8"))).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                    channel.writeAndFlush(Unpooled.copiedBuffer(eventMemory.toString(), Charset.forName("UTF-8"))).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                });
    }

}
