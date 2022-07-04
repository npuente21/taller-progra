package project

import javax.jms._

import org.apache.activemq.ActiveMQConnectionFactory

import project.Msg.PositionMsg


case object RF {

  val activeMqUrl: String = "tcp://localhost:61616"

  def main(args: Array[String]): Unit = {
    val cFactory = new ActiveMQConnectionFactory(activeMqUrl)
    val connection = cFactory.createConnection()
    connection.start()
    val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val cola = session.createQueue("mqHost1")
    val x = 0
    val y =8
    val productor = session.createProducer(cola)
    val ObjMessage = new PositionMsg(nombre="RF1", x, y)
    val message = session.createObjectMessage(ObjMessage)
    productor.send(message)
    println(s"Mensaje enviado, el centro de la circunferencia es el ($x, $y)")
    connection.close()
}


    //var thread = new MainThread(uuid)
    //thread.start()
}
