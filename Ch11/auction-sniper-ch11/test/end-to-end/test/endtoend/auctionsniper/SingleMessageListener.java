package test.endtoend.auctionsniper;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

//Ch11, p.94
public class SingleMessageListener implements MessageListener {
    private final ArrayBlockingQueue<Message> messages = 
            new ArrayBlockingQueue<Message>(1);
    
    public void processMessage(Chat chat, Message message) {
        messages.add(message);
    }
    
    public void receiveAMessage() throws InterruptedException {
    	//If message is not coming in 5 seconds, exception
    	//If message is null (empty message is not null), assert
        assertThat("Message", messages.poll(5, TimeUnit.SECONDS), is(notNullValue()));
    }
}
