#Spring JNDI Annotation [![Build Status](https://secure.travis-ci.org/ryantenney/spring-jndi-annotation.png)](http://travis-ci.org/ryantenney/spring-jndi-annotation)
=================================

###Maven

```xml
<repository>
	<id>sonatype-oss-public</id>
	<url>https://oss.sonatype.org/content/groups/public/</url>
	<snapshots>
		<enabled>true</enabled>
	</snapshots>
</repository>

<dependency>
	<groupId>com.ryantenney</groupId>
	<artifactId>spring-jndi-annotation</artifactId>
	<version>0.1.0-SNAPSHOT</version>
</dependency>
```

###Basic Usage

Include in your application context:

```xml
<bean class="com.ryantenney.spring.jndi.JndiValueAnnotationBeanPostProcessor" />
```

And annotate away:

```java
@Component
public class SpringBean {

	@JndiValue("foo")
	private String bar;

}
```

---

### License

Copyright (c) 2012 Ryan Tenney

Published under Apache Software License 2.0, see LICENSE

[![Rochester Made](http://rochestermade.com/media/images/rochester-made-dark-on-light.png)](http://rochestermade.com)
