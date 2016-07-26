package com.seachaos.pplock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PPLock {

	public static interface PPLockCallback{
		public void onOutput(String msg, StringBuilder sb);
		public void onFinish(int exitCode, StringBuilder sb);
	}
	
	public static final int
		OUPUT_MODE_STD = 0,
		OUPUT_MODE_ERR = 1;
	
	public PPLockCallback callback = new PPLockCallback(){
		public void onOutput(String msg, StringBuilder sb) { }
		public void onFinish(int exitCode, StringBuilder sb){ }
	};
	public int outputMode = OUPUT_MODE_STD;
	private Thread outputWatcher = null;
	boolean keepRun = true;
	
	public PPLock(){
		
	}
	
	public PPLock(String cmd, PPLockCallback pcallback){
		this.callback = pcallback;
		run(cmd);
	}
	public PPLock(String cmd, int mode, PPLockCallback pcallback){
		this.callback = pcallback;
		this.outputMode = mode;
		run(cmd);
	}
	
	
	public void run(final String cmd){
		new Thread(new Runnable(){
			@Override
			public void run() {
				_cli(cmd);
			}
		}).start();
	}
	
	private void sleep(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private BufferedReader getInputStream(Process proc){
		InputStream stream = null;
		if(outputMode==OUPUT_MODE_ERR){
			stream = proc.getErrorStream();
		}else{
			stream = proc.getInputStream();
		}
		return new BufferedReader(new InputStreamReader(stream));
	}
	
	private void _cli(String cmd){
		Runtime rt = Runtime.getRuntime();
		try {
			Process proc = rt.exec(cmd);
			final BufferedReader input = getInputStream(proc);
			final StringBuilder sb = new StringBuilder();
			while(keepRun){
				sleep(10);
				newCatchOutputThread(input, sb);
				try{
					int code = proc.exitValue();
					keepRun = false;
					callback.onFinish(code, sb);
				}catch(IllegalThreadStateException e){
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void newCatchOutputThread(final BufferedReader input,
			final StringBuilder sb) {
		if(outputWatcher != null){
			return;
		}
		outputWatcher = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					String s = null;
					while((s = input.readLine())!=null){
						sb.append(s);
						callback.onOutput(s, sb);
						if(!keepRun){
							return;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}						
		});
		outputWatcher.start();
	}
}
