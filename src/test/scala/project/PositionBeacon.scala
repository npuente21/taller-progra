package project
import javax.jms._
import org.apache.activemq.ActiveMQConnectionFactory
import project.Msg.PositionMsg
import java.util.Calendar
//Este componente simula el dispositivo que tiene cada paciente, el cual está encargado de enviar asincronamente
//Las coordenadas del paciente al Monitor

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
    val limit_time = 3
    val now = Calendar.getInstance()
    var segundo = now.get(Calendar.SECOND)
    val productor = session.createProducer(cola)

    var x =0
    while(x<100){
      val now = Calendar.getInstance()
      val segundo_actual = now.get(Calendar.SECOND)
      if ((segundo_actual-segundo).abs == limit_time && (segundo<(60-limit_time))){

        val r1 = r.nextInt(15)
        val r2 = r.nextInt(15)
        val Position = new PositionMsg(nombre = user, x =r1, y=r2)
        val ObjectMessage = session.createObjectMessage(Position)
        productor.send(ObjectMessage)
        println(s"Mensaje enviado, la posición del pacientes es: ($r1, $r2)")
        x+=1
        val now = Calendar.getInstance()
        segundo = now.get(Calendar.SECOND)

      }else if(segundo>=(60-limit_time)){
        val time = (60-segundo+segundo_actual).abs
        if(time == limit_time){

          val r1 = r.nextInt(15)
          val r2 = r.nextInt(15)
          val Position = new PositionMsg(nombre = user, x =r1, y=r2)
          val ObjectMessage = session.createObjectMessage(Position)
          productor.send(ObjectMessage)
          println(s"Mensaje enviado, la posición del pacientes es: ($r1, $r2)")
          x+=1
          val now = Calendar.getInstance()
          segundo = now.get(Calendar.SECOND)
        }
      }

    }
    connection.close()

  }
}

