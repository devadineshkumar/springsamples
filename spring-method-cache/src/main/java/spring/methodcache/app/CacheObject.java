package spring.methodcache.app;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

public class CacheObject {

	@Cacheable(value = "myIntValue")
	public String getIntValue(int intVal) {
		switch (intVal) {
		case 1:
			System.out.println("Executed Method for 1");
			return "One";
		case 2:
			System.out.println("Executed Method for 2");
			return "Two";
		case 3:
			System.out.println("Executed Method for 3");
			return "Three";
		default:
			System.out.println("No Value Match");
			break;
		}
		return "No Value Match";
	}

	@CacheEvict(value = "myIntValue", allEntries = true)
	public void evitBySpringReg(int value) {
		System.out.println("Waiting to implement sssssssssssssssssssssssssssssssssssssssssssssssssssssssss");
	}

}
