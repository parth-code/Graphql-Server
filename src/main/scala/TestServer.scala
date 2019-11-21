import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import sangria.ast.Document
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.parser.QueryParser
import spray.json.{JsObject, JsString, JsValue, _}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.io.StdIn
import scala.util.{Failure, Success}

import sangria.execution.deferred.{DeferredResolver, Fetcher, Relation, RelationIds}
import sangria.schema._

object TestServer extends App with DefaultJsonProtocol{
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  //implicit val unmarshaller:Unmarshaller[RequestEntity, JsObject] =
  //Test String
  val jsonObject:String =
    "{\n    \"glossary\": {\n        \"title\": \"example glossary\",\n\t\t\"GlossDiv\": {\n            \"title\": \"S\",\n\t\t\t\"GlossList\": {\n                \"GlossEntry\": {\n                    \"ID\": \"SGML\",\n\t\t\t\t\t\"SortAs\": \"SGML\",\n\t\t\t\t\t\"GlossTerm\": \"Standard Generalized Markup Language\",\n\t\t\t\t\t\"Acronym\": \"SGML\",\n\t\t\t\t\t\"Abbrev\": \"ISO 8879:1986\",\n\t\t\t\t\t\"GlossDef\": {\n                        \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\n\t\t\t\t\t\t\"GlossSeeAlso\": [\"GML\", \"XML\"]\n                    },\n\t\t\t\t\t\"GlossSee\": \"markup\"\n                }\n            }\n        }\n    }\n}"

//Marshalling and Unmarshalling
  def endpoint(requestEntity:JsValue)(implicit e: ExecutionContext): Route = {

    val s:JsObject = requestEntity.asJsObject()

    val JsString(query) = s.fields("query")

    val operation = s.fields.get("operationName") collect {
      case JsString(op) ⇒ op
    }

    val variables: JsObject= s.fields.get("variables") match {
      case Some(obj: JsObject) => obj
      case _ => JsObject.empty
    }

    QueryParser.parse(query) match{
      case Success(value) =>
        complete(sendResponse(value, variables, operation))
      case Failure(exception)=>
        complete(BadRequest, JsObject("error" -> JsString(exception.getMessage)))
    }


  }
  def sendResponse(query:Document,vars:JsObject, opn: Option[String])(implicit ec: ExecutionContext) ={
    Executor.execute(
      schema = SchemaDef.ProductSchema,
      queryAst = query,
      variables = vars,
      operationName = opn
    ).map(OK -> _)
      .recover{
      case error: QueryAnalysisError => BadRequest -> error.resolveError
      case error: ErrorWithResolver => InternalServerError -> error.resolveError
      }
  }
/*
  URL: https://scalac.io/akka-http-sangria-graphql-backend/
  Config
  Every POST to /graphql endpoint is delegated to endpoint
 */

  val route:Route = (post & path("graphql")){
    entity(as[JsValue]){
      requestJson => endpoint(requestJson)
    }
  }

  val binding = Http().bindAndHandle(route, "localhost", 8080)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  binding
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
