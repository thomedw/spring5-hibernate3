[![Build Status](https://circleci.com/gh/thomedw/spring5-hibernate3.svg?style=svg)](https://circleci.com/gh/thomedw/spring5-hibernate3)


# Hibernate 3 Support for Spring 5

Spring 5 [dropped hibernate 3 and 4 support](https://github.com/spring-projects/spring-framework/wiki/What%27s-New-in-Spring-Framework-5.x). This library includes all classes in `org.springframework.orm.hibernate3`. The source is taken from Spring 4, the last with hibernate 3 support.

## How to Use

This library is hosted in jcenter. If jcenter does not work, use this repository: `https://dl.bintray.com/thomedw/maven`.

Maven Configuration:

```xml
<dependency>
  <groupId>com.katalisindonesia.spring5</groupId>
  <artifactId>spring5-hibernate3</artifactId>
  <version>0.0.1</version>
  <type>pom</type>
</dependency>
```

Gradle

```
compile 'com.katalisindonesia.spring5:spring5-hibernate3:0.0.1'
```

Ivy

```xml
<dependency org='com.katalisindonesia.spring5' name='spring5-hibernate3' rev='0.0.1'>
  <artifact name='spring5-hibernate3' ext='pom' ></artifact>
</dependency>
```

