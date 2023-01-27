public interface ICircuitBreaker {

    void recordSuccess();
    void recordFailure(String response);
    String getState();
    String attemptRequest();
}
