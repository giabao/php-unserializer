php-unserializer
========================
This is a library for parsing [php-serialized](http://php.net/manual/en/function.serialize.php) data to [scala](http://scala-lang.org/) object.

### Sample usage
(This is my real usage)

I have a RESTful service implemented in scala (use [Play Framework](http://www.playframework.com/)) that need integrate to an existing website (use [XenForo](http://xenforo.com/)) to check if user is logged in & send a validated captcha when call my service.

Captcha correct answers is stored (by XenForo) in a BLOB column in Mysql.
The code below will validate if answer is correct:
```scala
val blob: Array[Byte] = getDataFromDB()

//If we have blob data = sql fetch from a BLOB column (data is saved from php using serialize() function)
//Then in scala (java) we need prepare input for PhpUnserializer by calling:
//input = new String(data, "utf-8")
val input = new String(blob, "utf-8")

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

### Licence
This software is licensed under the Apache 2 license:
http://www.apache.org/licenses/LICENSE-2.0

Copyright 2013 Sân Đình (http://sandinh.com)
