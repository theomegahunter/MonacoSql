package org.malibu.monacosql.exception;

public class MonacoSqlException extends Exception {
	private static final long serialVersionUID = 5218363539142907750L;

	public MonacoSqlException(String message, Throwable t) {
		super(message, t);
	}
	
	public MonacoSqlException(Throwable t) {
		super(t);
	}
	
	public MonacoSqlException(String message) {
		super(message);
	}
	
	public MonacoSqlException() {
		super();
	}
}
