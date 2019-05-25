package com.ahei.gonetty;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		int port = 8080;
		
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		}
		new DiscardServer(port).run();
	}
}
