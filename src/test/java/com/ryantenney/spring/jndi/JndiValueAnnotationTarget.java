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


	/**
	 * Tests setter injection
	 */

	private String qwerty;

	@JndiValue("qwerty")
	public void setQwerty(final String qwerty) {
		this.qwerty = qwerty;
	}

	public String getQwerty() {
		return this.qwerty;
	}

	/**
	 * Tests required=false
	 */

	private Object nothing;

	@JndiValue(value="nothing", required=false)
	public void setNothing(final Object nothing) {
		this.nothing = nothing;
	}

	public Object getNothing() {
		return this.nothing;
	}


	/**
	 * Tests type conversion
	 */

	private URL url;

	@JndiValue("url")
	public void setUrl(URL url) {
		this.url = url;
	}

	public URL getUrl() {
		return url;
	}

}
