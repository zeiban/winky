
package com.zeiban.winky;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleInputThread extends Thread {
	private Logger logger = Logger.getLogger(ConsoleInputThread.class.getName());
	private InputStreamThread stdin;
	private Scanner scanner;
	private BufferedReader input;
	private Wrapper server;
	private boolean running = true;
	public ConsoleInputThread(Wrapper server, InputStreamThread stdin) {
		this.server = server;
		this.stdin = stdin;
		this.setName("Console Input Thread");
		this.start();
	}
	@Override
	public void run() {
		input = new BufferedReader(new InputStreamReader(System.in));
		scanner = new Scanner(input);
		while(running) {
			try {
				if(input.ready()) {
					String line = scanner.nextLine();
					if(line.toLowerCase().startsWith("git-commit")) {
						server.commitCommand(line, null);
					} else if(line.equalsIgnoreCase("restart")){
						server.setRestarting(true);
						server.sendText("stop");
					} else if(line.toLowerCase().startsWith("git-log")){
						server.logCommand(line, null);
					} else if(line.toLowerCase().startsWith("git-reset")){
						server.resetCommand(line, null);
					} else {
						stdin.put(line);
					}
				}
			} catch (IOException e) {
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
					running = false;
			}
		}
	}
	@Override
	public void interrupt() {
		running = false;
		super.interrupt();
	}

}
