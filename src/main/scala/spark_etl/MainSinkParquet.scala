package spark_etl

import org.apache.spark.sql._
import spark_etl.model.{OutParquet, Transform}

object MainSinkParquet extends MainTrait {
  // fails if non-parquet outputs are provided
  override def sink(props: Map[String, String], sinkables: Seq[(Transform, DataFrame)])(implicit spark: SparkSession): Unit = {
    val parquetSinks = sinkables.collect { case (t, df) if t.output.`type` == OutParquet => (t.output, df) }
    val other = sinkables.collect { case (t, df) if t.output.`type` != OutParquet => t.output.`type` }
    if (other.nonEmpty)
      throw new Exception(s"Unrecognized output types: ${other.mkString(", ")}")

    parquetSinks.foreach {
      case (output, df) =>
        output.partition_by match {
          case Some(partitions) => df.write.partitionBy(partitions:_*).parquet(output.uri)
          case None => df.write.parquet(output.uri)
        }
    }
  }
}