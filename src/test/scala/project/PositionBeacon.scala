package project
import javax.jms._
import org.apache.activemq.ActiveMQConnectionFactory
import project.Msg.PositionMsg

case object PositionBeacon{
  val activeMqUrl: String = "tcp://localhost:61616"
  val r = new scala.util.Random(100)

  def main(args: Array[String]): Unit = {
    val cFactory = new ActiveMQConnectionFactory(activeMqUrl)
    val connection = cFactory.createConnection()
    connection.start()
    val user = "Paciente 1"
    val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val cola = session.createQueue("mqHost1")

    val productor = session.createProducer(cola)

    var x =0
    while(x<12){
      val r1 = r.nextInt(11)
      val r2 = r.nextInt(11)

      val Position = new PositionMsg(nombre = user, x =r1, y=r2)

      val ObjectMessage = session.createObjectMessage(Position)
      productor.send(ObjectMessage)
      println("Mensaje enviado")
      x+=1
    }
    connection.close()

  }
}

