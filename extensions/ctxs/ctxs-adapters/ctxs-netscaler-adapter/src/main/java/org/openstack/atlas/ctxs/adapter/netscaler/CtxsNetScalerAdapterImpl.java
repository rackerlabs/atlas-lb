package org.openstack.atlas.ctxs.adapter.netscaler;


import org.openstack.atlas.ctxs.service.domain.entity.*;
import org.openstack.atlas.service.domain.entity.*;
import org.openstack.atlas.service.domain.repository.NodeRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import org.openstack.atlas.ctxs.adapter.CtxsLoadBalancerAdapter;

import org.openstack.atlas.adapter.LoadBalancerEndpointConfiguration;
import org.openstack.atlas.adapter.exception.*;

import org.openstack.atlas.adapter.netscaler.NetScalerAdapterImpl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.*;
import java.io.*;
import java.net.*;





@Primary
@Service
public class CtxsNetScalerAdapterImpl extends NetScalerAdapterImpl implements CtxsLoadBalancerAdapter {

    public static Log LOG = LogFactory.getLog(CtxsNetScalerAdapterImpl.class.getName());
    

    @Override
    public void createLoadBalancer(LoadBalancerEndpointConfiguration config, LoadBalancer lb)
            throws AdapterException 
    {
    
        if (lb instanceof CtxsLoadBalancer) {  
             LOG.info("Reaching CtxsNetScalerAdapterImpl.createLoadBalancer() with extended loadbalancer");
             // NOP
        } else {
             LOG.info("Reaching CtxsNetScalerAdapterImpl.createLoadBalancer() with core loadbalancer");
             super.createLoadBalancer(config,lb);        
        }          
    }
    
}
