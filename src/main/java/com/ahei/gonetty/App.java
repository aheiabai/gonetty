package com.ahei.gonetty;

import java.time.Duration;

import org.ehcache.CacheManager;
import org.ehcache.config.Builder;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.core.statistics.CacheStatistics;
import org.ehcache.impl.internal.statistics.DefaultStatisticsService;
import org.ehcache.spi.service.Service;
import org.ehcache.spi.service.ServiceProvider;

/**
 * Hello world!
 *
 */
public class App {
	public static  final StatisticsService statisService= new DefaultStatisticsService();
	public static final CacheConfiguration<String, String> cacheConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(
			String.class, String.class, ResourcePoolsBuilder.heap(1007))
			.withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofHours(2)))
			.build();
	public static final CacheManager CACHE_MANAGER = CacheManagerBuilder.newCacheManagerBuilder()
			.using(statisService)
			.build(true);

	public static void main(String[] args) {
		int port = 8080;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		}
		/*
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() { new Server(8081).run(); } }).start();
		 */
		new Server(port).run();
	}
	
}
