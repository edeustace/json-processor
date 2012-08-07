object JsonProcessorCli {

  def main(args: Array[String]) {
    println("JsonProcessorCli : Version : 0.0.1")

    args.length match {
      case 3 => JsonProcessor.process(args(0), args(1), args(2))
      case _ => println("Usage: inputFile.json, mappingDefinition.json, outputFilename")
    }
  }
}
