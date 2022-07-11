package project

import javax.jms._
import org.apache.activemq.ActiveMQConnectionFactory
import project.Msg._

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
    var limit_x = 0
    var limit_y=0
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
                limit_x = x+radio
                limit_y = y+radio
              }
              else{
                var status = "Fuera"
                if(x <= limit_x && y<=limit_y){
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
            println(s"Los límites son ($limit_x, $limit_y)")
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