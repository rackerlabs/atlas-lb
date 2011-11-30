package org.openstack.atlas.rax.domain.stub;

import org.openstack.atlas.rax.datamodel.XmlHelper;
import org.openstack.atlas.rax.domain.entity.AccessListType;
import org.openstack.atlas.rax.domain.entity.RaxLoadBalancer;
import org.openstack.atlas.rax.domain.helper.ExtensionConverter;

import org.openstack.atlas.core.api.v1.IpVersion;
import org.openstack.atlas.api.v1.extensions.rax.NetworkItemType;

import org.openstack.atlas.service.domain.stub.StubFactory;
import org.openstack.atlas.service.domain.entity.*;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

public class RaxStubFactory extends StubFactory {
    protected static final Integer NETWORK_ITEM1_ID = 1;
    protected static final String NETWORK_ITEM1_ADDRESS = "1.1.1.1";
    protected static final String NETWORK_ITEM1_IP_VERSION = "IPV4";
    protected static final String NETWORK_ITEM1_TYPE = "DENY";


    public static org.openstack.atlas.api.v1.extensions.rax.LoadBalancer createMinimalDataModelLoadBalancerForPost() {
        org.openstack.atlas.api.v1.extensions.rax.LoadBalancer loadBalancer = new org.openstack.atlas.api.v1.extensions.rax.LoadBalancer();

        loadBalancer.setName(LOAD_BALANCER_NAME);

        org.openstack.atlas.core.api.v1.Node node1 = createDataModelNodeForPost(null, NODE1_ADDRESS, NODE1_PORT, null, true, null);
        final org.openstack.atlas.core.api.v1.Nodes nodes = new org.openstack.atlas.core.api.v1.Nodes();
        nodes.getNodes().add(node1);
        loadBalancer.getNodes().addAll(nodes.getNodes());

        return loadBalancer;
    }
    
    
    public static org.openstack.atlas.api.v1.extensions.rax.LoadBalancer createHydratedDataModelLoadBalancerForRaxPost() throws JAXBException, ParserConfigurationException {
        org.openstack.atlas.api.v1.extensions.rax.LoadBalancer loadBalancer = createMinimalDataModelLoadBalancerForPost();

        loadBalancer.setPort(LOAD_BALANCER_PORT);
        loadBalancer.setProtocol(LOAD_BALANCER_PROTOCOL);
        loadBalancer.setAlgorithm(LOAD_BALANCER_ALGORITHM);

        loadBalancer.getNodes().get(0).setEnabled(NODE1_ENABLED);

        org.openstack.atlas.core.api.v1.VirtualIp virtualIp1 = new org.openstack.atlas.core.api.v1.VirtualIp();
        virtualIp1.setType(org.openstack.atlas.core.api.v1.VipType.fromValue(VIP1_TYPE));
        virtualIp1.setIpVersion(IpVersion.fromValue(VIP1_VERSION));
        loadBalancer.getVirtualIps().add(virtualIp1);

        org.openstack.atlas.core.api.v1.VirtualIp virtualIp2 = new org.openstack.atlas.core.api.v1.VirtualIp();
        virtualIp2.setId(VIP2_ID);

        loadBalancer.setConnectionThrottle(createHydratedDataModelConnectionThrottle());
        loadBalancer.setHealthMonitor(createHydratedDataModelHealthMonitor());
        loadBalancer.setSessionPersistence(createHydratedDataModelSessionPersistence());


        loadBalancer.getOtherAttributes().put(new QName("http://docs.openstack.org/atlas/api/v1.1/extensions/rax", "crazyName", "rax"), "foo");

        List<org.openstack.atlas.api.v1.extensions.rax.NetworkItem> accessList = loadBalancer.getAccessList();
        org.openstack.atlas.api.v1.extensions.rax.NetworkItem networkItem = new org.openstack.atlas.api.v1.extensions.rax.NetworkItem();
        
        networkItem.setAddress(NETWORK_ITEM1_ADDRESS);
        networkItem.setIpVersion(IpVersion.valueOf(NETWORK_ITEM1_IP_VERSION));
        networkItem.setType(NetworkItemType.valueOf(NETWORK_ITEM1_TYPE));
        
        accessList.add(networkItem);

        loadBalancer.setAccessList(accessList);


        return loadBalancer;
    }
    

