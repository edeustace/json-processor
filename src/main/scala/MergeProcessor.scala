import collection.mutable
import util.matching.Regex

case class Merge(items: mutable.Map[String, String], template: String)

class MergeProcessor {

  private val merges: mutable.Map[String, Merge] = new mutable.HashMap[String, Merge]()

  def createMergeIfNeeded(name: String, template: String) {
    merges.get(name) match {
      case None =>
        merges.put(name, new Merge(new mutable.HashMap[String, String](), template))
      case _ => //do nothing
    }
  }

  def addSubstitutionToMerge(mergeName: String, key: String, value: String) {
    merges.get(mergeName) match {
      case Some(m) => m.items.put(key, value)
      case None => //do nothing
    }
  }

  def processMerges(): List[(String, String)] = {
    def _mapFn( t : (String,Merge) ) = (t._1, processMerge(t._2))
    val list : List[(String,Merge)]= merges.toList
    val out = list.map( _mapFn )
    out
  }

  def processMerge(m: Merge): String = interpolate(m.template, m.items)

  def interpolate(text: String, vars: mutable.Map[String, String]) =
    """\$\{([^}]+)\}""".r.replaceAllIn(text, (_: scala.util.matching.Regex.Match) match {
      case Regex.Groups(v) => vars.getOrElse(v, "")
    })
}
