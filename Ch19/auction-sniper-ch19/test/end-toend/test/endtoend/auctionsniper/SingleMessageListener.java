package test.endtoend.auctionsniper;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.hamcrest.Matcher;
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
    
    //Ch12, p.108, revise Ch11
    //Ch16, p.178, revise Ch12
    public void receiveAMessage(Matcher<? super String> messageMatcher) throws InterruptedException {
        //If message is not coming in 5 seconds, exception
        //If message is null (empty message is not null), assert
        //If message is not match, assert
        final Message message = messages.poll(5, TimeUnit.SECONDS);
        //Ch16, p.177, revise Ch12
        assertThat(message, hasProperty("body", messageMatcher));        
    }
}
