package me.kbrewster.ffsend.api;


import com.google.gson.Gson;

import javax.websocket.*;
import java.io.*;
import java.nio.ByteBuffer;

@ClientEndpoint
public class Upload {

    private Session session;

    private static Gson gson = new Gson();

    private ClientUploadData uploadData;
    private File file;

    public Upload(File file, ClientUploadData uploadData) {
        this.file = file;
        this.uploadData = uploadData;
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to endpoint: " + session.getBasicRemote());
        try {
            String uploadDataJson = gson.toJson(uploadData);
            System.out.println("Sending message to endpoint: " + uploadDataJson);
            session.getBasicRemote().sendText(uploadDataJson);
        } catch (IOException ex) {
            // :)
        }
    }

    @OnMessage
    public void processMessage(String message) throws IOException {
        System.out.println("Received message in client: " + message);
        if(FFSend.messageLatch.getCount() != 1) {
            try (FileInputStream fis = new FileInputStream(file)) {
                int i = 0;
                do {

                    byte[] buf = new byte[1024 * 64]; // CHUNCKS OF 64KB
                    i = fis.read(buf);

                    session.getBasicRemote().sendBinary(ByteBuffer.wrap(buf));
                } while (i != -1);
            }
            session.getBasicRemote().sendBinary(ByteBuffer.wrap(new byte[1]));
        } else {
            session.close();
        }
        FFSend.messageLatch.countDown();
    }


    @OnError
    public void processError(Throwable t) {
        t.printStackTrace();
    }
}
