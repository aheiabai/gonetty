package com.ahei.gonetty;

import java.util.HashMap;
import java.util.Map;

import org.ehcache.Cache;
import org.ehcache.core.statistics.CacheStatistics;
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
//	private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private String channelId;
	private static final Map<String, Channel> CHANNEL_MAP = new HashMap<>();

	private static final Cache<String, String> cache = App.CACHE_MANAGER.createCache("idCache", App.cacheConfig);
	CacheStatistics stats = App.statisService.getCacheStatistics("idCache");

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		channels.add(ctx.channel());
//		channelId = ((NioSocketChannel)ctx.channel()).remoteAddress().hashCode();
		String host = ((NioSocketChannel) ctx.channel()).remoteAddress().getHostString();
		int port = ((NioSocketChannel) ctx.channel()).remoteAddress().getPort();
//		logger.debug("host: {}, port: {}", host, port);

//		logger.debug("New channelId {}, remaining channels {}", channelId, CHANNEL_MAP.size());
		logger.info("new channel --> address: {}:{}", host, port);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		if ("quit".equals(msg)) {
			cache.remove(channelId);
			ctx.close();
//			channels.remove(ctx.channel());
//			System.out.println(channelId + " is leaving, remaining channels " + channels.size());
//			logger.debug(channelId + " is leaving, remaining channels " + channels.size());
		} else if (msg.startsWith("--UID ")) {// keep user for 2 hour
			channelId = msg.substring(6, msg.length());
			if (cache.containsKey(channelId)) {
				cache.put(channelId, "");
				CHANNEL_MAP.put(channelId, ctx.channel());
				logger.debug("{} reconnect, cache size {}", channelId, stats.getTierStatistics().get("OnHeap").getMappings());
			} else {
				channelId = NameGenerator.generator();
				CHANNEL_MAP.put(channelId, ctx.channel());
				cache.put(channelId, "");
				ctx.writeAndFlush("Welcome " + channelId + "!\r\n");
				logger.debug("New User {}, remaining channels {}, cache size {}", channelId, CHANNEL_MAP.size(),
						stats.getTierStatistics().get("OnHeap").getMappings());
			}
			
			// broadcast online number
			for (Channel c : CHANNEL_MAP.values()) {
				c.writeAndFlush("Online " + CHANNEL_MAP.size() + "\r\n");
			}

		} else {// broadcast msg
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
