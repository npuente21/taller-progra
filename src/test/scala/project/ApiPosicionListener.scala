package project

import javax.jms._
import org.apache.activemq.ActiveMQConnectionFactory
import project.Msg._

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
          case text: ObjectMessage => {  //cada mensaje que recibe es un objeto
            val position = text.getObject.asInstanceOf[PositionMsg]  //recibo el msg de RF

            //aquí falta la lógica del Monitor
              val x = position.x
              val y = position.y


              var txtMessage = "Fuera"
              if(x <8 && y<8){
                txtMessage = "Dentro :)"
              }
              val cola_Relay = session.createQueue("mqHost2")
              val productor = session.createProducer(cola_Relay)
              val response = new ResponseMsg(user=position.nombre, status = txtMessage)

              val textMessage = session.createObjectMessage(response)
              productor.send(textMessage)
              println(s"Mensaje recibido por parte de : " + position.nombre)
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