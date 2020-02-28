package Exceptions;

public class UserIsNpcException extends RuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7549291293355696930L;

	/**
     * Constructs a {@code NullPointerException} with no detail message.
     */
    public UserIsNpcException() {
        super();
    }

    /**
     * Constructs a {@code NullPointerException} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public UserIsNpcException(String s) {
        super(s);
    }
}
