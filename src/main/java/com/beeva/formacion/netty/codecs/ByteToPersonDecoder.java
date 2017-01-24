package com.beeva.formacion.netty.codecs;

import com.beeva.formacion.netty.model.Person;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by Beeva Architecture Team
 */
public class ByteToPersonDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        String message = in.readCharSequence(30, Charset.forName("UTF-8")).toString();

        Person p = new Person();
        p.setName(message.split("#")[0])
         .setAge(Integer.valueOf(message.split("#")[1].trim()));

        out.add(p);
    }
}
