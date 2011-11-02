package com.zeiban.winky;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;

public class GitHelper {
	private static Logger logger = Logger.getLogger(GitHelper.class.getName());
	private static Git init(String world) {
		Git git;
		File worldDir = new File(world);
		try{
			git = Git.open(worldDir);
			logger.log(Level.INFO, "Opening repository for world " + world);
		} catch (Exception e) {	
			logger.log(Level.INFO, "Initializing and opening repository for world " + world);
			git = Git.init().setDirectory(worldDir).call();
		}
		return git;
	}
	public synchronized static boolean commit(String world, String commiter) {
		try {
			Git git = init(world);
			
			git.add().addFilepattern(".").call();
			Status status = git.status().call();
			if(status.getAdded().size() > 0 || status.getChanged().size() > 0) {
				git.commit().setMessage(commiter).call();
				return true;
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to commit world " + world + " to repository", e);
		}
		return false;
	}
	
	public static List<String> log(String world, String playerName) {
		List<String> commitLogs = new ArrayList<String>();
		Git git = init(world);

		Iterable<RevCommit> commits;
		try {
			commits = git.log().call();
			Iterator<RevCommit> iter = commits.iterator();
			int i=0;
			while(iter.hasNext()) {
				RevCommit commit = iter.next();
				commitLogs.add(i + ": " + new SimpleDateFormat("MM-dd-yyyy-HH-mm-ss").format(commit.getCommitterIdent().getWhen()) + " " + commit.getFullMessage());
				i++;
			}
		} catch (Exception e) {
		}
		return commitLogs;
	}
	public static boolean reset(String world, int id, boolean validateOnly) {
		Git git = init(world);
		Iterable<RevCommit> commits;
		try {
			commits = git.log().call();
			Iterator<RevCommit> iter = commits.iterator();
			int i=0;
			while(iter.hasNext()) {
				RevCommit commit = iter.next();
				if(i == id) {
					if(!validateOnly){
						git.reset().setMode(ResetType.HARD).setRef(commit.getName()).call();
						logger.log(Level.INFO, "Reset of world " + world + " is complete");
					}
					return true;
				}
				i++;
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Unable to reset world " + world + " to commit ID " + id, e);
		}
		return false;
	}
}
