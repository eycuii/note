package demo.protocol;

import java.util.Date;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class ProtocolServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		System.out.println("server "+new Date());
        // 添加自定义协议的编解码工具
        ch.pipeline().addLast(new MyProtocolEncoder());
        ch.pipeline().addLast(new MyProtocolDecoder());
        // 处理网络IO
        ch.pipeline().addLast(new ProtocolServerHandler());
    }

}
