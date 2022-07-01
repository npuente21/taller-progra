package project

class PositionMsg (var nombre: String,
              var x: Int,
              var y:Int) extends Serializable{
  def imprimir(): Unit ={
    println(s"$nombre, $x, $y")
  }
}

class ResponseMsg (var user: String, var status: String) extends Serializable