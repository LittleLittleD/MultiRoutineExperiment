import javax.swing.JFrame;

public abstract class FrameFunction extends JFrame {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private User user;

	FrameFunction(User user) {
		this.user = user;
	}

	public abstract void initial();
}
