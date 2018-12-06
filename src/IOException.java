public class IOException extends Exception {

	private static final long serialVersionUID = 1L;

	public IOException() {
		super();
	}

	public IOException(String message) {
		super(message);
	}

	public IOException(String message, Throwable cause) {
		super(message, cause);
	}

	public IOException(Throwable cause) {
		super(cause);
	}

}
