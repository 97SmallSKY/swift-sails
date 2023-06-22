package com.m3u8.download.video.utils.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Small_Tsk
 * @create 2022-11-19
 **/
// An highlighted block
public class ExecuteCmdCommand {
    public static void main(String[] args) {
        ExecuteCmdCommand s = new ExecuteCmdCommand();

        String command = "";
        System.out.println(command);
        s.exec(command);
    }
    public static void exec(String command){
        Runtime run =Runtime.getRuntime();
        try {
            Process p = run.exec(command);
            InputStream ins= p.getInputStream();
            InputStream ers= p.getErrorStream();
            new Thread(new inputStreamThread(ins)).start();
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
   static class  inputStreamThread implements Runnable{
        private InputStream ins = null;
        private BufferedReader bfr = null;
        public inputStreamThread(InputStream ins){
            this.ins = ins;
            this.bfr = new BufferedReader(new InputStreamReader(ins));
        }
        @Override
        public void run() {
            String line = null;
            byte[] b = new byte[100];
            int num = 0;
            try {
                while((num=ins.read(b))!=-1){
                    System.out.println(new String(b,"gb2312"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


