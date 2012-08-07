import org.specs2.mutable.Specification

class MergeProcessorSpec extends Specification {

  "MergeProcessor" should {

    "correctly merge" in {

      val mp : MergeProcessor = new MergeProcessor()

      mp.createMergeIfNeeded("out", "${a}")
      mp.addSubstitutionToMerge("out", "a", "apple")

      val out = mp.processMerges()

      println(out)

      out(0) must  be equalTo ( ("out", "apple"))
    }

    "correctly merge with multiple substitutions" in {
      val mp : MergeProcessor = new MergeProcessor()
      mp.createMergeIfNeeded("out", "${a},${b},${c}")

      mp.addSubstitutionToMerge("out", "a", "apple")
      mp.addSubstitutionToMerge("out", "b", "banana")
      mp.addSubstitutionToMerge("out", "c", "carrot")

      val out = mp.processMerges()

      out(0) must be equalTo( ("out", "apple,banana,carrot") )
    }

    "correctly merge with multiple merges" in {
      val mp : MergeProcessor = new MergeProcessor()
      mp.createMergeIfNeeded("out", "${a}")
      mp.createMergeIfNeeded("merge2", "${_2},${_3}")


      mp.addSubstitutionToMerge("out", "a", "apple")
      mp.addSubstitutionToMerge("merge2", "_2", "_two")
      mp.addSubstitutionToMerge("merge2", "_3", "_three")

      val out = mp.processMerges()

      out(0) must be equalTo( ("out", "apple") )
      out(1) must be equalTo( ("merge2", "_two,_three") )
    }
  }

}
