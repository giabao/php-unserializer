package com.sandinh.phpparser

import org.scalatest.{Matchers, FlatSpec}
import scala.collection.mutable
import scala.annotation.tailrec

class PhpUnserializerSpec extends FlatSpec with Matchers {
  "Parser" should "parse null" in {
    assert(PhpUnserializer.parse("N;") == null)
  }

  "Parser" should "parse bool" in {
    assert(PhpUnserializer.parse("b:1;") == true)
    assert(PhpUnserializer.parse("b:0;") == false)
    assert(PhpUnserializer.parse("b:;") == false)

  }

  "Parser" should "not parse invalid bool" in {
    intercept[IllegalStateException] {
      PhpUnserializer.parse("b")
    }
    intercept[IllegalStateException] {
      PhpUnserializer.parse("b:")
    }
  }

  "Parser" should "parse int" in {
    PhpUnserializer.parse("i:123;") should be(123)
  }

  "Parser" should "not parse invalid int" in {
    intercept[IllegalStateException] {
      PhpUnserializer.parse("i")
    }
    intercept[IllegalStateException] {
      PhpUnserializer.parse("i:")
    }
    intercept[NumberFormatException] {
      PhpUnserializer.parse("i:;")
    }
  }

  "Parser" should "parse float" in {
    PhpUnserializer.parse("d:123.123;") should be(123.123d)
  }

  "Parser" should "parse in as float" in {
    PhpUnserializer.parse("i:3422865137422183;") should be(3.422865137422183E15)

    PhpUnserializer.parse("i:100010001804;") should be(1.00010001804E11)
  }

  "Parser" should "not parse invalid float" in {
    intercept[IllegalStateException] {
      PhpUnserializer.parse("f")
    }
    intercept[IllegalStateException] {
      PhpUnserializer.parse("f:")
    }
    intercept[IllegalStateException] {
      PhpUnserializer.parse("f:;")
    }
  }

  "Parser" should "parse string" in {
    PhpUnserializer.parse("""s:6:"string";""") should be("string")
  }

  "Parser" should "not parse the empty string" in {
    intercept[IllegalStateException] {
      PhpUnserializer.parse("")
    }
  }

  "Parser" should "not parse an invalid string" in {
    intercept[IllegalStateException] {
      PhpUnserializer.parse("s")
    }
  }

  "Parser" should "not parse string when length is missing" in {
    intercept[IllegalStateException] {
      PhpUnserializer.parse("s:")
    }
  }

  "Parser" should "not parse when string is missing" in {
    intercept[IllegalStateException] {
      PhpUnserializer.parse("s:0:")
    }
  }

  def shouldBeMap(x: Any, size: Int) = {
    assert(x.isInstanceOf[Map[_, _]], "parsed value must be Map")
    val r = x.asInstanceOf[Map[String, _]]
    r should have size size
    r
  }

  def shouldBeObj(x: Any, name: String, size: Int) = {
    assert(x.isInstanceOf[(_, _)], "parsed value must be tuble2(String, Map)")
    val r = x.asInstanceOf[(String, Map[_, _])]
    r._1 should be(name)
    shouldBeMap(r._2, size)
  }

  "Parser" should "parse array" in {
    val parsed = PhpUnserializer.parse("a:1:{i:1;i:2;}")
    val r = shouldBeMap(parsed, 1)
    r should contain key "1"
    r should contain value 2
  }

  "Parser" should "not parse invalid array" in {
    intercept[IllegalStateException] {
      PhpUnserializer.parse("a")
    }
  }

  "Parser" should "not parse array when length is missing" in {
    intercept[IllegalStateException] {
      PhpUnserializer.parse("a:")
    }
  }

  "Parser" should "parse empty array" in {
    PhpUnserializer.parse("a:0:")
    PhpUnserializer.parse("a:0:{}")
  }

  "Parser" should "not parse array when values missing" in {
    intercept[IllegalStateException] {
      PhpUnserializer.parse("a:1:")
    }
  }

  "Parser" should "parse object" in {
    val parsed = PhpUnserializer.parse("""O:8:"TypeName":1:{s:3:"foo";s:3:"bar";}""")
    val r = shouldBeObj(parsed, "TypeName", 1)
    r should contain("foo" -> "bar")
  }

