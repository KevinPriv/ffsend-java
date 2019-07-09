package me.kbrewster.ffsend.api.endpoints.response;

import com.google.gson.annotations.SerializedName;

public class ServerUploadData {
    @SerializedName("id")
    private String id;
    @SerializedName("ownerToken")
    private String ownerToken;
    @SerializedName("url")
    private String url;

    public ServerUploadData(String id, String ownerToken, String url) {
        this.id = id;
        this.ownerToken = ownerToken;
        this.url = url;
    }
}
