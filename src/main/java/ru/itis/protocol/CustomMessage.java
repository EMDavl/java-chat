package ru.itis.protocol;


import ru.itis.exceptions.IllegalMessageTypeException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CustomMessage {

    public static final int TEXT_MESSAGE = 1;
    public static final int SERVICE_MESSAGE = 2;
    public static final int DISCONNECT_MESSAGE = 4;
    public static final int GREET_MESSAGE = 8;

    public static final byte[] START_BYTES = {0xA, 0xB};
    protected static final int MAX_DATA_LENGTH = 1000;

    public static CustomMessage createMessage(int type, String message)
            throws IllegalArgumentException {
        byte[] messageValue = message.getBytes(StandardCharsets.UTF_8);

        checkMessageType(type);
        checkMessageLength(messageValue.length);

        return new CustomMessage(type, message);
    }

    public static CustomMessage getFromInputStream(InputStream inputStream){
        try {
            byte[] buffer = new byte[MAX_DATA_LENGTH];
            inputStream.read(buffer, 0, 2);
            if(!Arrays.equals(Arrays.copyOfRange(buffer, 0, 2), START_BYTES)){
                throw new IllegalArgumentException("Wrong start bytes");
            }
            inputStream.read(buffer, 0, 4);
            int messageType = ByteBuffer.wrap(buffer, 0, 4).getInt();
            checkMessageType(messageType);

            inputStream.read(buffer, 0, 4);
            int messageLength = ByteBuffer.wrap(buffer, 0, 4).getInt();

            checkMessageLength(messageLength);

            inputStream.read(buffer, 0, messageLength);
            String messageValue = new String(buffer, 0, messageLength, StandardCharsets.UTF_8);

            return new CustomMessage(messageType, messageValue);

        } catch (IOException e) {
            throw new IllegalArgumentException("Can not read message from input stream");
        }
    }


    private static void checkMessageLength(int messageLength)
            throws IllegalArgumentException {
        if(messageLength > MAX_DATA_LENGTH){
            throw new IllegalArgumentException("Message too large." +
                    " Data length must be less than " + MAX_DATA_LENGTH);
        }
    }

    private static void checkMessageType(int type)
            throws IllegalMessageTypeException{
        if((type &
                (TEXT_MESSAGE
                        | SERVICE_MESSAGE
                        | DISCONNECT_MESSAGE
                        | GREET_MESSAGE)) == 0){
            throw new IllegalMessageTypeException();
        }
    }

    private final String messageText;
    private final int type;

    CustomMessage(int type, String messageText){
        this.type = type;
        this.messageText = messageText;
    }

    // data - contains all data about message, including
    // start bytes, message type, message length and message value
    // 2 bytes - start, 4 - for message type, 4 for message length, up to 990 for value;
    public byte[] getBytes() {
        byte[] messageValue = messageText.getBytes(StandardCharsets.UTF_8);
        int rawMessageLength = START_BYTES.length + 4 + 4 + messageValue.length;

        checkMessageType(type);
        checkMessageLength(rawMessageLength);

        int j = 0;
        byte[] data = new byte[rawMessageLength];

        for(byte b : START_BYTES){
            data[j++] = b;
        }
        for(byte b : ByteBuffer.allocate(4).putInt(type).array()){
            data[j++] = b;
        }
        for (byte b : ByteBuffer.allocate(4).putInt(messageValue.length).array()){
            data[j++] = b;
        }
        for (byte b : messageValue){
            data[j++] = b;
        }

        return data;
    }

    public int getType() {
        return type;
    }
    public String getMessageText(){
        return messageText;
    }
}
