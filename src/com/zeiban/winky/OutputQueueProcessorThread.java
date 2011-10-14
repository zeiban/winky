package com.zeiban.winky;


import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.revwalk.RevCommit;

public class OutputQueueProcessorThread extends Thread {
	private Logger logger = Logger.getLogger(OutputQueueProcessorThread.class.getName());
	private BlockingQueue<String> queue;
	InputStreamThread stdin;
	/*
	private boolean commiting = false;
	private boolean started = false;
	private boolean restarting = false;
	private boolean resetOnShutdown = false;
	private int resetId;
	*/
	private Properties serverProperties = new Properties();
	private Wrapper server;
	public OutputQueueProcessorThread(Wrapper server, BlockingQueue<String> queue, InputStreamThread stdin) {
		this.server = server;
		this.queue = queue;
		this.stdin = stdin;
		this.setName("Queue Processor");
		this.start();
	}
	@Override
	public void run() {
		String commitPlayer = null;
		try {
			while(true) {
					String line = queue.take();
					System.out.println(line);
					if(server.isStarted()) {
						if(line.contains("issued server command:")) {
							String[] parts = line.split(" ");
							String playerName = parts[3];
							String command = parts[7];
							if(command.equalsIgnoreCase("git-commit")) {
								logger.info(playerName + " git-commit");
								server.setCommiting(true);
								commitPlayer = playerName;
								stdin.put("save-off");
								stdin.put("save-all");
							} else if(command.equalsIgnoreCase("git-reset")) {
								if(parts.length < 8) {
									stdin.put("tell " + playerName + " git-reset requires an ID");
									stdin.put("tell " + playerName + " git-reset <ID>");
								}else {
									try {
										int id = Integer.parseInt(parts[8]);
										/*
										if(processResetCommand(playerName, id)) {
											break;
										}*/
									} catch (NumberFormatException e) {
										stdin.put("tell " + playerName + " invalid commit ID");
									}
								}
							} else if(command.equalsIgnoreCase("git-log")) {
								//processLogCommand(playerName);
							} else {
								logger.info(playerName + " unknown command"); 
							}
						} else if(line.contains("Save complete")) {
							if(server.isCommiting()) {
								server.message(server.getCommitPlayer(), "Commiting world " + server.getCommitWorld() + " to repository");
								GitHelper.commit(server.getCommitWorld(), server.getCommitPlayer());
								server.message(server.getCommitPlayer(),"Commit complete");
								server.sendText("save-on");
							}
						} else if(line.contains("Enabling level saving..")) {
							if(server.isCommiting()) {
								server.setCommiting(false);
							}
						}
					} else if (line.contains("[INFO] Done")){
						server.setStarted(true);
					}
					if(line.contains("[INFO] Done")) {
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
	/*
	private void processCommit(String commitPlayer) {
		try {
			Git git = initGit();
			
			git.add().addFilepattern(".").call();
			Status status = git.status().call();
			if(status.getAdded().size() > 0 || status.getChanged().size() > 0) {
				git.commit().setMessage(commitPlayer).call();
			}
			stdin.put("say Commit complete.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		stdin.put("save-on");
	}*/
	/*
	private boolean processResetCommand(String playerName, int id) {
		server.setRestarting(true);
		Git git = initGit();

		Iterable<RevCommit> commits;
		try {
			commits = git.log().call();
			Iterator<RevCommit> iter = commits.iterator();
			int i=0;
			while(iter.hasNext()) {
				iter.next();
				if(i == id) {
					this.resetId = id;
					this.resetOnShutdown = true;
					stdin.put("stop");
					return true;
				}
				i++;
			}
			stdin.put("tell " + playerName + " commit ID " + id + " doesn't exist");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}*/
	/*
	private Git initGit() {
		Git git;
		File worldDir = new File(serverProperties.getProperty("level-name"));
		try{
			git = Git.open(worldDir);
		} catch (Exception e) {	
			git = Git.init().setDirectory(worldDir).call();
		}
		return git;
	}*/
	/*
	private void processLogCommand(String playerName) {

		Git git = initGit();

		Iterable<RevCommit> commits;
		try {
			commits = git.log().call();
			Iterator<RevCommit> iter = commits.iterator();
			int i=0;
			while(iter.hasNext()) {
				RevCommit commit = iter.next();
				stdin.put("tell " + playerName + " " + i + ": " + new SimpleDateFormat("MM-dd-yyyy-HH-mm-ss").format(commit.getCommitterIdent().getWhen()) + " " + commit.getFullMessage());
				i++;
			}
		} catch (Exception e) {
		}
	}*/

}
