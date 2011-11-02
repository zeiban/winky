package com.zeiban.winky;


import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class OutputQueueProcessorThread extends Thread {
	private Logger logger = Logger.getLogger(OutputQueueProcessorThread.class.getName());
	private BlockingQueue<String> queue;

	private Properties serverProperties = new Properties();
	private Wrapper server;
	public OutputQueueProcessorThread(Wrapper server, BlockingQueue<String> queue, InputStreamThread stdin) {
		this.server = server;
		this.queue = queue;
		this.setName("Queue Processor");
		this.start();
	}
	@Override
	public void run() {
		try {
			while(true) {
					String line = queue.take();
					System.out.println(line);
					if(server.isStarted()) {
						if(line.contains("issued server command:")) {
							String[] parts = line.split(" ");
							String playerName = parts[3];
							String command = line.substring(line.indexOf(": ")+2).toLowerCase();
							if(command.startsWith("git-commit")) {
								server.commitCommand(command, playerName);
							} else if(command.startsWith("git-reset")) {
								server.resetCommand(command, playerName);
							} else if(command.startsWith("git-log")) {
								server.logCommand(command, playerName);
							} else if(command.startsWith("restart")) {
								server.restartCommand(playerName);
							} else {
								logger.info(playerName + " unknown command"); 
							}
						} else if(line.contains("Save complete")) {
							if(server.isCommiting()) {
								server.message(server.getCommitPlayer(), "Commiting world \"" + server.getCommitWorld() + "\" to repository");
								if(!GitHelper.commit(server.getCommitWorld(), server.getCommitPlayer())) {
									server.message(server.getCommitPlayer(),"Commit failed, check log");
								} else {
									server.message(server.getCommitPlayer(),"Commit complete");
									server.sendText("save-on");
								}
							}
						} else if(line.contains("Enabling level saving..")) {
							if(server.isCommiting()) {
								server.setCommiting(false);
							}
						}
					} else if (line.contains("[INFO] Done")){
						try {
							serverProperties.load(new FileInputStream("server.properties"));
							server.setStarted(true);
						} catch (Exception e) {
							logger.severe("Unable to read server.properties"); 
						}
					}
			}
		} catch (InterruptedException e) {
		}
	}
}
