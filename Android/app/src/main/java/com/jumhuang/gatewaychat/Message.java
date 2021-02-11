package com.jumhuang.gatewaychat;

public class Message
{
    private String avatar;
    private String clientId;
    private String message;
    private boolean isMine;

    public Message(String avatar, String clientId, String message, boolean isMine)
    {
        this.avatar = avatar;
        this.clientId = clientId;
        this.message = message;
        this.isMine = isMine;
    }

    public void setIsMine(boolean isMine)
    {
        this.isMine = isMine;
    }

    public boolean isMine()
    {
        return isMine;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    public String getClientId()
    {
        return clientId;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }
}
