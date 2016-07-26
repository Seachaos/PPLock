package com.seachaos.pplock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
	public static void debug(String msg){
		System.out.println(msg);
	}
	
	public static void error(String msg){
		System.err.println(msg);
	}
	
	public static void sleep(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args){
		if(args.length>0){
			debug("arg 0 is:" + args[0]);
			if(args[0].equals("demo")){
				for(int ax=0;ax<5;ax++){
					long sleepTime = (long)(( 1.f + Math.random() * 2.f ) * 1000);
					debug("step:" + ax + "/" + sleepTime);
					sleep(sleepTime);
				}
				System.exit(0);
				return;
			}
		}
		new Main();
	}

	Main(){
		// do test
		new PPLock("java -jar ./test/sleep.jar demo", PPLock.OUPUT_MODE_STD, new PPLock.PPLockCallback(){
			@Override
			public void onOutput(String msg, StringBuilder sb) {
				debug("onOutput:" + msg);
			}

			@Override
			public void onFinish(int exitCode, StringBuilder sb) {
				debug("---------");
				debug("final string:" + sb.toString());
			}			
		});
	}
}
