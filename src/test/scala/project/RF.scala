package project

import javax.jms._

import org.apache.activemq.ActiveMQConnectionFactory
import java.util.Calendar
import project.Msg.PositionMsg

//Este componente tiene como función enviar la posición en la cual se encuentra el
//Centro de circunferencia que delimita el área permitida para el paciente.

case object RF {

  val activeMqUrl: String = "tcp://localhost:61616"

  def main(args: Array[String]): Unit = {
    val cFactory = new ActiveMQConnectionFactory(activeMqUrl)
    val connection = cFactory.createConnection()
    connection.start()
    val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val cola = session.createQueue("mqHost1")
    val productor = session.createProducer(cola)
    val x = 5
    val y =8
    val limit_time = 5
    val now = Calendar.getInstance()
    var segundo = now.get(Calendar.SECOND)
    var n=0
    while (n<100){
      val now = Calendar.getInstance()
      val segundo_actual = now.get(Calendar.SECOND)

      if ((segundo_actual-segundo).abs == limit_time && (segundo<(60-limit_time))){

        val ObjMessage = new PositionMsg(nombre="RF", x, y)
        val message = session.createObjectMessage(ObjMessage)
        productor.send(message)
        println(s"Mensaje enviado, el centro de la circunferencia es el ($x, $y)")
        val now = Calendar.getInstance()
        segundo = now.get(Calendar.SECOND)
        n+=1

      }else if(segundo>=(60-limit_time)){
        val time = (60-segundo+segundo_actual).abs
        if(time == limit_time){
          val ObjMessage = new PositionMsg(nombre="RF", x, y)
          val message = session.createObjectMessage(ObjMessage)
          productor.send(message)
          println(s"Mensaje enviado, el centro de la circunferencia es el ($x, $y)")
          val now = Calendar.getInstance()
          segundo = now.get(Calendar.SECOND)
          n+=1
        }
      }

    }
    connection.close()
}


}
