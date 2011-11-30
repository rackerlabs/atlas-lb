package org.openstack.atlas.rax.api.mapper.dozer.factory;

import org.dozer.BeanFactory;
import org.openstack.atlas.api.v1.extensions.rax.LoadBalancer;

public class RaxLoadBalancerFactory implements BeanFactory {

    @Override
    public Object createBean(Object source, Class<?> sourceClass, String targetBeanId) {

        if (sourceClass.equals(org.openstack.atlas.rax.domain.entity.RaxLoadBalancer.class)) {
            LoadBalancer lb = new LoadBalancer();
            lb.setNodes(null);
            lb.setVirtualIps(null);
            lb.setAccessList(null);            
            return lb;
        }
        
        if (sourceClass.equals(org.openstack.atlas.service.domain.entity.LoadBalancer.class)) {
            org.openstack.atlas.core.api.v1.LoadBalancer lb = new org.openstack.atlas.core.api.v1.LoadBalancer();
            lb.setNodes(null);
            lb.setVirtualIps(null);
            return lb;
        }        

        if (sourceClass.equals(LoadBalancer.class)) {
            org.openstack.atlas.rax.domain.entity.RaxLoadBalancer lb = new org.openstack.atlas.rax.domain.entity.RaxLoadBalancer();
            return lb;
        }
        
        if (sourceClass.equals(org.openstack.atlas.core.api.v1.LoadBalancer.class)) {
            org.openstack.atlas.service.domain.entity.LoadBalancer lb = new org.openstack.atlas.service.domain.entity.LoadBalancer();
            return lb;
        }        

        return null;
    }
}
