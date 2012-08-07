import org.specs2.mutable._

class KeyProcessorSpec extends Specification {

  "KeyProcessor" should {


    "process camel case" in {
      val p : CamelCaseProcessor = new CamelCaseProcessor()
      p.process("Hello World") must be equalTo("helloWorld")
    }

    "process ignore" in {
      val p : KeyValueProcessor = new KeyValueProcessor {
        def process(key: String, value: String): Option[(String, Any)] = None
      }
      p.process("someVal", "value") must beNone
    }
  }

  "ProcessorLookup" should {

    "Find a string processor" in {
      val p : KeyValueProcessor = ProcessorLookup.lookup("PersonName")
      p.process("Person Name", "Ed") must beSome(("PersonName", "Ed"))
    }

    "Find an ignore processor" in {
      val p : KeyValueProcessor = ProcessorLookup.lookup("!ignore")
      p.process("Person Name", "Ed") must beNone
    }

    "Find a camel case processor" in {
      val p : KeyValueProcessor = ProcessorLookup.lookup("!camelCase")
      p.process("Person Name", "Ed") must beSome(("personName", "Ed"))
    }

    "Find an array processer" in {
      val p : KeyValueProcessor = ProcessorLookup.lookup("Names:Array")
      p.process("Names", "1,2") must beSome(("Names", List("1","2")))
    }
  }
}
