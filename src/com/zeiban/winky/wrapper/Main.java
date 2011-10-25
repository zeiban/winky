package com.zeiban.winky.wrapper;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;

import com.zeiban.winky.Wrapper;

public class Main implements WrapperListener {
	private Logger logger = Logger.getLogger(Main.class.getName());
	private Wrapper wrapper;
	private Main(){}
	
	public static void main(String[] args) {
		WrapperManager.start(new Main(), args);
	}

	@Override
	public void controlEvent(int event) {
		if ( ( event == WrapperManager.WRAPPER_CTRL_LOGOFF_EVENT )
				&& ( WrapperManager.isLaunchedAsService() || WrapperManager.isIgnoreUserLogoffs() ) )
		{
			//Ignore
		}
		else
		{
			WrapperManager.stop(0);
		}
	}

	@Override
	public Integer start(String[] args) {
		logger.log(Level.INFO,"Starting Wrapper Service");
		wrapper = new Wrapper(args);
		wrapper.start();
		return null;
	}

	@Override
	public int stop(int exitCode) {
		logger.log(Level.INFO,"Starting Wrapper Service");
		wrapper.stop();
		return 0;
	}

}
