package apiconnector

import apiconnector.ApiRegistry.{ActionPerformed, ApiResponseData}

//#json-formats
import spray.json.DefaultJsonProtocol

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat1(ApiResponseData)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
//#json-formats
