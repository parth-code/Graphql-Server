import sangria.macros.derive._
import sangria.schema._


object SchemaDef {
//  trait -> val
//  type -> case class

//  query Product($productId: String!) {
//  product(id: $productId) {
//     name
//     description
//  }
//}
  case class Picture(width: Int, height:Int, url: Option[String])

  trait Identifiable{def id:String}

  case class Product(id:String, name:String, desc:String) extends Identifiable{
    @GraphQLField
    def picture(size:Int):Picture = Picture(width = size, height = size, url = Some(s"//cdn.com/$size/$id.jpg"))
  }

  val IdentifiableType: InterfaceType[Unit, Identifiable] = InterfaceType(
    "Identifiable",
    "Unique id",
    fields[Unit, Identifiable](
      Field("id", StringType, resolve = _.value.id)
    )
  )

  implicit val PictureType = ObjectType(
    "Picture",
    "Product picture",
    fields[Unit, Picture](
      Field("width", IntType, resolve = _.value.width),
      Field("height", IntType, resolve = _.value.height),
      Field("url", OptionType(StringType), resolve = _.value.url)
    )
  )

  implicit val ProductType = deriveObjectType[Unit, Product](
    Interfaces(IdentifiableType),
    IncludeMethods("picture")
  )

  val Id = Argument("id", StringType)

  val QueryType = ObjectType(
    "Query",
    fields[ProductRepo, Unit](
      Field(
        "product",
        OptionType(ProductType),
        description = Some("Returns a product with specific id"),
        arguments = Id :: Nil,
        resolve = c =>c.ctx.product(c.arg(Id))),
      Field(
        "products",
        ListType(ProductType),
        description = Some("Returns a list of all available products."),
        resolve = _.ctx.products)
    )
  )

  val ProductSchema = Schema(
    query = QueryType
  )
//  val ProductType = ObjectType(
//    "Product",
//    "Product info",
//    interfaces[Unit, Identifiable](IdentifiableType),
//    fields[Unit, Product](
//      Field("name", StringType,resolve = _.value.name),
//      Field("desc", StringType, resolve = _.value.desc)
//    )
//     //Interfaces
////    IncludeMethods("picture")
//  )



}