  "Parser" should "parse stdClass" in {
    PhpUnserializer.parse("""O:8:"stdClass":0:{}""").asInstanceOf[(String, _)] should be("stdClass", Map())
    PhpUnserializer.parse("""O:8:"stdClass":0:""").asInstanceOf[(String, _)] should be("stdClass", Map())
  }

  "Parser" should "not parse invalid object" in {
    intercept[IllegalStateException] {
      PhpUnserializer.parse("O")
    }
  }

  "Parser" should "not parse when object length is missing" in {
    intercept[IllegalStateException] {
      PhpUnserializer.parse("O:")
    }
  }

  "Parser" should "not parse when object is missing" in {
    intercept[IllegalStateException] {
      PhpUnserializer.parse("O:8:")
    }
    intercept[IllegalStateException] {
      // O:8:"stdClass":0:{}
      PhpUnserializer.parse("""O:8:"stdClass":1""")
    }
  }

  "Parser" should "parse complex" in {
    try {
      PhpUnserializer.parse("""a:2:{i:0;a:8:{s:5:"class";O:7:"MyClass":1:{s:5:"pippo";s:4:"test";}i:0;i:1;i:1;d:0.19999998807907104;i:2;b:1;i:3;b:0;i:4;N;i:5;a:1:{i:0;s:1:";";}i:6;O:6:"Object":0:{}}i:1;a:8:{s:5:"class";O:7:"MyClass":1:{s:5:"pippo";s:4:"test";}i:0;i:1;i:1;d:0.19999998807907104;i:2;b:1;i:3;b:0;i:4;N;i:5;a:1:{i:0;s:1:";";}i:6;O:6:"Object":0:{}}}""")
    } catch {
      case _: Exception => fail("can't parse!")
    }

    // sample output of a yahoo web image search api call
    val input =
      """a:1:{s:9:"ResultSet";a:4:{s:21:"totalResultsAvailable";s:7:"1177824";s:20:"totalResultsReturned";
        |i:2;s:19:"firstResultPosition";i:1;s:6:"Result";a:2:{i:0;a:10:{s:5:"Title";s:12:"corvette.jpg";
        |s:7:"Summary";s:150:"bluefirebar.gif 03-Nov-2003 19:02 22k burning_frax.jpg 05-Jul-2002 14:34 169k corvette
        |.jpg 21-Jan-2004 01:13 101k coupleblack.gif 03-Nov-2003 19:00 3k";s:3:"Url";
        |s:48:"http://www.vu.union.edu/~jaquezk/MG/corvette.jpg";s:8:"ClickUrl";
        |s:48:"http://www.vu.union.edu/~jaquezk/MG/corvette.jpg";s:10:"RefererUrl";
        |s:35:"http://www.vu.union.edu/~jaquezk/MG";s:8:"FileSize";
        |s:7:"101.5kB";s:10:"FileFormat";s:4:"jpeg";s:6:"Height";s:3:"768";
        |s:5:"Width";s:4:"1024";s:9:"Thumbnail";a:3:{s:3:"Url";s:42:"http://sp1.mm-a1.yimg.com/image/2178288556";
        |s:6:"Height";s:3:"120";s:5:"Width";s:3:"160";}}i:1;a:10:{s:5:"Title";
        |s:23:"corvette_c6_mini_me.jpg";s:7:"Summary";s:48:"Corvette I , Corvette II , Diablo , Enzo , Lotus";
        |s:3:"Url";s:54:"http://www.ku4you.com/minicars/corvette_c6_mini_me.jpg";s:8:"ClickUrl";
        |s:54:"http://www.ku4you.com/minicars/corvette_c6_mini_me.jpg";s:10:"RefererUrl";
        |s:61:"http://mik-blog.blogspot.com/2005_03_01_mik-blog_archive.html";s:8:"FileSize";s:4:"55kB";
        |s:10:"FileFormat";s:4:"jpeg";s:6:"Height";s:3:"518";s:5:"Width";s:3:"700";
        |s:9:"Thumbnail";a:3:{s:3:"Url";s:42:"http://sp1.mm-a2.yimg.com/image/2295545420";
        |s:6:"Height";s:3:"111";s:5:"Width";s:3:"150";}}}}}
        |""".stripMargin.replace("\r\n", "").replace("\n", "")
    val r = shouldBeMap(PhpUnserializer.parse(input), 1)
    val r2 = shouldBeMap(r("ResultSet"), 4)
    shouldBeMap(r2("Result"), 2)
  }

