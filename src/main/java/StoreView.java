public interface StoreView {

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
