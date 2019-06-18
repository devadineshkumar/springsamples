package spring.user.app;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "loading")
public class PropsConfiguration {
	
	private Integer integerValue;
	private String stringValue;
	private Integer randomValue;
	private String randomString;
	
	private InnerProp innerProp;

	public Integer getIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Integer getRandomValue() {
		return randomValue;
	}

	public void setRandomValue(Integer randomValue) {
		this.randomValue = randomValue;
	}

	public String getRandomString() {
		return randomString;
	}

	public void setRandomString(String randomString) {
		this.randomString = randomString;
	}

	public InnerProp getInnerProp() {
		return innerProp;
	}

	public void setInnerProp(InnerProp innerProp) {
		this.innerProp = innerProp;
	}

}
