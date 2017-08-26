package demo.hello;

import java.net.InetAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class HelloServerHandler extends SimpleChannelInboundHandler<Object> {
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if (msg instanceof User) {
	        User user = (User) msg;
	        // 收到消息直接打印输出
	        System.out.println(ctx.channel().remoteAddress() + " Say : " + user);
	        // 返回客户端消息 - 我已经接收到了你的消息
	        //ctx.writeAndFlush("Received your message !\n");
	        user.setId(321);
	        user.setName("cba");
	        ctx.writeAndFlush(user);
    	}
    }
    
    /*
     * 
     * 覆盖 channelActive 方法 在channel被启用的时候触发 (在建立连接的时候)
     * 
     * channelActive 和 channelInActive 在后面的内容中讲述，这里先不做详细的描述
     * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("RemoteAddress : " + ctx.channel().remoteAddress() + " active !");
        ctx.writeAndFlush( "Welcome to " + InetAddress.getLocalHost().getHostName() + " service!\n");
        
        super.channelActive(ctx);
    }
}