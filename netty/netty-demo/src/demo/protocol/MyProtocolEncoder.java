package demo.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyProtocolEncoder extends MessageToByteEncoder<MyProtocol> {

	@Override
	protected void encode(ChannelHandlerContext ctx, MyProtocol msg,
			ByteBuf out) throws Exception {
		
        out.writeInt(msg.getHeadData());  
        out.writeInt(msg.getContentLength());  
        out.writeBytes(msg.getContent());
	}

}
