package controllers

import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONObjectID, BSONDocument}
import repos.WidgetRepoImpl

object WidgetFields {
  val Id = "_id"
  val Name ="name"
  val Description = "description"
  val Author = "author"
}


class Widgets @Inject()(val reactiveMongoApi: ReactiveMongoApi) extends Controller
    with MongoController with ReactiveMongoComponents {

  import controllers.WidgetFields._

  def widgetRepo = new WidgetRepoImpl(reactiveMongoApi)

  def create = Action.async(BodyParsers.parse.json) { implicit request =>
    println("inside create")
    val name = (request.body \ Name).as[String]
    val description = (request.body \ Description).as[String]
    val author = (request.body \ Author).as[String]
    widgetRepo.save(BSONDocument(
      Name -> name,
      Description -> description,
      Author -> author
    )).map(result => Created)
  }

  def update(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    println("inside update")
    val name = (request.body \ Name).as[String]
    val description = (request.body \ Description).as[String]
    val author = (request.body \ Author).as[String]
    widgetRepo.update(BSONDocument(Id -> BSONObjectID(id)),
      BSONDocument("$set" -> BSONDocument(Name -> name, Description -> description, Author -> author)))
        .map(result => Accepted)
  }

  def index = Action.async { implicit request =>
    println("inside index")
    widgetRepo.find().map(widgets => Ok(Json.toJson(widgets)))
  }

  def read(id: String) = Action.async { implicit request =>
    println("inside read")
    widgetRepo.select(BSONDocument(Id -> BSONObjectID(id))).map(widget => Ok(Json.toJson(widget)))
  }

  def delete(id: String) = Action.async {
    println("inside delete")
    widgetRepo.remove(BSONDocument(Id -> BSONObjectID(id)))
      .map(result => Accepted)
  }


}
