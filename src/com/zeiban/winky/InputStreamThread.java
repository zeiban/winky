package com.zeiban.winky;


import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class InputStreamThread extends Thread {
	private Logger logger = Logger.getLogger(Main.LOG_FILENAME);
	private OutputStream os;
	private BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	public InputStreamThread(String name, OutputStream os) {
		this.setName(name);
		this.os = os;
		this.start();
	}
	public void put(String line) {
		try {
			queue.put(line);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		
		try {
			while(true) {
				try {
					String line = queue.take();
					os.write((line + "\n").getBytes());
					os.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			logger.info("Stopping " + this.getName() + "thread");
		}
	}

}
