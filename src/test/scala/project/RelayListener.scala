package project

import javax.jms._
import org.apache.activemq.ActiveMQConnectionFactory
import project.Msg.ResponseMsg

object RelayListener {
  val activeMqUrl: String = "tcp://localhost:61616"
  def main(args: Array[String]): Unit = {
    val cFactory = new ActiveMQConnectionFactory(activeMqUrl)
    cFactory.setTrustAllPackages(true)
    val connection = cFactory.createConnection()
    connection.start()

    val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val cola = session.createQueue("mqHost2")

    val consumidor = session.createConsumer(cola)

    val listener = new MessageListener {
      def onMessage(message: Message): Unit ={
        message match {
          case msg: ObjectMessage => {
            val StatusMsg = msg.getObject.asInstanceOf[ResponseMsg]
            println("Mensaje redirigido")
            val cola_end = session.createQueue("mqHost3")
            val producer = session.createProducer(cola_end)
            val ObjMessage = session.createObjectMessage(StatusMsg)
            producer.send(ObjMessage)
          }
          case _ => {
            throw new Exception("Error desconocido")
          }
        }
      }
    }
    consumidor.setMessageListener(listener)
  }
}