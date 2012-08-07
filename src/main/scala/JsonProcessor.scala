import collection.immutable
import collection.immutable.HashMap
import com.codahale.jerkson.Json
import com.codahale.jerkson.Json._
import io.Source
import java.io.{FileWriter, File}

object JsonProcessor {


  val MergeMatch = "!merge->(.*)".r

  def process(input: String, map: String, output: String) {
    println(input)
    println(map)
    println(output)
    val inputFile: File = new File(input)

    if (!inputFile.exists) {
      println("Can't find: " + inputFile)
    }

    val mapFile: File = new File(map)

    if (!mapFile.exists) {
      println("Can't find: " + mapFile)
    }

    if (!inputFile.exists || !mapFile.exists) {
      return
    }

    val inputContents = Source.fromFile(inputFile).mkString
    val mapContents = Source.fromFile(mapFile).mkString
    val jsonOut: String = process(inputContents, mapContents)
    val fw = new FileWriter(output)
    fw.write(jsonOut)
    fw.close()
  }

  def process(inputJson: String, mapJson: String): String = {
    val inputList: List[Map[String, Any]] = Json.parse[List[Map[String, Any]]](inputJson)

    val mapObject: Map[String, String] = Json.parse[Map[String, String]](mapJson)

    val processedList: List[Map[String, Any]] = inputList.map(item => {
      var output: Map[String, Any] = immutable.Map.empty[String, Any]

      var unprocessedItems = collection.mutable.Map(item.toSeq: _*)
      val mergeProcessor = new MergeProcessor()

      mapObject.foreach((kv: (String, String)) => {

        val mapKey = kv._1
        val mapInstruction = kv._2
        val itemValue = item.get(mapKey)

        if (mapKey.startsWith("!insert")) {
          val key = mapKey.replace("!insert:", "")
          output += (key -> mapInstruction)
        } else {

          itemValue match {
            case Some(foundValue) => {

              if (isMerge(mapInstruction)) {
                val MergeMatch(mergeName) = mapInstruction
                val success: Boolean = processMerge(mergeName, mapObject.get(mergeName), mergeProcessor, mapKey, foundValue)
                if (success) {
                  unprocessedItems -= mapKey
                }
              } else {
                val processor = ProcessorLookup.lookup(mapInstruction)
                if (processor != null) {
                  unprocessedItems -= mapKey

                  val result: Option[(String, Any)] = processor.process(mapKey, foundValue.asInstanceOf[String])
                  result match {
                    case Some(processed) => {
                      output += processed._1 -> processed._2
                    }
                    case None => //do nothing
                  }
                }
              }
            }
            case None => //do-nothing
          }
        }
      })
      //copy over any unprocessed items
      unprocessedItems.foreach((kv) => output += (kv._1 -> kv._2))

      val merges: List[(String, String)] = mergeProcessor.processMerges()

      merges.foreach((t: (String, String)) => {
        output += (t._1 -> t._2)
      })

      output
    })
    generate(processedList)
  }


  private def processMerge(mergeName: String, template: Option[String], processor: MergeProcessor, mapKey: String, foundValue: Any): Boolean = {

    template match {
      case Some(foundTemplate) => {
        processor.createMergeIfNeeded(mergeName, foundTemplate)
        processor.addSubstitutionToMerge(mergeName, mapKey, foundValue.asInstanceOf[String])
        true
      }
      case _ => throw new RuntimeException("No merge template declared for: " + mergeName)
    }
  }

  def isMerge(instruction: String): Boolean = instruction != null && instruction.contains("!merge")
}
