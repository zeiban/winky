package com.zeiban.winky;

public class BackupThread extends Thread {
	private boolean running = true;
	private Wrapper server;
	public BackupThread(Wrapper server) {
		this.server = server;
	}
	@Override
	public void run() {
		while(running) {
			try {
				Thread.sleep(3*1000);
				server.commitCommand("git-commit world",null);
			} catch (InterruptedException e) {
				running = false;
			}
		}
	}

}
