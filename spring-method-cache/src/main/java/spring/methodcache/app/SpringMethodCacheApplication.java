package spring.methodcache.app;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication()
public class SpringMethodCacheApplication {

	public static void main(String[] args) {
		
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(); 
		ctx.register(CachingClass.class);
		ctx.refresh();
		
		CacheObject cacheObject = ctx.getBean(CacheObject.class);		
		// calling getBook method first time.
		System.out.println(cacheObject.getIntValue(1));
		// calling getBook method second time. This time, method will not
		// execute.
		System.out.println(cacheObject.getIntValue(1));
		// calling getBook method third time with different value.
		System.out.println(cacheObject.getIntValue(2));
		System.out.println(cacheObject.getIntValue(2));
		
		System.out.println(cacheObject.getIntValue(4));
		System.out.println(cacheObject.getIntValue(5));
		System.out.println(cacheObject.getIntValue(5));
		ctx.close();
		
	}

}
