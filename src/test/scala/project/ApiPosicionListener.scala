package project

import javax.jms._
import org.apache.activemq.ActiveMQConnectionFactory
import project.PositionMsg

object ApiPosicionListener {
  val activeMqUrl: String = "tcp://localhost:61616"
  def main(args: Array[String]): Unit = {
    val cFactory = new ActiveMQConnectionFactory(activeMqUrl)
    cFactory.setTrustAllPackages(true)
    val connection = cFactory.createConnection()
    connection.start()

    val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val cola = session.createQueue("mqHost1")

    val consumidor = session.createConsumer(cola)

    val listener = new MessageListener {
      def onMessage(message: Message): Unit ={
        message match {
          case text: ObjectMessage => {
            val RF_position = text.getObject.asInstanceOf[PositionMsg]

              val x = RF_position.x
              val y = RF_position.y
              var txtMessage = "Fuera"
              if(x <8 && y<8){
                txtMessage = "Dentro :)"
              }
              val productor = session.createProducer(cola)
              val textMessage = session.createTextMessage(txtMessage)
              productor.send(textMessage)
              println(s"Mensaje recibido por parte de : " + RF_position.nombre)
              println(s"Mensaje procesado: " + txtMessage)
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