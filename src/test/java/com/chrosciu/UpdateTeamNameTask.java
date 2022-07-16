package com.chrosciu;

import com.chrosciu.domain.Team;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class UpdateTeamNameTask implements JpaTest.Task {
    private final long teamId;
    private final String newName;
    private final LockModeType lockModeType;
    private final long sleepBeforeLock;
    private final long sleepAfterLock;
    private final EntityManagerFactory entityManagerFactory;

    private CountDownLatch countDownLatch;

    @Override
    @SneakyThrows
    public void run() {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            log.info("[{}] started", newName);
            transaction.begin();
            TimeUnit.SECONDS.sleep(sleepBeforeLock);
            log.info("[{}] before load", newName);
            var team = entityManager.find(Team.class, teamId, lockModeType);
            log.info("[{}] after load", newName);
            TimeUnit.SECONDS.sleep(sleepAfterLock);
            team.setName(newName);
            log.info("[{}] before commit", newName);
            transaction.commit();
            log.info("[{}] after commit", newName);
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
            countDownLatch.countDown();
        }
    }

    @Override
    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }
}
