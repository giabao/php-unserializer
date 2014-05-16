php-unserializer
========================
This is a library for parsing [php-serialized](http://php.net/manual/en/function.serialize.php)
data to [scala](http://scala-lang.org/) object.

For [SBT](http://www.scala-sbt.org/), add the following to your build file.

```scala
libraryDependencies += "com.sandinh" %% "php-unserializer" % "1.0.3"
```

### Sample usage
(This is my real usage)

I have a RESTful service implemented in scala (use [Play Framework](http://www.playframework.com/))
that need integrate to an existing website (use [XenForo](http://xenforo.com/)) to check if user is
logged in & send a validated captcha when call my service.

Captcha correct answers is stored (by XenForo) in a BLOB column in Mysql.
The code below will validate if answer is correct:
```scala
val blob: Array[Byte] = getDataFromDB()

val result = PhpUnserializer.parse(blob)

assert(result.isInstanceOf[Map[_, _]], "parsed value must be Map")

val expectedAnswers = result.asInstanceOf[Map[String, _]]

expectedAnswers should contain value "cường ẩ ẵ ự:;\""

def validate(answer: String) = expectedAnswers.exists{
  case (_, s: String) => answer.equalsIgnoreCase(s)
  case _ => false
}

assert(validate("cƯỜnG Ẩ Ẵ Ự:;\""))
```
@see full test code in [PhpUnserializerSpec.scala](https://github.com/giabao/php-unserializer/blob/master/src/test/scala/com/sandinh/phpparser/PhpUnserializerSpec.scala)

### Changelogs
We use [Semantic Versioning](http://semver.org), so changing in micro version is binary compatible.

##### v1.0.4
+ parse large int to Long instead of Double
+ add more test

##### v1.0.3
cross compile to scala 2.11 & 2.10

##### v1.0.2
Improved exception handling.

##### v1.0.1
Add PhpUnserializer#parse(Array[Byte])

##### v1.0.0
First stable release

### Licence
This software is licensed under the Apache 2 license:
http://www.apache.org/licenses/LICENSE-2.0

Copyright 2013 Sân Đình (http://sandinh.com)
