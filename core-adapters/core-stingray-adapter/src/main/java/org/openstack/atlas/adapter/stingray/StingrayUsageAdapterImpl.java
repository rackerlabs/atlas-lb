package org.openstack.atlas.adapter.stingray;

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openstack.atlas.adapter.LoadBalancerEndpointConfiguration;
import org.openstack.atlas.adapter.UsageAdapter;
import org.openstack.atlas.adapter.exception.AdapterException;
import org.openstack.atlas.adapter.exception.BadRequestException;
import org.openstack.atlas.adapter.stingray.helper.StingrayNameHelper;
import org.openstack.atlas.adapter.stingray.service.StingrayServiceStubs;
import org.openstack.atlas.service.domain.entity.LoadBalancer;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.rmi.RemoteException;
import java.util.*;

@Service
public class StingrayUsageAdapterImpl implements UsageAdapter {
    private static Log LOG = LogFactory.getLog(StingrayUsageAdapterImpl.class.getName());

    @Override
    public Map<Integer, Long> getTransferBytesIn(LoadBalancerEndpointConfiguration config, List<LoadBalancer> lbs) throws AdapterException {
        try {
            StingrayServiceStubs serviceStubs = getServiceStubs(config);
            Map<Integer, Long> bytesInMap = new HashMap<Integer, Long>();
            List<String> validVsNames = getValidVsNames(config, toVirtualServerNames(lbs));

            long[] bytesIn = serviceStubs.getSystemStatsBinding().getVirtualserverBytesIn(validVsNames.toArray(new String[validVsNames.size()]));

            for (int i = 0; i < validVsNames.size(); i++) {
                bytesInMap.put(StingrayNameHelper.stripLbIdFromName(validVsNames.get(i)), bytesIn[i]);
            }

            return bytesInMap;
        } catch (RemoteException e) {
            throw new AdapterException(e);
        }
    }

    @Override
    public Map<Integer, Long> getTransferBytesOut(LoadBalancerEndpointConfiguration config, List<LoadBalancer> lbs) throws AdapterException {
        try {
            StingrayServiceStubs serviceStubs = getServiceStubs(config);
            Map<Integer, Long> bytesOutMap = new HashMap<Integer, Long>();
            List<String> validVsNames = getValidVsNames(config, toVirtualServerNames(lbs));

            long[] bytesOut = serviceStubs.getSystemStatsBinding().getVirtualserverBytesOut(validVsNames.toArray(new String[validVsNames.size()]));

            for (int i = 0; i < validVsNames.size(); i++) {
                bytesOutMap.put(StingrayNameHelper.stripLbIdFromName(validVsNames.get(i)), bytesOut[i]);
            }

            return bytesOutMap;
        } catch (RemoteException e) {
            throw new AdapterException(e);
        }
    }

    /*
    * *********************
    * * PROTECTED METHODS *
    * *********************
    */

    protected StingrayServiceStubs getServiceStubs(LoadBalancerEndpointConfiguration config) throws AxisFault {
        return StingrayServiceStubs.getServiceStubs(config.getEndpointUrl(), config.getUsername(), config.getPassword());
    }

    protected List<String> toVirtualServerNames(List<LoadBalancer> lbs) throws BadRequestException {
        List<String> virtualServerNames = new ArrayList<String>();

        for (LoadBalancer lb : lbs) {
            virtualServerNames.add(StingrayNameHelper.generateNameWithAccountIdAndLoadBalancerId(lb));
        }

        return virtualServerNames;
    }

    protected List<String> getValidVsNames(LoadBalancerEndpointConfiguration config, List<String> virtualServerNames) throws RemoteException, BadRequestException {
        Set<String> allLoadBalancerNames = new HashSet<String>(getStatsSystemLoadBalancerNames(config));
        Set<String> loadBalancerNamesForHost = new HashSet<String>(virtualServerNames);
        loadBalancerNamesForHost.retainAll(allLoadBalancerNames); // Get the intersection
        return new ArrayList<String>(loadBalancerNamesForHost);
    }

    protected List<String> getStatsSystemLoadBalancerNames(LoadBalancerEndpointConfiguration config) throws RemoteException {
        StingrayServiceStubs serviceStubs = getServiceStubs(config);
        List<String> loadBalancerNames = new ArrayList<String>();
        loadBalancerNames.addAll(Arrays.asList(serviceStubs.getSystemStatsBinding().getVirtualservers()));
        return loadBalancerNames;
    }
}
