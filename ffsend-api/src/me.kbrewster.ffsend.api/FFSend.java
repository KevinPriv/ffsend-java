package me.kbrewster.ffsend.api;

/*
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;*/

import com.google.gson.Gson;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import me.kbrewster.ffsend.api.endpoints.Upload;
import me.kbrewster.ffsend.api.endpoints.response.ClientUploadData;
import sun.rmi.runtime.Log;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FFSend {

    private final static String url = "https://send.firefox.com/api";

    public final static CountDownLatch messageLatch = new CountDownLatch(2);

    public static void main(String[] args) throws Exception {
        upload(new File("/home/kevin/mounts/DriveOne/Development/Development 2019/ffsend/ffsend-api/resources/test.txt"));

    }

    public static void upload(File file) throws Exception {
        upload(file, 300);
    }

    public static void upload(File file, long timeout) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        String uri = "wss://send.firefox.com/api/ws/";
        System.out.println("Connecting to " + uri);

        //encryption of metadata
        SecureRandom sr = new SecureRandom();
        byte[] secret = new byte[16];
        sr.nextBytes(secret);
        byte[] metadata = encrypt(secret, new Gson().toJson(new ClientUploadData.Metadata(file.getName(), file.length(), "text", "TODO")).getBytes());

        //upload data
        ClientUploadData clientUpload = new ClientUploadData(Base64.encode(metadata), "send-v1 " + Base64.encode(secret), 10, 5 * 60);
        //sends upload data using Upload class.
        container.connectToServer(new Upload(file, clientUpload), URI.create(uri));
        messageLatch.await(60, TimeUnit.SECONDS);

    }

    private static byte[] encrypt(byte[] key, byte[] src) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
        byte[] iv = cipher.getIV(); // See question #1
        assert iv.length == 12; // See question #2
        byte[] cipherText = cipher.doFinal(src);
        assert cipherText.length == src.length + 16; // See question #3
        byte[] message = new byte[12 + src.length + 16]; // See question #4
        System.arraycopy(iv, 0, message, 0, 12);
        System.arraycopy(cipherText, 0, message, 12, cipherText.length);
        return message;
    }

}
