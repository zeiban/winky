package com.zeiban.winky;




public class Main {
	/*
	public static final String LOG_FILENAME = "winky.log";
	public static final String PROPERTIES_FILENAME = "winky.properties";
	
	private Logger logger = Logger.getLogger(Main.LOG_FILENAME);
	private Process process;
	private OutputStreamThread stdout;
	private OutputStreamThread stderr;
	private InputStreamThread stdin;
	private ConsoleInputThread console;
	private OutputQueueProcessorThread queueProcessor;
	private BlockingQueue<String> outputQueue = new LinkedBlockingQueue<String>();
	private Properties properties = new Properties();
	private Properties serverProperties = new Properties();
	private List<Thread> threads = new ArrayList<Thread>();
	*/
	/*
	public class CommitThread extends Thread {
		public boolean running = true;
		private InputStreamThread stdin;
		public CommitThread(InputStreamThread stdin) {
			this.stdin = stdin;
		}
		@Override
		public void run() {
			setName("Commit Thread");
			int backupInterval = Integer.parseInt(inkyProps.getProperty("backup.interval", "30"));
			logger.info("Commiting world to git repository every " + backupInterval + " minutes");
			while(running) {
				try {
					Thread.sleep(1000*60*backupInterval);
					logger.info("Commiting world to git repository");
					try {
						stdin.put("say Commiting world to repository...");
						stdin.put("save-off");
						stdin.put("save-all");

						Git git;
						File worldDir = new File(serverProps.getProperty("level-name"));
						try{
							git = Git.open(worldDir);
						} catch (Exception e) {	
							git = Git.init().setDirectory(worldDir).call();
						}
						git.add().addFilepattern(".").call();
						Status status = git.status().call();
						if(status.getAdded().size() > 0 || status.getChanged().size() > 0) {
							git.commit().setMessage(new SimpleDateFormat("MM-dd-yyyy-HH-mm-ss").format(new Date())).call();
						}
						stdin.put("say Commit complete.");
						stdin.put("save-on");
					} catch (Exception e) {
						saving = false;
						e.printStackTrace();
					}
				} catch (InterruptedException e) {
				}				
			}
		}

	}
	*/
	public static void main(String[] args) {
		new Thread(new Wrapper()).start();
	}
	/*
	public void run() {
		try {
			FileHandler handler = new FileHandler(Main.LOG_FILENAME);
			handler.setFormatter(new SimpleFormatter());
			Logger.getLogger("").setUseParentHandlers(false);
			Logger.getLogger("").addHandler(handler);
		} catch (Exception e) {
		}
		
		try {
			while(true) {
				properties.load(new FileInputStream(Main.PROPERTIES_FILENAME));
				process = Runtime.getRuntime().exec(properties.getProperty("jvm.path") + " -Xmx1024M -Xms1024M -jar minecraft_server.jar nogui");
				
				
				stdout = new OutputStreamThread("stdout",process.getInputStream(), outputQueue);
				threads.add(stdout);
				
				stderr = new OutputStreamThread("stderr",process.getErrorStream(), outputQueue);
				threads.add(stderr);
				
				stdin = new InputStreamThread("stdin",process.getOutputStream());
				threads.add(stdin);
				
				queueProcessor = new OutputQueueProcessorThread(outputQueue,stdin);
				threads.add(queueProcessor);

				console = new ConsoleInputThread(stdin);
				threads.add(console);
				
				process.waitFor();
								
				logger.finest("Stopping threads");
				for(Thread thread : threads) {
					logger.info("Trying to stop " + thread.getName());
					thread.interrupt();
					thread.join();
				}
				if(!queueProcessor.isRestarting()) {
					logger.info("Stopping server");
					break;
				} else {
					logger.info("Starting server");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
}
