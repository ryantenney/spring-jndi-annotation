package com.ryantenney.spring.jndi;

import static org.springframework.util.ReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.jndi.JndiLocatorSupport;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.MethodCallback;

public class JndiValueAnnotationBeanPostProcessor extends JndiLocatorSupport implements BeanPostProcessor, BeanFactoryAware, PriorityOrdered {

	private int order = Ordered.LOWEST_PRECEDENCE - 2;

	private TypeConverter typeConverter;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof ConfigurableBeanFactory) {
			typeConverter = ((ConfigurableBeanFactory) beanFactory).getTypeConverter();
		}

		if (typeConverter == null && logger.isInfoEnabled()) {
			logger.info("Unable to obtain a TypeConverter, will attempt to make do without one");
		}
	}

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
					MethodParameter param = MethodParameter.forMethodOrConstructor(method, 0);
					Class<?> requiredType = param.getParameterType();
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
		return lookup(ann, requiredType, null);
	}

	protected <T> T lookup(JndiValue ann, Class<T> requiredType, MethodParameter param) {
		try {
			Object value = super.lookup(ann.value());

			if (typeConverter != null) {
				return typeConverter.convertIfNecessary(value, requiredType, param);
			} else {
				if (requiredType.isInstance(value)) {
					return requiredType.cast(value);
				} else {
					throw new TypeMismatchException(value, requiredType);
				}
			}
		} catch (Exception ex) {
			if (ann.required()) {
				logger.error("Object not found for required @JndiValue with name [" + ann.value() + "]");
				rethrowRuntimeException(ex);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Object not found for optional @JndiValue with name [" + ann.value() + "]");
				}
			}
			return null;
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