  "Parser" should "parse complex struct with special chars" in {
    val input =
      """a:1:{i:0;O:9:"albumitem":19:{s:5:"image";O:5:"image":12:{s:4:"name";
        |s:26:"top_story_promo_transition";s:4:"type";s:3:"png";s:5:"width";i:640;
        |s:6:"height";i:212;s:11:"resizedName";s:32:"top_story_promo_transition.sized";
        |s:7:"thumb_x";N;s:7:"thumb_y";N;s:11:"thumb_width";N;s:12:"thumb_height";N;
        |s:9:"raw_width";i:900;s:10:"raw_height";i:298;s:7:"version";i:37;}s:9:"thumbnail";O:5:"image":12:{s:4:"name";
        |s:32:"top_story_promo_transition.thumb";s:4:"type";s:3:"png";s:5:"width";i:150;s:6:"height";
        |i:50;s:11:"resizedName";N;s:7:"thumb_x";N;s:7:"thumb_y";N;s:11:"thumb_width";
        |N;s:12:"thumb_height";N;s:9:"raw_width";i:150;s:10:"raw_height";i:50;s:7:"version";i:37;}s:7:"preview";
        |N;s:7:"caption";s:8:"supẩrb";s:6:"hidden";N;s:9:"highlight";b:1;s:14:"highlightImage";O:5:"image":12:{s:4:"name";
        |s:36:"top_story_promo_transition.highlight";s:4:"type";s:3:"png";s:5:"width";i:150;s:6:"height";i:50;
        |s:11:"resizedName";N;s:7:"thumb_x";N;s:7:"thumb_y";N;s:11:"thumb_width";N;s:12:"thumb_height";N;s:9:"raw_width";
        |i:150;s:10:"raw_height";i:50;s:7:"version";i:37;}s:11:"isAlbumName";N;s:6:"clicks";N;s:8:"keywords";s:0:"";
        |s:8:"comments";N;s:10:"uploadDate";i:1196339460;s:15:"itemCaptureDate";i:1196339460;s:8:"exifData";N;s:5:"owner";
        |s:20:"1156837966_352721747";s:11:"extraFields";a:1:{s:11:"Description";s:0:"";}s:4:"rank";N;s:7:"version";i:37;s:7:"emailMe";N;}}
        |""".stripMargin.replace("\r\n", "").replace("\n", "")
    val r = shouldBeMap(PhpUnserializer.parse(input), 1)
    r.toString should include("supẩrb")
  }

  "Parser" should "parsing from byte array" in {
    //this is exported from XenForo table xf_captcha_question / column `answers` (type BLOB)
    //a:4:{i:0;s:23:"cường ẩ ẵ ự:;"";i:1;s:6:"giabao";i:2;s:4:"vinh";i:3;s:3:"hai";}
    val exported = "613a343a7b693a303b733a32333a2263c6b0e1bb9d6e6720e1baa920e1bab520e1bbb13a3b22223b693a313b733a363a2267696162616f223b693a323b733a343a2276696e68223b693a333b733a333a22686169223b7d"

    def unHex(s: String): Array[Byte] = {
      assert(s.length % 2 == 0)
      var i = 0
      val a = mutable.ArrayBuilder.make[Byte]

      @tailrec
      def take2(): Unit = {
        val ret = s.substring(i, i + 2)
        i += 2
        val c = Integer.parseInt(ret, 16)
        a += c.toByte
        if (i < s.length) take2()
      }

      take2()
      a.result()
    }

    val blob = unHex(exported)
    val result = PhpUnserializer.parse(blob)
    assert(result.isInstanceOf[Map[_, _]], "parsed value must be Map")
    val r = result.asInstanceOf[Map[String, _]]
    r should have size 4
    r should contain key "0"
    r should contain value "cường ẩ ẵ ự:;\""

    val expectedAnswers = r
    def validate(answer: String) = expectedAnswers.exists {
      case (_, s: String) => answer.equalsIgnoreCase(s)
      case _              => false
    }
    assert(validate("cƯỜnG Ẩ Ẵ Ự:;\""))
  }
}
