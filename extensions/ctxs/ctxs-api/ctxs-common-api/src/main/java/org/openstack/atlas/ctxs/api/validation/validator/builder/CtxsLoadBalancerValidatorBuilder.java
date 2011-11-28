package org.openstack.atlas.ctxs.api.validation.validator.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openstack.atlas.api.validation.validator.builder.*;
import org.openstack.atlas.api.validation.verifier.MustBeEmptyOrNull;
import org.openstack.atlas.api.validation.verifier.Verifier;
import org.openstack.atlas.api.validation.verifier.VerifierResult;
import org.openstack.atlas.datamodel.AlgorithmType;
import org.openstack.atlas.datamodel.ProtocolType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import java.util.Map;


import org.openstack.atlas.api.v1.extensions.ctxs.LoadBalancer;
import org.openstack.atlas.ctxs.api.validation.validator.CertificateRefValidator;

import static org.openstack.atlas.api.validation.context.HttpRequestType.POST;

@Primary
@Component
@Scope("request")
public class CtxsLoadBalancerValidatorBuilder extends LoadBalancerValidatorBuilder {
    protected final Log LOG = LogFactory.getLog(CtxsLoadBalancerValidatorBuilder.class);
    private final int MIN_CERTIFICATES = 1;
    private final int MAX_CERTIFICATES = 10;
    
    @Autowired
    public CtxsLoadBalancerValidatorBuilder(AlgorithmType algorithmType,
                                           ProtocolType protocolType,
                                           NodeValidatorBuilder nodeValidatorBuilder,
                                           VirtualIpValidatorBuilder virtualIpValidatorBuilder,
                                           HealthMonitorValidatorBuilder healthMonitorValidatorBuilder,
                                           ConnectionThrottleValidatorBuilder connectionThrottleValidatorBuilder,
                                           SessionPersistenceValidatorBuilder sessionPersistenceValidatorBuilder,
                                           CertificateRefValidatorBuilder certRefValidatorBuilder) {
                                           
        super(algorithmType, protocolType, nodeValidatorBuilder, virtualIpValidatorBuilder, healthMonitorValidatorBuilder, connectionThrottleValidatorBuilder, sessionPersistenceValidatorBuilder);

        // POST EXPECTATIONS
        
        Object validationTarget = validationTarget();
        LOG.debug("inside constructor for CtxsLoadBalancerValidatorBuilder");
        LOG.debug(String.format("validation target object class is: %s", validationTarget.getClass().getName()));
        LOG.debug(String.format("validation target object name is: %s", ((org.openstack.atlas.core.api.v1.LoadBalancer) validationTarget).getName()));
                
        if (validationTarget instanceof LoadBalancer) {
            LoadBalancer ctxsLoadBalancer = (LoadBalancer) validationTarget;
        
            result(ctxsLoadBalancer.getCertificates()).must().exist().forContext(POST).withMessage("Must provide at least 1 certificate for the load balancer.");
            result(ctxsLoadBalancer.getCertificates()).must().haveSizeOfAtLeast(MIN_CERTIFICATES).forContext(POST).withMessage(String.format("Must have at least %d certificate(s).", MIN_CERTIFICATES));
            result(ctxsLoadBalancer.getCertificates()).must().haveSizeOfAtMost(MAX_CERTIFICATES).forContext(POST).withMessage(String.format("Must not provide more than %d certificates per load balancer.", MAX_CERTIFICATES));
            result(ctxsLoadBalancer.getCertificates()).if_().exist().then().must().delegateTo(new CertificateRefValidator(certRefValidatorBuilder).getValidator(), POST).forContext(POST);
        }
    }
}
