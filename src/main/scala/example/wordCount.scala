package example

//import org.apache.spark.mllib.linalg.{Matrices, Matrix}
import org.apache.spark.{SparkConf, SparkContext}

object wordCount extends App {

  override def main(args: Array[String]): Unit = {
    // set hdfs virtualised windows file system. Only applicable for windows file system! Path set to winutils.exe
    System.setProperty("hadoop.home.dir", "C:\\Users\\kmyko\\Downloads\\winutils-master\\hadoop-3.0.0")

    // Initialise spark context
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("CountDemo").set("spark.io.compress.codec", "lzf")
    val sc = new SparkContext(sparkConf)

    // read a file locally
    val rdd = sc.textFile("C:\\Users\\kmyko\\Desktop\\computer architecture\\lab\\src\\test\\hello.txt")
    //      val rdd = scala.io.Source.fromURL("exampletxt.com")
    var counts = rdd.flatMap(line => line.split(" "))
      .map(word => (word, 1))
      .reduceByKey(_ + _)
    counts.saveAsTextFile("C:\\Users\\kmyko\\Desktop\\computer architecture\\lab\\src\\test\\helloresult")
    sc.stop()
  }

}
