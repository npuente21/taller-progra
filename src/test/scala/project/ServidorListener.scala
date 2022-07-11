package project
import org.apache.activemq.ActiveMQConnectionFactory
import project.Msg._
import scalaj.http.{Http,HttpOptions}
import javax.jms._

//Este componente actua como el servidor del hospital, el cual es el encargado de mantener
//la persistencia de las "escapadas" del paciente.

object ServidorListener {
  val activeMqUrl: String = "tcp://localhost:61616"
  val server_url = "https://6pxvyrkegj.execute-api.us-east-1.amazonaws.com/APIChill/escape"
  def main(args: Array[String]): Unit = {
    val cFactory = new ActiveMQConnectionFactory(activeMqUrl)
    cFactory.setTrustAllPackages(true)
    val connection = cFactory.createConnection()
    connection.start()

    val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val cola = session.createQueue("mqHost4")

    val consumidor = session.createConsumer(cola)

    val listener = new MessageListener {
      def onMessage(message: Message): Unit ={
        message match {
          case msg: ObjectMessage => {
            val StatusMsg = msg.getObject.asInstanceOf[PositionMsg]
            //post to AWS

            val result = Http(server_url).postData(s"""{"name":${StatusMsg.nombre},"coord":[${StatusMsg.x},${StatusMsg.y}]}""")
              .header("Content-Type", "application/json")
              .header("Charset", "UTF-8")
              .option(HttpOptions.readTimeout(10000)).asString

            if (result.code == 200){
              println(result.body)
            }else{
              println("Ha ocurrido un error :c")
            }
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
