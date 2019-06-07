package com.ahei.gonetty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
	private static final Logger logger = LoggerFactory.getLogger(Server.class);
	private int port;

	public Server(int port) {
		this.port = port;
	}

	public void run() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		ServerBootstrap boot = new ServerBootstrap();
		boot.group(bossGroup, workGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ServerInitializer())
			.option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE, true);
		try {
			ChannelFuture cf = boot.bind(port).sync();
			logger.info("netty is starting");

			// Wait until the server socket is closed.
			cf.channel().closeFuture().sync();
			System.out.println("netty is down");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			workGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}
