package org.template.classification

import io.prediction.controller.EngineFactory
import io.prediction.controller.Engine

class Query(
  val voice_usage : Double,
  val data_usage : Double,
  val text_usage : Double
) extends Serializable

class PredictedResult(
  val service_plan: Double
) extends Serializable

class ActualResult(
  val service_plan: Double
) extends Serializable

object ClassificationEngine extends EngineFactory {
  def apply() = {
    new Engine(
      classOf[DataSource],
      classOf[Preparator],
      Map("naive" -> classOf[NaiveBayesAlgorithm]),
      classOf[Serving])
  }
}
