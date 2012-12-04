package com.ryantenney.spring.jndi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.naming.NamingException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.jndi.JndiLocatorSupport;

import static org.springframework.util.ReflectionUtils.*;

public class JndiValueAnnotationBeanPostProcessor extends JndiLocatorSupport implements BeanPostProcessor, PriorityOrdered {

	private int order = Ordered.LOWEST_PRECEDENCE - 2;

	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
		Class<?> targetClass = bean.getClass();

		doWithFields(targetClass, new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				JndiValue ann = field.getAnnotation(JndiValue.class);
				if (ann != null) {
					Class<?> requiredType = field.getType();
					Object value = lookup(ann, requiredType);
					if (value != null) {
						makeAccessible(field);
						setField(field, bean, value);
					}
				}
			}
		}, COPYABLE_FIELDS);

		doWithMethods(targetClass, new MethodCallback() {
			@Override
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				JndiValue ann = method.getAnnotation(JndiValue.class);
				if (ann != null) {
					Class<?> requiredType = method.getParameterTypes()[0];
					Object value = lookup(ann, requiredType);
					if (value != null) {
						makeAccessible(method);
						invokeMethod(method, bean, value);
					}
				}
			}
		}, USER_DECLARED_METHODS);

		return bean;
	}

	protected <T> T lookup(JndiValue ann, Class<T> requiredType) {
		try {
			return super.lookup(ann.value(), requiredType);
		} catch (NamingException ex) {
			if (ann.required()) {
				logger.debug("Object not found for required @JndiValue with name [" + ann.value() + "]");
				throw new RuntimeException(ex);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Object not found for optional @JndiValue with name [" + ann.value() + "]");
				}
				return null;
			}
		}
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public int getOrder() {
		return order;
	}

	public void setOrder(final int order) {
		this.order = order;
	}

}
