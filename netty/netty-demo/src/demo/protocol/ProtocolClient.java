package demo.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;

public class ProtocolClient {
    
    public static String host = "127.0.0.1";
    public static int port = 7878;

    /**
     * @param args
     * @throws InterruptedException 
     * @throws IOException 
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
            .channel(NioSocketChannel.class)
            .handler(new ProtocolClientInitializer());

            // 连接服务端
            Channel ch = b.connect(host, port).sync().channel();
            
            String str = "hello world~";
            MyProtocol mp = new MyProtocol(str.getBytes().length, str.getBytes());
            ch.writeAndFlush(mp);
            // 等待连接关闭
         	ch.closeFuture().sync();
        } finally {
            // The connection is closed automatically on shutdown.
            group.shutdownGracefully();
        }
    }
}