package com.ahei.gonetty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {
	public static void main(String[] args) {
		String host = "106.13.103.14";
		int port = 8080;
		
		EventLoopGroup workGroup = new NioEventLoopGroup();
		Bootstrap boot = new Bootstrap();
		boot.group(workGroup)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.SO_KEEPALIVE, true)
			.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new TimeClientHandler());
				}
			});

		try {
		// Start the client
			ChannelFuture cf = boot.connect(host, port).sync();
			System.out.println("Connection is established");

			cf.channel()
			.closeFuture()
			.sync();
			System.out.println("Server shutdown");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			workGroup.shutdownGracefully();
		}
	}
}
