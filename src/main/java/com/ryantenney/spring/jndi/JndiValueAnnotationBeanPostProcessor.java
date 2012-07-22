package com.ryantenney.spring.jndi;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.MethodCallback;

public class JndiValueAnnotationBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware, PriorityOrdered {

	private static final Logger log = LoggerFactory.getLogger(JndiValueAnnotationBeanPostProcessor.class);

	private static final int ORDER = Ordered.LOWEST_PRECEDENCE - 2;

	private TypeConverter typeConverter;
	private InitialContext ctx;

	public JndiValueAnnotationBeanPostProcessor() throws NamingException {
		this.ctx = new InitialContext();
	}

	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
		ReflectionUtils.doWithFields(bean.getClass(), new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				JndiValue ann = field.getAnnotation(JndiValue.class);
				if (ann != null) {
					Object value = lookup(ann.value(), field.getType());
					setAccessible(field);
					field.set(bean, value);
				}
			}
		});

		ReflectionUtils.doWithMethods(bean.getClass(), new MethodCallback() {
			@Override
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				JndiValue ann = method.getAnnotation(JndiValue.class);
				if (ann != null) {
					try {
						Object value = lookup(ann.value(), method.getParameterTypes()[0]);
						setAccessible(method);
						method.invoke(bean, value);
					} catch (InvocationTargetException e) {
						throw new RuntimeException(e);
					}
				}
			}
		});

		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
			throw new IllegalArgumentException("Requires ConfigurableListableBeanFactory");
		}

		this.typeConverter = ((ConfigurableListableBeanFactory) beanFactory).getTypeConverter();
	}

	@Override
	public int getOrder() {
		return ORDER;
	}

	private <T> T lookup(String name, Class<T> type) {
		try {
			Object value = ctx.lookup(name);
			return typeConverter.convertIfNecessary(value, type);
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

	private void setAccessible(AccessibleObject ao) {
		if (!ao.isAccessible()) {
			ao.setAccessible(true);
		}
	}

}
