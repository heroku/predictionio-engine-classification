package org.template.classification

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.classification.NaiveBayesModel

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NaiveBayesAlgorithmTest
  extends FlatSpec with EngineTestSparkContext with Matchers {

  val params = AlgorithmParams(lambda = 10)
  val algorithm = new NaiveBayesAlgorithm(params)

  val dataSource = Seq(
    LabeledPoint(0, Vectors.dense(1000, 10, 10)),
    LabeledPoint(1, Vectors.dense(10, 1000, 10)),
    LabeledPoint(2, Vectors.dense(10, 10, 1000))
  )

  "train" should "return NaiveBayes model" in {
    val dataSourceRDD = sc.parallelize(dataSource)
    val preparedData = new PreparedData(labeledPoints = dataSourceRDD)
    val model = algorithm.train(sc, preparedData)
    model shouldBe a [NaiveBayesModel]
  }

  // val model = new CooccurrenceModel(
  //   //
  //   topCooccurrences = Map(
  //     0 -> Array((1, 4), (2, 1)),
  //     1 -> Array((0, 4), (2, 3), (3, 1)),
  //     2 -> Array((1, 3), (3, 2), (0, 1)),
  //     3 -> Array((2, 2), (1, 1))
  //   ),
  //   itemStringIntMap = BiMap(Map(
  //     "i0" -> 0,
  //     "i1" -> 1,
  //     "i2" -> 2,
  //     "i3" -> 3
  //   )),
  //   items = Map(
  //     0 -> Item(categories = Some(List("c0", "c1"))),
  //     1 -> Item(categories = None),
  //     2 -> Item(categories = Some(List("c0", "c2"))),
  //     3 -> Item(categories = Some(List("c0,", "c2", "c3")))
  //   )
  // )

}