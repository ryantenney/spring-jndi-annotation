package com.ryantenney.spring.jndi;

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

	private Object nothing;

	/**
	 * Tests required=false
	 */
	@JndiValue(value="nothing", required=false)
	public void setNothing(final Object nothing) {
		this.nothing = nothing;
	}

	public Object getNothing() {
		return this.nothing;
	}

}
