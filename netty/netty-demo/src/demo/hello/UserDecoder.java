package demo.hello;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class UserDecoder<T> extends ByteToMessageDecoder {

    private final Class<T> clazz;

    public UserDecoder(Class<T> clazz) {
        this.clazz = clazz;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
        ByteBufInputStream byteBufInputStream = new ByteBufInputStream(in);
		out.add(new ObjectMapper().readValue(byteBufInputStream, clazz));
	}

}