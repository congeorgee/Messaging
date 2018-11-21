package sample;
import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.print.attribute.standard.Destination;

import com.solacesystems.jcsmp.Context;
import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;
import com.solacesystems.jms.SupportedProperty;

/**
 * Sends a persistent message to a queue using Solace JMS API implementation.
 * 
 * The queue used for messages is created on the message broker.
 */
public class QueueProducer {

    final String QUEUE_NAME = "FeedtoSyn2";

    public void run(String... args) throws Exception {

    	  System.out.printf("QueueProducerJNDI is connecting to Solace messaging at " );

          // setup environment variables for creating of the initial context
          Hashtable<String, Object> env = new Hashtable<String, Object>();
          // use the Solace JNDI initial context factory
          env.put(InitialContext.INITIAL_CONTEXT_FACTORY, "com.solacesystems.jndi.SolJNDIInitialContextFactory");
    
          // assign Solace message router connection parameters
          env.put(InitialContext.PROVIDER_URL, "tcp://mr7jh6pyetbt.messaging.solace.cloud:20032");
          env.put(javax.naming.Context.SECURITY_PRINCIPAL, "solace-cloud-client" + '@' + "msgvpn-7jh6q1osan"); // Formatted as user@message-vpn
          env.put(javax.naming.Context.SECURITY_CREDENTIALS, "tguo1vvebkr1h7sg408fkftrdh");

          // Create the initial context that will be used to lookup the JMS Administered Objects.
          InitialContext initialContext = new InitialContext(env);
          // Lookup the connection factory
          ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("/jms/cf/default");

          // Create connection to the Solace router
          Connection connection = connectionFactory.createConnection();

          // Create a non-transacted, client ACK session.
          Session session = connection.createSession(false, SupportedProperty.SOL_CLIENT_ACKNOWLEDGE);

          System.out.printf("Connected to the Solace Message VPN ");
          // Lookup the queue.
          Queue queue = (Queue) initialContext.lookup("FeedtoSyn");

          // From the session, create a consumer for the destination.
          MessageProducer messageProducer = session.createProducer(queue);
          TextMessage message = session.createTextMessage("Hello world Queues!");
          messageProducer.send(queue, message, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY,
                  Message.DEFAULT_TIME_TO_LIVE);
          System.out.println("Sent successfully. Exiting...");
          messageProducer.close();
          session.close();
          connection.close();
    }

    public static void main(String... args) throws Exception {
         new QueueProducer().run(args);
    }
}