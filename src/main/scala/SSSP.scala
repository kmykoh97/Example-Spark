import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Graph, GraphLoader, VertexId}
import org.apache.spark.graphx.util.GraphGenerators
import org.apache.spark.sql.SparkSession

object SSSP {
  def main(args: Array[String]): Unit = {
    // set hdfs virtualised windows file system. Only applicable for windows file system! Path set to winutils.exe
    System.setProperty("hadoop.home.dir", "C:\\Users\\kmyko\\Desktop\\computer architecture\\lab\\driver")

    // disable useless logs
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    // Creates a SparkSession.
    val spark = SparkSession
      .builder
      .appName(s"${this.getClass.getSimpleName}")
      .config("spark.master", "local")
      .getOrCreate()
    val sc = spark.sparkContext

    // A graph with edge attributes containing distances
    val graph = GraphLoader.edgeListFile(sc, "C:\\Users\\kmyko\\Desktop\\computer architecture\\lab\\src\\test\\web-Google.txt")
//    val graph: Graph[Long, Double] =
//    GraphGenerators.logNormalGraph(sc, numVertices = 100).mapEdges(e => e.attr.toDouble)
    println("--------------------------------------------------------------------")
    println("vertices no:"+graph.vertices.count())
    println("edges no:"+graph.edges.count())
    println("--------------------------------------------------------------------")
    val sourceId: VertexId = 3 // The ultimate source
//    val sourceId: VertexId = 11342 // The ultimate source for web-Google.txt
    // Initialize the graph such that all vertices except the root have distance infinity.
    val initialGraph = graph.mapVertices((id, _) =>
      if (id == sourceId) 0.0 else Double.PositiveInfinity)
    val sssp = initialGraph.pregel(Double.PositiveInfinity)(
      (id, dist, newDist) => math.min(dist, newDist), // Vertex Program
      triplet => {  // Send Message
        if (triplet.srcAttr + triplet.attr < triplet.dstAttr) {
          Iterator((triplet.dstId, triplet.srcAttr + triplet.attr))
        } else {
          Iterator.empty
        }
      },
      (a, b) => math.min(a, b) // Merge Message
    )
    println(sssp.vertices.collect.mkString("\n"))

//    sssp.triplets.take(5000).map(println)
//    println("--------------------------------------------------------------------")
//    sssp.vertices.map{case((id,dist)) => if(id < 100) println((id, dist))}.map(println)
//    println("--------------------------------------------------------------------")
//    sssp.vertices.map(v => v._1+" "+v._2).saveAsTextFile("C:\\Users\\kmyko\\Desktop\\computer architecture\\lab\\src\\test\\sssptestresult")

    spark.stop()
  }
}