package org.openstack.atlas.rax.domain.repository.impl;

import org.openstack.atlas.rax.domain.entity.RaxHost;
import org.openstack.atlas.rax.domain.repository.RaxHostRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RaxHostRepositoryImpl implements RaxHostRepository {

    @PersistenceContext(unitName = "loadbalancing")
    private EntityManager entityManager;

    public List<RaxHost> getAllHosts() {
        String hqlStr = "from RaxHost h where h.hostStatus in ('ACTIVE_TARGET', 'FAILOVER') ";
        List<RaxHost> raxHosts = entityManager.createQuery(hqlStr).getResultList();
        return raxHosts;
    }

    public void save(RaxHost raxHost) {
        entityManager.persist(raxHost);
    }
}
