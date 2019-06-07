package com.ahei.gonetty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel>{
	public static final StringDecoder DECODER = new StringDecoder();
	public static final StringEncoder ENCODER = new StringEncoder();
//	public static final MessageHandler MSG_HANDLER = new MessageHandler();

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline cPipe = ch.pipeline();
		
		// separate message by a delimiter
		cPipe.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
		// decode or encode msg to String or Byte
		cPipe.addLast(DECODER);
		cPipe.addLast(ENCODER);
		
		// handler the message
		cPipe.addLast(new MessageHandler());
	}
	

}
