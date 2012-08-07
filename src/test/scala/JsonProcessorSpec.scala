import io.Source
import org.specs2.mutable._

class JsonProcessorSpec extends Specification {

  "JsonProcessor " should {

    "process an input/map with one item" in {
      val input = """[{"name":"Ed"}]"""
      val map = """{"name":"firstName"}"""
      JsonProcessor.process(input, map) must equalTo("""[{"firstName":"Ed"}]""")
    }

    "process an input/map with multiple items" in {
      val input = """[{"name":"Ed"},{"name":"Joe"}]"""
      val map = """{"name":"firstName"}"""
      JsonProcessor.process(input, map) must equalTo("""[{"firstName":"Ed"},{"firstName":"Joe"}]""")
    }

    "process an input/map with multiple items and multiple maps" in {
      val input = """[{"name":"Ed","phone":"112233"},{"name":"Joe","phone":"112233"}]"""
      val map = """{"name":"firstName","phone":"phoneNumber"}"""
      JsonProcessor.process(input, map) must equalTo("""[{"firstName":"Ed","phoneNumber":"112233"},{"firstName":"Joe","phoneNumber":"112233"}]""")
    }

    "just pass through any property that there is no map for" in {
      val input = """[{"name":"Ed","phone":"112233"},{"name":"Joe","phone":"112233"}]"""
      val map = """{"name":"firstName"}"""
      JsonProcessor.process(input, map) must equalTo("""[{"firstName":"Ed","phone":"112233"},{"firstName":"Joe","phone":"112233"}]""")
    }

    "ignore any property that can't be mapped" in {
      val input = """[{"name":"Ed","phone":"112233"},{"name":"Joe","phone":"112233"}]"""
      val map = """{"name":"firstName", "age":"personAge"}"""
      JsonProcessor.process(input, map) must equalTo("""[{"firstName":"Ed","phone":"112233"},{"firstName":"Joe","phone":"112233"}]""")
    }

    "support !camelCase command" in {
      val input = """[{"Person Name":"Ed"}]"""
      val map = """{"Person Name":"!camelCase"}"""
      JsonProcessor.process(input, map) must equalTo("""[{"personName":"Ed"}]""")
    }

    "support !ignore command" in {
      val input = """[{"Person Name":"Ed", "blockMe" : "blah"}]"""
      val map = """{"Person Name":"!camelCase", "blockMe" : "!ignore" }"""
      JsonProcessor.process(input, map) must equalTo("""[{"personName":"Ed"}]""")
    }

    "support :Array hint" in {
      val input = """[{"Names":"Ed"}]"""
      val map = """{"Names":"Names:Array" }"""
      JsonProcessor.process(input, map) must equalTo("""[{"Names":["Ed"]}]""")
    }

    "support camelcase and array " in {
      val input = """[{"Person Names":"Ed,Joe,Sam"}]"""
      val map = """{"Person Names":"!camelCase:Array" }"""
      JsonProcessor.process(input, map) must equalTo("""[{"personNames":["Ed","Joe","Sam"]}]""")
    }

    "support !merge" in {
      val input = """[{"a":"apple"}]"""
      val map = """{"a" : "!merge->mergeOut", "mergeOut" : "${a}"}"""
      JsonProcessor.process(input, map) must equalTo("""[{"mergeOut":"apple"}]""")
    }

    "support !merge with mutiple substitutions" in {

     val input = """[{
        "a" : "Apple",
        "b" : "Banana"
     }]"""

      val map =
        """{
        "a" : "!merge->mergeOut",
        "b" : "!merge->mergeOut",
        "mergeOut" : "template:${a},${b}!"
        }
        """

      JsonProcessor.process(input,map) must equalTo("""[{"mergeOut":"template:Apple,Banana!"}]""")
    }

    "support !merge with keys that have multiple words" in {
      val input = """[{
        "A Key" : "I'm a key"
      }]"""

      val map = """{"A Key" : "!merge->mergeOut", "mergeOut": "template:${A Key}!" }"""

      JsonProcessor.process(input, map) must equalTo("""[{"mergeOut":"template:I'm a key!"}]""")
    }

    "work with sample1 json" in {

      val input = Source.fromFile("src/test/resources/sample1/input.json").mkString
      val map = Source.fromFile("src/test/resources/sample1/map.json").mkString

      val out = JsonProcessor.process(input,map)
      println("out:")
      println(out)
      println("..")
      true must be equalTo(true)
    }

    "allow inserts from map" in {
      val input = """[{"name":"Ed"}]"""
      val map = """{"name":"firstName", "!insert:defaultValue": "hello"}"""
      JsonProcessor.process(input, map) must equalTo("""[{"firstName":"Ed","defaultValue":"hello"}]""")
    }

    /*"allow inserts of full objects from map" in {

      val input = """[{"name":"Ed"}]"""
      val map = """{"name":"firstName", "!insert:object": {"msg":"hello"}}"""
      JsonProcessor.process(input, map) must equalTo("""[{"firstName":"Ed","object":{"msg":"hello"}}]""")

    } */
  }
}