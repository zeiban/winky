
package com.zeiban.winky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Logger;

public class ConsoleInputThread extends Thread {
	private Logger logger = Logger.getLogger(Main.LOG_FILENAME);
	private InputStreamThread stdin;
	private Scanner scanner;
	private BufferedReader input;
	public ConsoleInputThread(InputStreamThread stdin) {
		this.stdin = stdin;
		this.setName("Console Input Thread");
		this.start();
	}
	@Override
	public void run() {
		input = new BufferedReader(new InputStreamReader(System.in));
		scanner = new Scanner(input);
			while(true) {
				try {
					if(input.ready()) {
						String line = scanner.nextLine();
						stdin.put(line);
					}
				} catch (IOException e) {
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					break;
				}
			}
		scanner.close();

		logger.info("Stopping " + this.getName() + " thread");
	}

}
