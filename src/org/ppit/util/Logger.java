package org.ppit.util;

public class Logger {
	private static Logger m_instance; 
	
	static {
		m_instance = new Logger();
	}

	public static Logger getInstance() {
		return m_instance;
	}
	
	public void log(int level, String message) {
		System.out.println(get_level(level) + ": " + message);
	}
	
	private String get_level(int level) {
		switch (level)
		{
		case INFO:
			return "INFO";
		case WARNING:
			return "WARNING";
		case ERROR:
			return "ERROR";
		default:
			return "UNDEFINED";
		}
	}
	
	public static final int INFO = 1;
	public static final int WARNING = 2;
	public static final int ERROR = 3;
}
