package project

import javax.jms._
import org.apache.activemq.ActiveMQConnectionFactory
import project.Msg._

//Este componente actua como la estación de enfermeria del hospital, la cual actuaría acorde a la
//información recibida.

object EnfermeriaListener {
  val activeMqUrl: String = "tcp://localhost:61616"
  def main(args: Array[String]): Unit = {
    val cFactory = new ActiveMQConnectionFactory(activeMqUrl)
    cFactory.setTrustAllPackages(true)
    val connection = cFactory.createConnection()
    connection.start()

    val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val cola = session.createQueue("mqHost3")

    val consumidor = session.createConsumer(cola)

    val listener = new MessageListener {
      def onMessage(message: Message): Unit ={
        message match {
          case msg: ObjectMessage => {
            val StatusMsg = msg.getObject.asInstanceOf[ResponseMsg]
            println(s"Mensaje recibido en Enfermeria, el paciente ${StatusMsg.user} se encuentra ${StatusMsg.status} del área delimitada")
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
