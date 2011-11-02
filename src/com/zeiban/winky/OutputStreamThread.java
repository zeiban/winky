package com.zeiban.winky;


import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Logger;

public class OutputStreamThread extends Thread {
	private Logger logger = Logger.getLogger(OutputStreamThread.class.getName());
	private boolean running = true;
	private Scanner scanner;
	private Queue<String> queue;
	private Wrapper server;
	public OutputStreamThread(Wrapper server, String name, InputStream is, Queue<String> queue) {
		this.server = server;
		this.setName(name);
		scanner = new Scanner(is);
		this.queue = queue;
		this.start();
	}
	@Override
	public void run() {
		String line;
			while(!isInterrupted()) {
					try {
						line = scanner.nextLine();
						this.queue.add(line);
					} catch (NoSuchElementException e) {
					}
			}
		scanner.close();		
	}
}
