package Exceptions;

public class UserNotFoundException extends RuntimeException
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5730463219899337377L;

	/**
     * Constructs a {@code NullPointerException} with no detail message.
     */
    public UserNotFoundException() {
        super();
    }

    /**
     * Constructs a {@code NullPointerException} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public UserNotFoundException(String s) {
        super(s);
    }
}
