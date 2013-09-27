package org.lorecraft.phpserializer

import org.scalatest.{Matchers, FlatSpec}
import scala.annotation.tailrec
import org.lorecraft.phparser.SerializedPhpParser
import scala.collection.mutable
import collection.JavaConverters._

class SerializedPhpParserSpec extends FlatSpec with Matchers {
  "parsing from byte array" should "success" in {
    //this is exported from XenForo table xf_captcha_question / column `answers`
    //a:4:{i:0;s:23:"cường ẩ ẵ ự:;"";i:1;s:6:"giabao";i:2;s:4:"vinh";i:3;s:3:"hai";}
    val exported = "613a343a7b693a303b733a32333a2263c6b0e1bb9d6e6720e1baa920e1bab520e1bbb13a3b22223b693a313b733a363a2267696162616f223b693a323b733a343a2276696e68223b693a333b733a333a22686169223b7d"

    def convert(s: String): String = {
      assert(s.length % 2 == 0)
      var i = 0
      val a = mutable.ArrayBuilder.make[Byte]

      @tailrec
      def take2() {
        val ret = s.substring(i, i + 2)
        i += 2
        val c = Integer.parseInt(ret, 16)
        a += c.toByte
        if(i < s.length) take2()
      }

      take2()

      new String(a.result(), "utf-8")
    }

    val input = convert(exported)
    val serializedPhpParser = new SerializedPhpParser(input)
    val result = serializedPhpParser.parse

    assert(result.isInstanceOf[java.util.Map[_, _]])
    val r = result.asInstanceOf[java.util.Map[_, _]].asScala
    r should have size 4
    r should contain key 0
    r should contain value "cường ẩ ẵ ự:;\""
  }
}
