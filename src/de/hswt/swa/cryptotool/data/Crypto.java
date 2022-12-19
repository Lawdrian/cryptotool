package de.hswt.swa.cryptotool.data;

import java.io.Serializable;
import java.util.Date;

public class Crypto implements Serializable {

    private String plainText;

    private String cipher;

    private String password;

    private Date date;



    public void setPlainText(String text) {
        plainText = text;
    }

    public void setCipher(String text) {
        cipher = text;
    }

    public void setPassword(String text) {
        password = text;
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
    public String getPassword() {
        return password;
    }

    public Date getDate() {
        return date;
    }

}
