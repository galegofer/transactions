package com.backbase.transactions.config.cache;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.cache.CacheBuilder;

/**
 * Configuration class to cache the response that comes from open bank, given we
 * assume no changes in the data, we can cache it.
 * 
 * @author Damian
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheConfiguration.class);

	@Bean
	public CacheManager cacheManager() {
		GuavaCacheManager cacheManager = new GuavaCacheManager("transaction-list", "transaction-per-type",
				"transaction-per-type-amount");
		CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES);
		cacheManager.setCacheBuilder(cacheBuilder);

		LOGGER.info("Cache initialized with name/s '{}'", cacheManager.getCacheNames());

		return cacheManager;
	}
}
