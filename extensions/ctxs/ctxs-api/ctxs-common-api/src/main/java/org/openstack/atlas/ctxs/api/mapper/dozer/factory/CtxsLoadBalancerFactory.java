package org.openstack.atlas.ctxs.api.mapper.dozer.factory;

import org.dozer.BeanFactory;
import org.openstack.atlas.api.v1.extensions.ctxs.LoadBalancer;

public class CtxsLoadBalancerFactory implements BeanFactory {

    @Override
    public Object createBean(Object source, Class<?> sourceClass, String targetBeanId) {

        if (sourceClass.equals(org.openstack.atlas.ctxs.service.domain.entity.CtxsLoadBalancer.class)
                || sourceClass.equals(org.openstack.atlas.service.domain.entity.LoadBalancer.class)) {
            LoadBalancer lb = new LoadBalancer();
            lb.setNodes(null);
            lb.setVirtualIps(null);
            lb.setCertificates(null);            
            return lb;
        }

        if (sourceClass.equals(LoadBalancer.class)) {
            org.openstack.atlas.ctxs.service.domain.entity.CtxsLoadBalancer lb = new org.openstack.atlas.ctxs.service.domain.entity.CtxsLoadBalancer();
            return lb;
        }
        
        if (sourceClass.equals(org.openstack.atlas.core.api.v1.LoadBalancer.class)) {
            org.openstack.atlas.service.domain.entity.LoadBalancer lb = new org.openstack.atlas.service.domain.entity.LoadBalancer();
            return lb;
        }        

        return null;
    }
}
