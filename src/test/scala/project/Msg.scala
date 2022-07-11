package project



object Msg{
  class PositionMsg (var nombre: String,
                     var x: Int,
                     var y:Int) extends Serializable
}
