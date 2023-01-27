public class CircuitBreaker implements ICircuitBreaker{

    private final long timeout;
    private final long retryTimePeriod;
    private final RemoteService service;
    long lastFailureTime;
    private String lastFailureResponse;
    int failureCount;
    private final int failureThreshold;
    private State state;
    private final long futureTime = (long)1e12;

    CircuitBreaker(RemoteService serviceToCall, long timeout, int failureThreshold,
                   long retryTimePeriod) {

        this.service = serviceToCall;
        // We start in a closed state hoping that everything is fine
        this.state = State.CLOSED;
        this.failureThreshold = failureThreshold;
        // Timeout for the API request.
        this.timeout = timeout;
        this.retryTimePeriod = retryTimePeriod;
        //An absurd amount of time in future which basically indicates the last failure never happened
        this.lastFailureTime = System.currentTimeMillis() + futureTime;
        this.failureCount = 0;
    }

    // Reset everything to defaults
    @Override
    public void recordSuccess() {
        this.failureCount = 0;
        this.lastFailureTime = System.currentTimeMillis() + futureTime;
        this.state = State.CLOSED;
    }

    @Override
    public void recordFailure(String response) {
        failureCount = failureCount + 1;
        this.lastFailureTime = System.currentTimeMillis();
        this.lastFailureResponse = response;
    }

    // Evaluate the current state based on failureThreshold, failureCount and lastFailureTime.
    protected void evaluateState() {
        if (failureCount >= failureThreshold) { //Then something is wrong with remote service
            if ((System.currentTimeMillis() - lastFailureTime) > retryTimePeriod) {
                //We have waited long enough and should try checking if service is up
                state = State.PARTIALLY_OPEN;
            } else {
                //Service would still probably be down
                state = State.OPEN;
            }
        } else {
            //Everything is working fine
            state = State.CLOSED;
        }
    }

    @Override
    public String getState() {
        evaluateState();
        return state.name();
    }

    /**
     * Executes service call.
     *
     * @return Value from the remote resource, stale response or a custom exception
     */
    @Override
    public String attemptRequest(){
        evaluateState();
        if (state == State.OPEN) {
            return this.lastFailureResponse;
        } else {
            // Make the API request if the circuit is not OPEN

            long startTime = System.currentTimeMillis();
            String response = service.call();
            long endTime = System.currentTimeMillis();

            long serviceExecutionDuration = endTime - startTime;

            //System.out.println(serviceExecutionDuration+" "+timeout);

            if(serviceExecutionDuration > timeout){
                recordFailure("Service "+service.getClass().getName()+" not working fine");
            }
            else{ // API responded fine. Let's reset everything.

                recordSuccess();
            }
            return response;
        }
    }
}
