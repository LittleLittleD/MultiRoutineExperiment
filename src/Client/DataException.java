package Client;
public class DataException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataException() {
		super();
	}

	public DataException(String message) {
		super(message);
	}

	public DataException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataException(Throwable cause) {
		super(cause);
	}
}
