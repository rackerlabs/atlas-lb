package org.openstack.atlas.rax.domain.service;

import org.openstack.atlas.service.domain.common.ErrorMessages;
import org.openstack.atlas.service.domain.entity.LoadBalancer;
import org.openstack.atlas.service.domain.entity.LoadBalancerProtocol;
import org.openstack.atlas.service.domain.entity.LoadBalancerStatus;
import org.openstack.atlas.service.domain.entity.SessionPersistence;
import org.openstack.atlas.service.domain.exception.*;
import org.openstack.atlas.service.domain.service.impl.LoadBalancerServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class RaxLoadBalancerServiceImpl extends LoadBalancerServiceImpl {

    @Override
    @Transactional
    public LoadBalancer update(final LoadBalancer loadBalancer) throws PersistenceServiceException {
        LoadBalancer dbLoadBalancer = loadBalancerRepository.getByIdAndAccountId(loadBalancer.getId(), loadBalancer.getAccountId());

        loadBalancerRepository.changeStatus(dbLoadBalancer.getAccountId(), dbLoadBalancer.getId(), LoadBalancerStatus.PENDING_UPDATE);

        setName(loadBalancer, dbLoadBalancer);
        setAlgorithm(loadBalancer, dbLoadBalancer);
        setPort(loadBalancer, dbLoadBalancer);
        setProtocol(loadBalancer, dbLoadBalancer);
        setConnectionLogging(loadBalancer, dbLoadBalancer);

        dbLoadBalancer = loadBalancerRepository.update(dbLoadBalancer);
        dbLoadBalancer.setUserName(loadBalancer.getUserName());

        return dbLoadBalancer;
    }

    protected void setProtocol(final LoadBalancer loadBalancer, final LoadBalancer dbLoadBalancer) throws BadRequestException {
        boolean portHMTypecheck = true;
        if (loadBalancer.getProtocol() != null && !loadBalancer.getProtocol().equals(dbLoadBalancer.getProtocol())) {

            //check for health monitor type and allow update only if protocol matches health monitory type for HTTP and HTTPS
            if (dbLoadBalancer.getHealthMonitor() != null) {
                if (dbLoadBalancer.getHealthMonitor().getType() != null) {
                    if (dbLoadBalancer.getHealthMonitor().getType().name().equals(LoadBalancerProtocol.HTTP.name())) {
                        //incoming port not HTTP
                        if (!(loadBalancer.getProtocol().name().equals(LoadBalancerProtocol.HTTP.name()))) {
                            portHMTypecheck = false;
                        }
                    } else if (dbLoadBalancer.getHealthMonitor().getType().name().equals(LoadBalancerProtocol.HTTPS.name())) {
                        //incoming port not HTTP
                        if (!(loadBalancer.getProtocol().name().equals(LoadBalancerProtocol.HTTPS.name()))) {
                            portHMTypecheck = false;
                        }
                    }
                }
            }

            if (portHMTypecheck) {
                /* Notify the Usage Processor on changes of protocol to and from secure protocols */
                //notifyUsageProcessorOfSslChanges(message, queueLb, dbLoadBalancer);

                if (loadBalancer.getProtocol().equals(LoadBalancerProtocol.HTTP)) {
                    LOG.debug("Updating loadbalancer protocol to " + loadBalancer.getProtocol());
                    dbLoadBalancer.setProtocol(loadBalancer.getProtocol());
                } else {
                    dbLoadBalancer.setSessionPersistence(SessionPersistence.NONE);
                    dbLoadBalancer.setProtocol(loadBalancer.getProtocol());
                }
            } else {
                LOG.error("Cannot update port as the loadbalancer has a incompatible Health Monitor type");
                throw new BadRequestException(ErrorMessages.PORT_HEALTH_MONITOR_INCOMPATIBLE);
            }
        }
    }

    protected void setPort(final LoadBalancer loadBalancer, final LoadBalancer dbLoadBalancer) throws BadRequestException {
        if (loadBalancer.getPort() != null && !loadBalancer.getPort().equals(dbLoadBalancer.getPort())) {
            LOG.debug("Updating loadbalancer port to " + loadBalancer.getPort());
            if (loadBalancerRepository.canUpdateToNewPort(loadBalancer.getPort(), dbLoadBalancer.getLoadBalancerJoinVipSet())) {
                loadBalancerRepository.updatePortInJoinTable(loadBalancer);
                dbLoadBalancer.setPort(loadBalancer.getPort());
            } else {
                LOG.error("Cannot update load balancer port as it is currently in use by another virtual ip.");
                throw new BadRequestException(ErrorMessages.PORT_IN_USE);
            }
        }
    }

    protected void setConnectionLogging(final LoadBalancer loadBalancer, final LoadBalancer dbLoadBalancer) throws UnprocessableEntityException {
        if (loadBalancer.getConnectionLogging() != null && !loadBalancer.getConnectionLogging().equals(dbLoadBalancer.getConnectionLogging())) {
            /*if (loadBalancer.getConnectionLogging()) {
                if (loadBalancer.getProtocol() != LoadBalancerProtocol.HTTP) {
                    LOG.error("Protocol must be HTTP for connection logging.");
                    throw new UnprocessableEntityException(String.format("Protocol must be HTTP for connection logging."));
                }
                LOG.debug("Enabling connection logging on the loadbalancer...");
            } else {
                LOG.debug("Disabling connection logging on the loadbalancer...");
            }*/
            dbLoadBalancer.setConnectionLogging(loadBalancer.getConnectionLogging());
        }
    }
}
