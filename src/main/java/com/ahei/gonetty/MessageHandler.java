package com.ahei.gonetty;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

public class MessageHandler extends SimpleChannelInboundHandler<String> {
	private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
	private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private String channelId;
	private static final Map<String, Channel> CHANNEL_MAP = new HashMap<>();

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		channels.add(ctx.channel());
//		channelId = ((NioSocketChannel)ctx.channel()).remoteAddress().hashCode();
//		String host = ((NioSocketChannel)ctx.channel()).remoteAddress().getHostString();
//		int port = ((NioSocketChannel)ctx.channel()).remoteAddress().getPort();
//		logger.debug("host: {}, port: {}", host, port);

		channelId = NameGenerator.generator();
		CHANNEL_MAP.put(channelId, ctx.channel());
		logger.debug("New channelId {}, remaining channels {}", channelId, CHANNEL_MAP.size());
		ctx.writeAndFlush("Welcome " + channelId + "!\r\n");

		for (Channel c : CHANNEL_MAP.values()) {
			c.writeAndFlush("Online " + CHANNEL_MAP.size() + "\r\n");
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		if ("quit".equals(msg)) {
			ctx.close();
//			channels.remove(ctx.channel());
//			System.out.println(channelId + " is leaving, remaining channels " + channels.size());
//			logger.debug(channelId + " is leaving, remaining channels " + channels.size());
		} else {
			for (Channel c : CHANNEL_MAP.values()) {
				c.writeAndFlush(channelId + " say " + msg + "\r\n");
			}
		}
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		CHANNEL_MAP.remove(channelId);
		logger.debug("{} is unregistered, remaining channels {}", channelId, CHANNEL_MAP.size());

		for (Channel c : CHANNEL_MAP.values()) {
			c.writeAndFlush("Online " + CHANNEL_MAP.size() + "\r\n");
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.debug(cause.getMessage());
		ctx.close();
	}

}