    public static org.openstack.atlas.api.v1.extensions.rax.LoadBalancer createHydratedDataModelLoadBalancer() throws Exception {
    
         // TODO: Call minimal method first and use the values from it.
        org.openstack.atlas.api.v1.extensions.rax.LoadBalancer loadBalancer = new org.openstack.atlas.api.v1.extensions.rax.LoadBalancer();

        loadBalancer.setId(LOAD_BALANCER_ID);
        loadBalancer.setName(LOAD_BALANCER_NAME);
        loadBalancer.setPort(LOAD_BALANCER_PORT);
        loadBalancer.setProtocol(LOAD_BALANCER_PROTOCOL);
        loadBalancer.setAlgorithm(LOAD_BALANCER_ALGORITHM);
        loadBalancer.setStatus(LOAD_BALANCER_STATUS);

        org.openstack.atlas.core.api.v1.Node node1 = createDataModelNodeForPost(NODE1_ID, NODE1_ADDRESS, NODE1_PORT, NODE1_WEIGHT, NODE1_ENABLED, NODE1_STATUS);
        org.openstack.atlas.core.api.v1.Node node2 = createDataModelNodeForPost(NODE2_ID, NODE2_ADDRESS, NODE2_PORT, NODE2_WEIGHT, NODE2_ENABLED, NODE2_STATUS);
        final org.openstack.atlas.core.api.v1.Nodes nodes = new org.openstack.atlas.core.api.v1.Nodes();
        nodes.getNodes().add(node1);
        nodes.getNodes().add(node2);
        loadBalancer.getNodes().addAll(nodes.getNodes());

        org.openstack.atlas.core.api.v1.VirtualIp virtualIp1 = new org.openstack.atlas.core.api.v1.VirtualIp();
        virtualIp1.setId(VIP1_ID);
        virtualIp1.setAddress(VIP1_ADDRESS);
        virtualIp1.setType(org.openstack.atlas.core.api.v1.VipType.fromValue(VIP1_TYPE));
        virtualIp1.setIpVersion(IpVersion.fromValue(VIP1_VERSION));
        loadBalancer.getVirtualIps().add(virtualIp1);

        loadBalancer.setConnectionThrottle(createHydratedDataModelConnectionThrottle());
        loadBalancer.setHealthMonitor(createHydratedDataModelHealthMonitor());
        loadBalancer.setSessionPersistence(createHydratedDataModelSessionPersistence());
        loadBalancer.setCreated(Calendar.getInstance());
        loadBalancer.setUpdated(Calendar.getInstance());

        loadBalancer.getOtherAttributes().put(new QName("http://docs.openstack.org/atlas/api/v1.1/extensions/rax", "crazyName", "rax"), "foo");

        List<org.openstack.atlas.api.v1.extensions.rax.NetworkItem> accessList = loadBalancer.getAccessList();
        org.openstack.atlas.api.v1.extensions.rax.NetworkItem networkItem = new org.openstack.atlas.api.v1.extensions.rax.NetworkItem();
        
        networkItem.setAddress(NETWORK_ITEM1_ADDRESS);
        networkItem.setIpVersion(IpVersion.valueOf(NETWORK_ITEM1_IP_VERSION));
        networkItem.setType(NetworkItemType.valueOf(NETWORK_ITEM1_TYPE));
        
        accessList.add(networkItem);

        loadBalancer.setAccessList(accessList);

        return loadBalancer;
    }


