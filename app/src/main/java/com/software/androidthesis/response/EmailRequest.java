package com.software.androidthesis.response;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/7 22:01
 * @Decription:
 */
public class EmailRequest {
    private String email;

    public EmailRequest() {}
    public EmailRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
