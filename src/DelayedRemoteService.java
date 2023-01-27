import java.lang.Thread;
public class DelayedRemoteService implements RemoteService{

    public DelayedRemoteService() {
    }

    @Override
    public String call(){
        try{

            Thread.sleep(10000);
        }
        catch (InterruptedException ie){
            return ie.getMessage();
        }
        return "Delayed Response";
    }
}
