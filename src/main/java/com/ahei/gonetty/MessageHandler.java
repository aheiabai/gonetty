package com.ahei.gonetty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class MessageHandler extends SimpleChannelInboundHandler<String>{
	private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private String channelId;
//	private static final Map<Integer, String> CHANNEL_NAME= new HashMap<>();

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		channels.add(ctx.channel());
//		channelId = ((NioSocketChannel)ctx.channel()).remoteAddress().hashCode();
		channelId = NameGenerator.generator();
		ctx.writeAndFlush("Welcome " + channelId + "!\r\n");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		if("quit".equals(msg)) {
			ctx.close();
			channels.remove(ctx.channel());
			System.out.println(channelId + " is leaving, remaining channels " + channels.size());
		}else {
			for(Channel c: channels) {
				c.writeAndFlush(channelId + " say " + msg + "\r\n");
			}
		}
	}
	
	
	/*
	 * @Override public void channelReadComplete(ChannelHandlerContext ctx) throws
	 * Exception { ctx.flush(); }
	 */	 

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

	

}
