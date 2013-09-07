import Overload.*;
import es.tid.TIDorbj.core.poa.POAManagerImpl;
import org.omg.NotificationService.NotificationAdmin;

public class NotificationAdminHealerImpl 
        extends NotificationAdminHealerPOA
        implements POAManagerHolder {

    private POAManagerImpl              m_poa_manager;
    private POAManagerStatsExtractor    m_stats_extractor;
    private NotificationAdmin           m_notif_admin;

    public NotificationAdminHealerImpl(
            POAManagerImpl poa_manager,
            NotificationAdmin notif_admin)
    {
        m_poa_manager = poa_manager;
        m_stats_extractor = new POAManagerStatsExtractor(this);
        m_notif_admin = notif_admin;
    }

    public void resetStatsCounter() {
        m_stats_extractor.resetStatsCounter();
    }

    public Stats getStats() {
        return m_stats_extractor.getStats();
    }

    public Stats getLastSecondsStats(int seconds) {
        return m_stats_extractor.getLastSecondsStats(seconds);
    }

    public POAManagerImpl obtainPOAManager() {
        return m_poa_manager;
    }

}
