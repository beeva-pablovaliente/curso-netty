package com.beeva.formacion.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import java.util.Set;

/**
 * Created by Architecture Team
 */
public class SSEServerHandler extends ChannelInboundHandlerAdapter {

    private Set<Channel> channels;

    public SSEServerHandler(Set<Channel> channels) {
        this.channels = channels;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.channels.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.channels.remove(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        FullHttpRequest in = (FullHttpRequest) msg;

        if (in.method().equals(HttpMethod.GET)
                && in.headers().get(HttpHeaderNames.ACCEPT).equals("text/event-stream")
                && in.uri().matches("/stream(/.*)?")) {

            initializeSSEConnection(ctx);
        }
        else{
            ctx.channel().writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND))
                    .addListener(ChannelFutureListener.CLOSE);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Exception Caught: " + cause.getMessage());
        ctx.channel().close();
    }

    private void initializeSSEConnection(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK
        );
        //Accept event stream
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream");
        String cacheControlValue =
                HttpHeaderValues.NO_CACHE + ","
                        + HttpHeaderValues.NO_STORE + ","
                        + HttpHeaderValues.MAX_AGE + "=0,"
                        + HttpHeaderValues.MUST_REVALIDATE;
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, cacheControlValue);
        response.headers().set(HttpHeaderNames.PRAGMA, HttpHeaderValues.NO_CACHE);

        //Write the Accept Response
        ctx.writeAndFlush(response).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()){
                //Mutate the channel to send ByteBuf
                future.channel().pipeline().remove("httpResponseEncoder");
                future.channel().pipeline().remove("corsHandler");
            }
            else {
                future.channel().pipeline().fireExceptionCaught(future.cause());
            }
        });


    }
}
