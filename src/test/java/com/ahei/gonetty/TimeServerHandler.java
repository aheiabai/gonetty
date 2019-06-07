package com.ahei.gonetty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		final ByteBuf bbuf = ctx.alloc().buffer(4);
		bbuf.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
		
		// ByteBuf does not have a flip() method, instead it keeps two pointers each for read and write operations
		final ChannelFuture cf = ctx.writeAndFlush(bbuf);
		cf.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				System.out.println("Channel close");
				ctx.close();
			}
		});
		// add a pre-defined listener
//		cf.addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
