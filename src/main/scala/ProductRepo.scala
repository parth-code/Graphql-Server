import SchemaDef.Product

class ProductRepo {

  //In-memory storage
  private val Products = List(
    Product("1", "A", "abc"),
    Product("2", "B", "def")
  )

//  Get single product
  def product(id:String):Option[Product] = Products find(id == _.id)
//  Get all items
  def products:List[Product] = Products


}
