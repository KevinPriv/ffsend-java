package me.kbrewster.ffsend.api;

import com.google.gson.annotations.SerializedName;

public class ClientUploadData {

    public ClientUploadData(String fileMetadata, String authorization, int downloadLimit, long timeLimit) {
        this.fileMetadata = fileMetadata;
        this.authorization = authorization;
        this.downloadLimit = downloadLimit;
        this.timeLimit = timeLimit;
    }

    @SerializedName("fileMetadata")
    private String fileMetadata;
    @SerializedName("authorization")
    private String authorization;
    @SerializedName("dLimit")
    private int downloadLimit;
    @SerializedName("timeLimit")
    private long timeLimit;

    /**
     * TODO
     */
    public static class Metadata {
        private String name;
        private long size;
        private String type = "application/octet-stream";

        public Metadata(String name, long size, String type, String manifest) {
            this.name = name;
            this.size = size;
            this.type = type;
            this.manifest = manifest;
        }

        private String manifest = "";

    }
}
