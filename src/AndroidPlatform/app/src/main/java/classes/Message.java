package classes;

/**
 *
 * Type: Object Class
 * Defines a Message object. Utilised by MessageAdapter and PairChatActivity
 *
 **/
@SuppressWarnings({"unused"})
public class Message
{
    /**
     *
     * Class Variables
     *
     **/
    private String author, message_contents, message_type, message_receiver, messageID, time, date;

    /**
     *
     * Constructors
     *
     **/
    public Message(){}

    public Message(String author, String message_contents, String message_type,String messageID, String message_receiver, String time, String date)
    {
        this.author = author;
        this.message_contents = message_contents;
        this.message_type = message_type;
        this.messageID = messageID;
        this.message_receiver = message_receiver;
        this.time = time;
        this.date = date;
    }

    /**
     *
     * Getters and Setters
     *
     **/
    public String getMessage_receiver() {
        return message_receiver;
    }

    public void setMessage_receiver(String message_receiver) { this.message_receiver = message_receiver; }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getMessage_contents()
    {
        return message_contents;
    }

    public void setMessage_contents(String message_contents) { this.message_contents = message_contents; }

    public String getMessage_type()
    {
        return message_type;
    }

    public void setMessage_type(String message_type)
    {
        this.message_type = message_type;
    }
}
