package com.ryantenney.spring.jndi;

import java.net.URL;

public class JndiValueAnnotationTarget {

	/**
	 * Tests private field injection
	 */
	@JndiValue("asdf")
	private String asdf;

	public String getAsdf() {
		return this.asdf;
	}

	private String qwerty;

	/**
	 * Tests setter injection
	 */
	@JndiValue("qwerty")
	public void setQwerty(final String qwerty) {
		this.qwerty = qwerty;
	}

	public String getQwerty() {
		return this.qwerty;
	}

}
