package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import Shared.*;

public class ClientHandler extends Thread {

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Properties properties;

    public ClientHandler(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream, Properties properties) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.properties = properties;
    }

    @Override
    public void run() {
        Message receivedMessage;

        System.out.println("Client handler socket open...");

        try {
            receivedMessage = (Message)inputStream.readObject();

            MessageHandler messageHandler = new MessageHandler(receivedMessage, properties);
            Message returnMessage = messageHandler.getReturnMessage();

            outputStream.writeObject(returnMessage);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Client handler socket closed...");
    }
}
