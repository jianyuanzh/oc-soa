package cc.databus.user.response;

import java.io.Serializable;

public class Response implements Serializable {
    private int code;
    private String message;

    public Response() {
        this(0, "OK");
    }

    public Response(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Response exception(Exception e) {
        return new Response(9999, e.getMessage());
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
