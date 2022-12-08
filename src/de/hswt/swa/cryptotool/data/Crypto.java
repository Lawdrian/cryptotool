package de.hswt.swa.cryptotool.data;

import java.util.Date;

public class Crypto {

    private String plainText;

    private String cipher;

    private String key;

    private Date date;



    public void setPlainText(String text) {
        plainText = text;
    }

    public void setCipher(String text) {
        cipher = text;
    }

    public void setKey(String text) {
        key = text;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPlainText() {
        return plainText;
    }
    public String getCipher() {
        return cipher;
    }
    public String getKey() {
        return key;
    }

    public Date getDate() {
        return date;
    }

}
