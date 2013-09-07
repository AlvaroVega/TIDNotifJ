import Overload.*;
import es.tid.TIDorbj.core.poa.POAManagerImpl;

public class POAManagerStatsExtractor implements HealingSensorOperations {

    POAManagerHolder m_poa_manager;

    public POAManagerStatsExtractor(POAManagerHolder poa_manager)
    {
        m_poa_manager = poa_manager;
    }

    public void resetStatsCounter() {
        m_poa_manager.obtainPOAManager().resetRequestStats();
    }

    public Stats getStats() {
        Stats result = new Stats();
        POAManagerImpl man = m_poa_manager.obtainPOAManager();

        result.error_rate = 1.0 / man.getRequestDispatchingRate();
        result.requests_accepted = (int) man.getDispatchedRequests();
        result.requests_discarded = (int) man.getRejectedRequests();
        result.requests_received =
                result.requests_accepted + result.requests_discarded;

        return result;
    }

    public Stats getLastSecondsStats(int seconds) {
        POAManagerImpl man = m_poa_manager.obtainPOAManager();
        
        long succ_from = man.getDispatchedRequests();
        long fail_from = man.getRejectedRequests();

        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ex) {
            System.err.println("Warning: Healed push consumer couldn't sleep!");
        }

        Stats result = getStats();
        result.requests_discarded -= fail_from;
        result.requests_accepted  -= succ_from;
        result.requests_received =
                result.requests_discarded + result.requests_accepted;
        result.error_rate =
                (float) result.requests_discarded /
                (float) result.requests_received;

        return result;
    }

}
