package project

import javax.jms._
import org.apache.activemq.ActiveMQConnectionFactory
import project.Msg.ResponseMsg

//Este componente tiene como funcionalidad recibir el mensaje proporcionado por el monitor y
//Redistribuirlo a las distintas áreas del hospital

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
            var lista:List[String] = List ("mqHost3", "mqHost4", "mqHost5")
            lista.foreach(
              queue =>{
                val cola_enf = session.createQueue(queue)
                val producer = session.createProducer(cola_enf)
                val ObjMessage = session.createObjectMessage(StatusMsg)
                producer.send(ObjMessage)
              }
            )

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