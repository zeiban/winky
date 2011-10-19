package com.zeiban.winky;

public class Main {
	public static void main(String[] args) {
		new Thread(new Wrapper()).start();
	}
}
