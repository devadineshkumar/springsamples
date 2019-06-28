package spring.methodcache.app;

import java.util.Arrays;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Component
@Configuration
@EnableCaching
public class CachingClass {
	
	private CacheManager cacheManager = null;
		
	@Bean
	public CacheObject getCacheObject() {
		return new CacheObject();
	}
	
	@Bean
	public CacheManager cacheManager() {
		cacheManager = new SimpleCacheManager();
		((SimpleCacheManager) cacheManager).setCaches(Arrays.asList(new ConcurrentMapCache("myIntValue")));
		return cacheManager;
	}
	
	// @CacheEvict("myIntValue")
		@CacheEvict(value="myIntValue", allEntries=true)
		public void evitBySpringReg(int value) {
		}

}
