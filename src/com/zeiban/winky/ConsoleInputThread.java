
package com.zeiban.winky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Logger;

public class ConsoleInputThread extends Thread {
	private Logger logger = Logger.getLogger(ConsoleInputThread.class.getName());
	private InputStreamThread stdin;
	private Scanner scanner;
	private BufferedReader input;
	private ServerProcess server;
	private boolean running = true;
	public ConsoleInputThread(ServerProcess server, InputStreamThread stdin) {
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
					if(line.startsWith("git-commit")) {
						if(server.isCommiting()) {
							server.say("Commit is already in process");
						} else {
							String[] parts = line.split(" ");
							if(parts.length >= 2) {
								server.setCommiting(true);
								server.setCommitPlayer("CONSOLE");
								server.setCommitWorld(parts[1]);
								server.sendText("save-off");
								server.sendText("save-all");
							} else {
								
							}
						}
					} else if(line.equalsIgnoreCase("restart")){
						server.setRestarting(true);
						server.sendText("stop");
					} else if(line.equalsIgnoreCase("git-log")){
						
					} else if(line.equalsIgnoreCase("git-reset")){
						
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
