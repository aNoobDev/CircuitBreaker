
public class CircuitBreakerApp {


    public static void main(String[] args) {


        RemoteService delayedService = new DelayedRemoteService();
        CircuitBreaker delayedServiceCircuitBreaker = new CircuitBreaker(delayedService, 1000*3, 2,
                (long)1000*5);

        RemoteService quickService = new QuickRemoteService();
        CircuitBreaker quickServiceCircuitBreaker = new CircuitBreaker(quickService, 1000*3, 2,
                (long)1000*5);

        //Create an object of monitoring service which makes both local and remote calls
        MonitoringService monitoringService = new MonitoringService(delayedServiceCircuitBreaker,
                quickServiceCircuitBreaker);

        //Fetch current state of delayed service circuit breaker before crossing failure threshold limit
        System.out.println("Circuit Breaker for Delayed Service is now "+delayedServiceCircuitBreaker.getState());

        //Fetch response from delayed service 2 times, to meet the failure threshold
        System.out.println(monitoringService.delayedServiceResponse());
        System.out.println(monitoringService.delayedServiceResponse());

        //Fetch current state of delayed service circuit breaker after crossing failure threshold limit

        System.out.println("Circuit Breaker for Delayed Service is now "+delayedServiceCircuitBreaker.getState());

        //Meanwhile, the delayed service is down, fetch response from the healthy quick service
        System.out.println(monitoringService.quickServiceResponse());
        System.out.println("Circuit Breaker for Quick Service is now "+quickServiceCircuitBreaker.getState());

        //Wait for the delayed service to become responsive
        try {
            System.out.println("Waiting for delayed service to become responsive");
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Check the state of delayed circuit breaker, should be PARTIALLY_OPEN
        System.out.println("Circuit Breaker for Delayed Service is now "+delayedServiceCircuitBreaker.getState());

        //Fetch response from delayed service, which should be healthy by now
        //Since we have by default added timeout in delayed service it will give delayed response and circuit breaker will remain open although in real world scenario we will get response time from API call
        System.out.println(monitoringService.delayedServiceResponse());
        //As successful response is fetched, it should be CLOSED again.
        System.out.println("Circuit Breaker for Delayed Service is now "+delayedServiceCircuitBreaker.getState());
    }
}
