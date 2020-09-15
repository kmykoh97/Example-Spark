package example

import org.apache.spark.{SparkConf, SparkContext}

object pageranknative {
  def main(args: Array[String]) {
    // set hdfs virtualised windows file system. Only applicable for windows file system! Path set to winutils.exe
    System.setProperty("hadoop.home.dir", "C:\\Users\\kmyko\\Desktop\\computer architecture\\lab\\driver")

    val sparkConf = new SparkConf().setAppName("PageRank").setMaster("local[*]")
    val iters = if (args.length > 1) args(1).toInt else 100
    val ctx = new SparkContext(sparkConf)
    val lines = ctx.textFile("C:\\Users\\kmyko\\Desktop\\computer architecture\\lab\\src\\test\\Wiki-Vote.txt", 1)
    val links = lines.map{ s =>
      val parts = s.split("\\s+")
      (parts(0), parts(1))
    }.distinct().groupByKey().cache()
    var ranks = links.mapValues(v => 1.0)

    for (i <- 1 to iters) {
      val contribs = links.join(ranks).values.flatMap{ case (urls, rank) =>
        val size = urls.size
        urls.map(url => (url, rank / size))
      }
      ranks = contribs.reduceByKey(_ + _).mapValues(0.15 + 0.85 * _)
    }

    val output = ranks.collect()
    output.foreach(tup => println(tup._1 + " has rank: " + tup._2 + "."))

    ctx.stop()
  }
}
