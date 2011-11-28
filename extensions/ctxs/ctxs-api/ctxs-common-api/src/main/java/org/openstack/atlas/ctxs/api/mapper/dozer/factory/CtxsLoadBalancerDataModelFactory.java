package org.openstack.atlas.ctxs.api.mapper.dozer.factory;

import org.dozer.BeanFactory;
import org.openstack.atlas.api.v1.extensions.ctxs.LoadBalancer;

public class CtxsLoadBalancerDataModelFactory implements BeanFactory {

    @Override
    public Object createBean(Object source, Class<?> sourceClass, String targetBeanId) {

        if (sourceClass.equals(LoadBalancer.class)) {
            org.openstack.atlas.core.api.v1.LoadBalancer lb = new org.openstack.atlas.core.api.v1.LoadBalancer();
            lb.setNodes(null);
            lb.setVirtualIps(null);
            return lb;
        }
        

        return null;
    }
}
