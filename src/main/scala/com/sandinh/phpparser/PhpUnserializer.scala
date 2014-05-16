package com.sandinh.phpparser

import scala.util.Try

object PhpUnserializer {
  @inline def parse(input: Array[Byte]) = new PhpUnserializer(new String(input, "UTF-8")).parse
  @inline def parse(input: String) = new PhpUnserializer(input).parse
}
class PhpUnserializer(input: String) {
  private var index = 0

  private def parse = {
    if (index >= input.length)
      throw new IllegalStateException(s"End of input at $index")
    val t = input.charAt(index)
    t match {
      case 'N' =>
        index += 2
        null
      case 'b' =>
        index += 2
        parseBoolean
      case 'd' =>
        index += 2
        parseDouble
      case 'i' =>
        index += 2
        parseIntOrLong
      case 's' =>
        index += 2
        parseString
      case 'a' =>
        index += 2
        parseArray
      case 'O' =>
        index += 2
        parseObject
      case _ => throw new IllegalStateException(s"Encountered unknown type [$t]")
    }
  }

  private def parseBoolean = {
    val delimiter = parseDelimiter(';')
    val value = input.substring(index, delimiter)
    index = delimiter + 1
    value == "1"
  }

  private def parseDouble = {
    val delimiter = parseDelimiter(';')
    val value = input.substring(index, delimiter)
    index = delimiter + 1
    value.toDouble
  }

  /** @return Int | Long */
  private def parseIntOrLong = {
    val delimiter = parseDelimiter(';')
    val value = input.substring(index, delimiter)
    index = delimiter + 1
    // Let's store old value of the index for the patch Integer/Double for the PHP x64.
    Try { value.toInt } getOrElse value.toLong
  }

  private def parseDelimiter(ch: Char) = {
    val delim = input.indexOf(ch, index)
    if (delim < 0)
      throw new IllegalStateException(s"No delimiter $ch found at $index")
    delim
  }

  private def readLength = {
    val delimiter = parseDelimiter(':')
    val arrayLen = input.substring(index, delimiter).toInt
    index = delimiter + 2
    arrayLen
  }

  /** Assumes strings are utf8 encoded */
  private def parseString = {
    val strLen = readLength
    var utfStrLen = 0
    var byteCount = 0
    while (byteCount != strLen) {
      val ch = input.charAt(index + utfStrLen)
      utfStrLen += 1
      if ((ch >= 0x0001) && (ch <= 0x007F)) {
        byteCount += 1
      } else if (ch > 0x07FF) {
        byteCount += 3
      } else {
        byteCount += 2
      }
    }
    //TODO explain this check & add a test case for it
    if (index + utfStrLen > input.length)
      throw new IllegalStateException(s"End of input at ${input.length}")
    val value = input.substring(index, index + utfStrLen)
    index = index + utfStrLen + 2
    value
  }

  private def parseArray: Map[String, Any] = {
    val arrayLen = readLength
    val result = Map.newBuilder[String, Any]
    var i = 0
    while (i < arrayLen) {
      val key = parse
      val value = parse
      key match {
        case k: Int    => result += k.toString -> value
        case k: String => result += k -> value
        case _         => throw new IllegalStateException(s"Encountered unacceptable key [$key]")
      }
      i += 1
    }
    index += 1
    result.result()
  }

  /** return tuble2: (name, map of attributes)*/
  private def parseObject: (String, Map[String, Any]) = {
    val strLen = readLength
    if (index + strLen > input.length)
      throw new IllegalStateException(s"End of input at ${input.length}")
    val name = input.substring(index, index + strLen)
    index = index + strLen + 2
    val attributes = parseArray
    (name, attributes)
  }
}
