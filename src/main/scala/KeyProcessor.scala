import util.matching.Regex

trait KeyProcessor {
  def process(key: String): String = key
}

trait ValueProcessor {
  def process(value: String): Any = value
}

trait KeyValueProcessor {
  def process(key: String, value: String): Option[(String, Any)]
}

class DefaultKeyValueProcessor(
                                keyProcessor: KeyProcessor = new KeyProcessor {},
                                valueProcessor: ValueProcessor = new ValueProcessor {})
  extends KeyValueProcessor {

  def process(key: String, value: String): Option[(String, Any)] = {
    Some(keyProcessor.process(key), valueProcessor.process(value))
  }
}

object ProcessorLookup {

  val DirectiveMatch: Regex = "!(.*)".r

  val PairMatch: Regex = "(.*):(.*)".r

  val IGNORE: String = "ignore"
  val CAMEL_CASE: String = "camelCase"

  def lookup(instructionKey: String): KeyValueProcessor = instructionKey match {

    case null => null
    case s: String if s.contains(IGNORE) => {
      new KeyValueProcessor {
        def process(key: String, value: String) = None
      }
    }
    case s: String => {
      s match {
        case PairMatch(key,valueInstruction) => {
          val keyProcessor = lookupKeyProcessor(key)
          val valueProcessor = lookupValueProcessor(valueInstruction)
          new DefaultKeyValueProcessor(keyProcessor, valueProcessor)
        }
        case _ => {
          val keyProcessor = lookupKeyProcessor(instructionKey)
          new DefaultKeyValueProcessor(keyProcessor)
        }
      }
    }
  }

  def lookupKeyProcessor(key: String): KeyProcessor = key match {
    case null => new KeyProcessor {}
    case DirectiveMatch(d) => d match {
      case s: String if s == CAMEL_CASE => new CamelCaseProcessor()
    }
    case _ => new StringProcessor(key)
  }

  def lookupValueProcessor(key: String): ValueProcessor = key match {
    case null => new ValueProcessor {}
    case s: String if s.contains("Array") => new ValueProcessor {
      override def process(value: String) = {
        println("process list !!!: " + value)
        value.split(",").toList
      }
    }
    case _ => new ValueProcessor {}
  }
}

class StringProcessor(newKey: String) extends KeyProcessor {
  override def process(key: String) = newKey
}

class CamelCaseProcessor extends KeyProcessor {

  override def process(key: String) = camelize(key)

  def camelize(s: String) = s match {
    case null => ""
    case _ => {
      val list = s.split(" ").toList
      (list.head.toLowerCase + list.tail.map(capitalize).mkString)
    }
  }

  def capitalize(s: String) = s.length match {
    case 0 => s
    case 1 => s
    case _ => s(0).toUpper + s.substring(1, s.length).toLowerCase
  }
}
