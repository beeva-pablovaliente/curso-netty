package com.beeva.formacion.netty.codecs;

import com.beeva.formacion.netty.model.Person;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

/**
 * Created by Beeva Architecture Team
 */
public class PersonToByteEncoder extends MessageToByteEncoder<Person> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Person msg, ByteBuf out) throws Exception {
        out.writeCharSequence(msg.getName() + "#"+msg.getAge().toString(), Charset.forName("UTF-8"));
    }
}
