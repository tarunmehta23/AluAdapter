package com.charter.provisioning.voice.commercial.alu.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.Converter;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomEnumConverter implements Converter {
	@Override
	public <T> T convert(Class<T> tClass, Object o) {
		String enumValName = (String) o;
		Method method = null;
		try {
			method = tClass.getMethod("forString", String.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		T obj = null;
		try {
			obj = ((T) method.invoke(o, enumValName));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return obj ;
	}

}