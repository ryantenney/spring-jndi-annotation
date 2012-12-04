package com.ryantenney.spring.jndi;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.naming.Context;
import javax.naming.InitialContext;

import static org.junit.Assert.*;

public class JndiValueAnnotationTest {

	@BeforeClass
	public static void setUp() throws Exception {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryForTest.class.getName());

		InitialContext ctx = new InitialContext();
		ctx.bind("asdf", "ASDF");
		ctx.bind("qwerty", "QWERTY");
	}

	@Test
	public void test() throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:test-context.xml");
		JndiValueAnnotationTarget target = (JndiValueAnnotationTarget) ctx.getBean("targetBean");
		assertEquals("ASDF", target.getAsdf());
		assertEquals("QWERTY", target.getQwerty());
	}

}
