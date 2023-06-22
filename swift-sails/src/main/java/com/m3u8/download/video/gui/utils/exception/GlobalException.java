package com.m3u8.download.video.gui.utils.exception;

/**
 * @author Small_Tsk
 * @create 2023-06-11
 **/
public class GlobalException extends RuntimeException{

    private String message;

    public GlobalException(String message) {
        this.message = message;
    }

    public GlobalException(String message, String message1) {
        super(message);
        this.message = message1;
    }


}
