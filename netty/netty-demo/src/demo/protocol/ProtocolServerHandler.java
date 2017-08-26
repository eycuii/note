package demo.protocol;

import java.net.InetAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ProtocolServerHandler extends SimpleChannelInboundHandler<Object> {
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if (msg instanceof MyProtocol) {
	        System.out.println(ctx.channel().remoteAddress() + " say : " + (MyProtocol) msg);
    		String str = "Received your message";
	        MyProtocol mp = new MyProtocol(str.getBytes().length, str.getBytes());
	        ctx.writeAndFlush(mp);
    	}
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("RemoteAddress : " + ctx.channel().remoteAddress() + " active !");
        ctx.writeAndFlush( "Welcome to " + InetAddress.getLocalHost().getHostName() + " service!\n");
        
        super.channelActive(ctx);
    }
}