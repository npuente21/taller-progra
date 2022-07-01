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

    val productor = session.createProducer(cola)
    val ObjMessage = new PositionMsg(nombre="RF1", x=0, y=8)
    val message = session.createObjectMessage(ObjMessage)
    productor.send(message)
    println("Mensaje enviado")
    connection.close()
}


    //var thread = new MainThread(uuid)
    //thread.start()
}
