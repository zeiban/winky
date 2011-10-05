package com.zeiban.winky;


import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Logger;

public class OutputStreamThread extends Thread {
	private Logger logger = Logger.getLogger(Main.LOG_FILENAME);
	private boolean running = true;
	private Scanner scanner;
	private Queue<String> queue;
	public OutputStreamThread(String name, InputStream is, Queue<String> queue) {
		this.setName(name);
		scanner = new Scanner(is);
		this.queue = queue;
		this.start();
	}
	@Override
	public void run() {
		String line;
		try{
			while(true) {
					line = scanner.nextLine();
					this.queue.add(line);
			}
		} catch(Exception e) {
			logger.info("Stopping " + this.getName() + "thread");
		}
		scanner.close();		
	}
}
