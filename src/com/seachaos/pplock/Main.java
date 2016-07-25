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
	
	public static void cli(final String cmd){
		new Thread(new Runnable(){
			@Override
			public void run() {
				_cli(cmd);
			}
		}).start();
	}
	
	public static void _cli(String cmd){
		Runtime rt = Runtime.getRuntime();
		Process proc;
		try {
			proc = rt.exec(cmd);
			final BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			final StringBuilder sb = new StringBuilder();
			boolean keepRun = true;
			while(keepRun){
				sleep(10);
				try{
					new Thread(new Runnable(){
						@Override
						public void run() {
							try {
								String s = null;
								while((s = stdInput.readLine())!=null){
									debug(s);
									sb.append(s);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}						
					}).start();
					int code = proc.exitValue();
					debug("get exit code:" + code);
					keepRun = false;
				}catch(Exception e){
				}
			}
			debug("final sb:" + sb.toString());
		} catch (IOException e) {
			error(e.toString());
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
		cli("java -jar ./test/sleep.jar demo");
	}
}
