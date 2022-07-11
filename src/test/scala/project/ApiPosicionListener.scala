package project

import javax.jms._
import org.apache.activemq.ActiveMQConnectionFactory
import project.Msg._
import scala.math.pow

//Este objeto representa el Monitor, el cual tiene como objetivo recibir las coordenas del Position Beacon y el RF
// Procesar en base a un criterio arbitrario si el paciente se encuentra dentro o fuera de la zona delimitada
// Y comunicarlo al Relay


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
    var center_x = 0
    var center_y=0
    val listener = new MessageListener {
      def onMessage(message: Message): Unit ={
        message match {
          case text: ObjectMessage => {
            val position = text.getObject.asInstanceOf[PositionMsg]

              val x = position.x
              val y = position.y

              //se considera el espacio delimitado como un circulo
              val radio =4
              if(position.nombre == "RF"){
                center_x = x
                center_y = y
                println(s"El centro del circulo es ($center_x, $center_y)")
              }
              else{
                var status = "Fuera"
                if((pow(x-center_x,2)+pow(y-center_y,2))<=pow(radio,2)){
                  status = "Dentro"
                }
                println("El paciente se encuentra "+status)
                if (status == "Fuera"){
                  val cola_Relay = session.createQueue("mqHost2")
                  val productor = session.createProducer(cola_Relay)
                  val response = new PositionMsg(nombre=position.nombre, x  = position.x , y= position.y)
                  val ObjMessage = session.createObjectMessage(response)
                  productor.send(ObjMessage)
                }
              }
              println(s"Mensaje recibido por parte de : " + position.nombre)

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