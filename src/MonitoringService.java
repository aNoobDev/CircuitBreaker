public class MonitoringService {

    private final ICircuitBreaker delayedServiceCircuitBreaker;

    private final ICircuitBreaker quickServiceCircuitBreaker;

    public MonitoringService(CircuitBreaker delayedServiceCB, CircuitBreaker quickServiceCB) {
        this.delayedServiceCircuitBreaker = delayedServiceCB;
        this.quickServiceCircuitBreaker = quickServiceCB;
    }


    /**
     * Fetch response from the delayed service (with some simulated startup time).
     *
     * @return response string
     */
    public String delayedServiceResponse() {
        try {
            return this.delayedServiceCircuitBreaker.attemptRequest();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * Fetches response from a healthy service without any failure.
     *
     * @return response string
     */
    public String quickServiceResponse() {
        try {
            return this.quickServiceCircuitBreaker.attemptRequest();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
