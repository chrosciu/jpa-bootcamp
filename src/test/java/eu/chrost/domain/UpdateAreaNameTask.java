package eu.chrost.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
class UpdateAreaNameTask implements Callable<Void> {
    private final long areaId;
    private final String newName;
    private final LockModeType lockModeType;
    private final long sleepBeforeLock;
    private final long sleepAfterLock;
    private final EntityManagerFactory entityManagerFactory;

    @Override
    public Void call() throws Exception {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            log.info("[{}] started", newName);
            transaction.begin();
            TimeUnit.SECONDS.sleep(sleepBeforeLock);
            log.info("[{}] before load", newName);
            var area = entityManager.find(Area.class, areaId, lockModeType);
            log.info("[{}] after load", newName);
            TimeUnit.SECONDS.sleep(sleepAfterLock);
            area.setName(newName);
            log.info("[{}] before commit", newName);
            transaction.commit();
            log.info("[{}] after commit", newName);
            return null;
        } catch (Throwable t) {
            log.warn("[{}] exception: ", newName, t);
            throw t;
        } finally {
            if (transaction != null && transaction.isActive()) {
                log.info("[{}] before rollback", newName);
                transaction.rollback();
                log.info("[{}] after rollback", newName);
            }
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }
}