    public static RaxLoadBalancer createHydratedDomainLoadBalancer() {
        RaxLoadBalancer loadBalancer = new RaxLoadBalancer();

        loadBalancer.setId(LOAD_BALANCER_ID);
        loadBalancer.setName(LOAD_BALANCER_NAME);
        loadBalancer.setPort(LOAD_BALANCER_PORT);
        loadBalancer.setProtocol(LOAD_BALANCER_PROTOCOL);
        loadBalancer.setAlgorithm(LOAD_BALANCER_ALGORITHM);
        loadBalancer.setStatus(LOAD_BALANCER_STATUS);

        Node node1 = new Node();
        node1.setId(NODE1_ID);
        node1.setAddress(NODE1_ADDRESS);
        node1.setPort(NODE1_PORT);
        node1.setEnabled(NODE1_ENABLED);
        node1.setStatus(NODE1_STATUS);
        loadBalancer.getNodes().add(node1);

        Node node2 = new Node();
        node2.setId(NODE2_ID);
        node2.setAddress(NODE2_ADDRESS);
        node2.setPort(NODE2_PORT);
        node2.setEnabled(NODE2_ENABLED);
        node2.setStatus(NODE2_STATUS);
        loadBalancer.getNodes().add(node2);

        VirtualIp virtualIp1 = new VirtualIp();
        virtualIp1.setId(VIP1_ID);
        virtualIp1.setAddress(VIP1_ADDRESS);
        virtualIp1.setVipType(org.openstack.atlas.service.domain.entity.VirtualIpType.valueOf(VIP1_TYPE));

        LoadBalancerJoinVip loadBalancerJoinVip = new LoadBalancerJoinVip(LOAD_BALANCER_PORT, loadBalancer, virtualIp1);
        loadBalancer.getLoadBalancerJoinVipSet().add(loadBalancerJoinVip);

        ConnectionThrottle throttle = new ConnectionThrottle();
        throttle.setMaxRequestRate(CONNECTION_THROTTLE_MAX_REQUEST_RATE);
        throttle.setRateInterval(CONNECTION_THROTTLE_RATE_INTERVAL);
        loadBalancer.setConnectionThrottle(throttle);

        HealthMonitor healthMonitor = new HealthMonitor();
        healthMonitor.setAttemptsBeforeDeactivation(HEALTH_MONITOR_ATTEMPTS_BEFORE_DEACTIVATION);
        healthMonitor.setDelay(HEALTH_MONITOR_DELAY);
        healthMonitor.setTimeout(HEALTH_MONITOR_TIMEOUT);
        healthMonitor.setPath(HEALTH_MONITOR_PATH);
        healthMonitor.setType(HEALTH_MONITOR_TYPE);
        loadBalancer.setHealthMonitor(healthMonitor);

        loadBalancer.setSessionPersistence(createHydratedDomainSessionPersistence());

        loadBalancer.setCreated(Calendar.getInstance());
        loadBalancer.setUpdated(Calendar.getInstance());

        // RAX SPECIFIC SETTINGS
        loadBalancer.setCrazyName("foobar");

        org.openstack.atlas.rax.domain.entity.AccessList accessList = new org.openstack.atlas.rax.domain.entity.AccessList();
        accessList.setId(NETWORK_ITEM1_ID);
        accessList.setIpAddress(NETWORK_ITEM1_ADDRESS);
        accessList.setIpVersion(org.openstack.atlas.service.domain.entity.IpVersion.valueOf(NETWORK_ITEM1_IP_VERSION));
        accessList.setType(AccessListType.valueOf(NETWORK_ITEM1_TYPE));
        Set<org.openstack.atlas.rax.domain.entity.AccessList> accessLists = new HashSet<org.openstack.atlas.rax.domain.entity.AccessList>();
        accessLists.add(accessList);
        loadBalancer.setAccessLists(accessLists);

        return loadBalancer;
    }
}
