public interface StoreView {

    /**
     * Handles the login attempt for a user.
     * @param b true if login was successful, false otherwise.
     */
    void handleLogin(boolean b);

    /**
     * Handles a message.
     * @param message Message to handle.
     */
    void handleMessage(String message);

    /**
     * Gets a user's input.
     * @param message Message for input request.
     * @return A user's input.
     */
    String getUserInput(String message);
}
