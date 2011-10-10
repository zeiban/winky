	package com.zeiban.winky;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.revwalk.RevCommit;

public class ServerProcess implements Runnable {
	private Logger logger = Logger.getLogger(ServerProcess.class.getName());
	private static final String PROP_FILENAME = "winky.properties";
	private static final String LOG_FILENAME = "winky.log";
	private static final String PROP_JVM_PATH = "jvm.path";
	private static final String APP_NAME = "Winky";
	private Process process;
	private OutputStreamThread stdout;
	private OutputStreamThread stderr;
	private InputStreamThread stdin;
	private ConsoleInputThread console;
	private OutputQueueProcessorThread queueProcessor;
	private boolean commiting = false;
	private String commitPlayer;
	private String commitWorld;
	private boolean resetting = false;
	private String resetWorld;
	private int resetId;
	private boolean restarting = false;
	private boolean started = false;

	public String getCommitWorld() {
		return commitWorld;
	}
	public void setCommitWorld(String commitWorld) {
		this.commitWorld = commitWorld;
	}
	public String getCommitPlayer() {
		return commitPlayer;
	}
	public void setCommitPlayer(String commitPlayer) {
		this.commitPlayer = commitPlayer;
	}

	public boolean isStarted() {
		return started;
	}
	public void setStarted(boolean started) {
		this.started = started;
	}

	private BlockingQueue<String> outputQueue = new LinkedBlockingQueue<String>();
	private Properties properties = new Properties();
	public int getResetId() {
		return resetId;
	}
	public void setResetId(int resetId) {
		this.resetId = resetId;
	}

	private List<Thread> threads = new ArrayList<Thread>();

	public boolean isRestarting() {
		return restarting;
	}
	public void setRestarting(boolean restarting) {
		this.restarting = restarting;
	}
	public Properties getProperties() {
		return properties;
	}
	public boolean isCommiting() {
		return commiting;
	}

	public void setCommiting(boolean commiting) {
		this.commiting = commiting;
	}

	public void sendText(String text) {
		stdin.put(text);
	}
	public void say(String text) {
		stdin.put("say " + text);
	}

	@Override
	public void run() {

		Handler fileHandler;
		try {
			fileHandler = new FileHandler(ServerProcess.LOG_FILENAME);
			fileHandler.setFormatter(new SimpleFormatter());
			LogManager.getLogManager().reset();
			Logger.getLogger("").addHandler(fileHandler);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Unable to open log file " + ServerProcess.LOG_FILENAME + " continuing anyway", e);
		}
		logger.info(ServerProcess.APP_NAME + " Version " + Version.Major + "." + Version.Minor + "." +Version.Patch);
		
		while(true) {
			logger.log(Level.INFO, "Loading properties");
			try {
				properties.load(new FileInputStream(ServerProcess.PROP_FILENAME));
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Unable to load properties file", e);
				return;
			}
			
			logger.log(Level.INFO, "Starting Minecraft server process");
			try {
				process = Runtime.getRuntime().exec(properties.getProperty(ServerProcess.PROP_JVM_PATH,"java") + " -Xmx1024M -Xms1024M -jar minecraft_server.jar nogui");
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Unable to start process", e);
				return;
			}
			
			
			stdin = new InputStreamThread(this, "stdin",process.getOutputStream());
			threads.add(stdin);

			queueProcessor = new OutputQueueProcessorThread(this, outputQueue,stdin);
			threads.add(queueProcessor);
			
			stdout = new OutputStreamThread(this, "stdout",process.getInputStream(), outputQueue);
			threads.add(stdout);
			
			stderr = new OutputStreamThread(this, "stderr",process.getErrorStream(), outputQueue);
			threads.add(stderr);
			
			console = new ConsoleInputThread(this, stdin);
			threads.add(console);
			
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				// Dont't care
			}
			logger.info("Minecraft server process has shutdown");

			for(Thread thread : threads) {
				thread.interrupt();
				try {
					thread.join();
				} catch (InterruptedException e) {
					logger.log(Level.SEVERE, "Shouldn't be here" ,e);
				}
			}
			threads.clear();
			
			if(!restarting) {
				logger.log(Level.INFO,"Wrapper shutdown");
				break;
			}
			if(resetId != 0) {
				//logger.info("Resetting to commit ID " + this.resetId);
//				Git git = GitHelper.();
				/*
				Iterable<RevCommit> commits;
				try {
					commits = git.log().call();
					Iterator<RevCommit> iter = commits.iterator();
					int i=0;
					while(iter.hasNext()) {
						RevCommit commit = iter.next();
						if(i == this.resetId) {
							git.reset().setMode(ResetType.HARD).setRef(commit.getName()).call();
							//logger.info("Reset complete");
							break;
						}
						i++;
					}
				} catch (Exception e1) {
					logger.severe("Unable to commit world to repository");
				}
			*/
			}
			logger.log(Level.INFO,"Wrapper restarting");
			restarting = false;
		}
	}
	public void setResetting(boolean resetting) {
		this.resetting = resetting;
	}
	public boolean isResetting() {
		return resetting;
	}
	public void setResetWorld(String resetWorld) {
		this.resetWorld = resetWorld;
	}
	public String getResetWorld() {
		return resetWorld;
	}
}
