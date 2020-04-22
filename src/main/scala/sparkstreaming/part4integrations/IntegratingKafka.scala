package sparkstreaming.part4integrations

import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SparkSession}

object IntegratingKafka {

  val articlesSchema = StructType(Array(
    StructField("article_id", StringType),
    StructField("title", StringType),
    StructField("abstract", StringType)
  ))


  val spark = SparkSession.builder()
    .appName("Kafka in Spark Streaming")
    .master("local[*]")
    .getOrCreate()

  def readFromKafka() = {
    val kafkaDF: DataFrame = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "articles")
      .load()

    kafkaDF.select(col("topic"), expr("cast(value as string) as actualValue"))
      .writeStream
      .format("console")
      .outputMode("append")
      .start()
      .awaitTermination()
  }

  def writeFieldToKafka() = {
    val articlesDF = spark.readStream
      .schema(articlesSchema)
      .json("src/main/resources/data/articles")

    //transform the data into a format that Kafka understands
    val articlesKafkaDF = articlesDF.selectExpr("upper(title) as key", "title as value")

    articlesKafkaDF.writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("topic", "articles")
      .option("checkpointLocation", "checkpoints")
      .start()
      .awaitTermination()
  }

  def writeJSONToKafka() = {

    val articlesDF = spark.readStream
      .schema(articlesSchema)
      .json("src/main/resources/data/articles")

    val articlesKafkaDF = articlesDF.select(
      col("article_id").as("key"),
      to_json(struct(col("article_id"), col("title"))).cast("String").as("value")
    )

    articlesKafkaDF.writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("topic", "articles")
      .option("checkpointLocation", "checkpoints")
      .start()
      .awaitTermination()
  }

  def main(args: Array[String]): Unit = {
//    readFromKafka()
//    writeFieldToKafka()

    writeJSONToKafka()
  }

}
