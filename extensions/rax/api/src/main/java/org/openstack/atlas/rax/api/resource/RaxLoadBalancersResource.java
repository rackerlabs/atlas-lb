package org.openstack.atlas.rax.api.resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openstack.atlas.api.resource.LoadBalancersResource;
import org.openstack.atlas.api.response.ResponseFactory;
import org.openstack.atlas.api.validation.context.HttpRequestType;
import org.openstack.atlas.api.validation.result.ValidatorResult;
import org.openstack.atlas.core.api.v1.IpVersion;
import org.openstack.atlas.core.api.v1.LoadBalancer;
import org.openstack.atlas.core.api.v1.VipType;
import org.openstack.atlas.core.api.v1.VirtualIp;
import org.openstack.atlas.rax.domain.entity.RaxLoadBalancer;
import org.openstack.atlas.rax.domain.pojo.RaxMessageDataContainer;
import org.openstack.atlas.service.domain.entity.LoadBalancerJoinVip;
import org.openstack.atlas.service.domain.entity.VirtualIpType;
import org.openstack.atlas.service.domain.operation.CoreOperation;
import org.openstack.atlas.service.domain.pojo.VirtualIpDozerWrapper;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Primary
@Controller
@Scope("request")
public class RaxLoadBalancersResource extends LoadBalancersResource {
    public static Log LOG = LogFactory.getLog(RaxLoadBalancersResource.class.getName());

    @Override
    public Response create(LoadBalancer _loadBalancer) {
        LOG.debug("loadbalancer: " + _loadBalancer);

        ValidatorResult result = validator.validate(_loadBalancer, HttpRequestType.POST);
        if (!result.passedValidation()) {
            return ResponseFactory.getValidationFaultResponse(result);
        }
        try {
            RaxLoadBalancer raxLoadBalancer = dozerMapper.map(_loadBalancer, RaxLoadBalancer.class);
            raxLoadBalancer.setAccountId(accountId);

            fixLoadBalancerVirtualIps(_loadBalancer, raxLoadBalancer);

            //This call should be moved somewhere else
            virtualIpService.addAccountRecord(accountId);

            LOG.debug(String.format("The number of IPV4 Virtual Ips are: %d", raxLoadBalancer.getLoadBalancerJoinVipSet().size()));
            LOG.debug(String.format("The number of IPV6 Virtual Ips are: %d", raxLoadBalancer.getLoadBalancerJoinVip6Set().size()));

            org.openstack.atlas.service.domain.entity.LoadBalancer newlyCreatedLb = loadbalancerService.create(raxLoadBalancer);
            RaxMessageDataContainer msg = new RaxMessageDataContainer();
            msg.setLoadBalancer(newlyCreatedLb);
            asyncService.callAsyncLoadBalancingOperation(CoreOperation.CREATE_LOADBALANCER, msg);
            return Response.status(Response.Status.ACCEPTED).entity(dozerMapper.map(newlyCreatedLb, LoadBalancer.class)).build();
        } catch (Exception e) {
            return ResponseFactory.getErrorResponse(e, null, null);
        }
    }

    private void fixLoadBalancerVirtualIps(LoadBalancer apiLoadBalancer, RaxLoadBalancer raxLoadBalancer)
    {
        // If the user hasn't specified an IPversion attribute in the virtual IP, then we add
        // a IPV4 virtual IP, so that when RAX extension is used, the user will end up with
        // the default IPv6 Virtual IP allocated by core + this one allocated by the extension.

        List<VirtualIp> vips = apiLoadBalancer.getVirtualIps();

        if (vips.size() > 0) {
            // We expect there is only one Virtual IP in the array. Validation in Core would have ensured this
            VirtualIp vip = vips.get(0);

            if ((vip.getIpVersion() == null) && (vip.getType() == VipType.PUBLIC)) {

                vip.setIpVersion(IpVersion.IPV4);
                Set<LoadBalancerJoinVip> loadBalancerJoinVipSet = buildLoadBalancerJoinVipSet(vip);

                VirtualIpDozerWrapper dozerWrapper = new VirtualIpDozerWrapper();
                dozerWrapper.setLoadBalancerJoinVipSet(loadBalancerJoinVipSet);

                raxLoadBalancer.setVirtualIpDozerWrapper(dozerWrapper);
            }
        }
    }


    private Set<LoadBalancerJoinVip> buildLoadBalancerJoinVipSet(VirtualIp vip) {
        LoadBalancerJoinVip loadBalancerJoinVip = new LoadBalancerJoinVip();
        Set<LoadBalancerJoinVip> loadBalancerJoinVipSet = new HashSet<LoadBalancerJoinVip>();

        org.openstack.atlas.service.domain.entity.VirtualIp domainVip = new org.openstack.atlas.service.domain.entity.VirtualIp();
        domainVip.setId(vip.getId());
        domainVip.setAddress(vip.getAddress());

        switch (vip.getType()) {
            case PUBLIC:
                domainVip.setVipType(VirtualIpType.PUBLIC);
                break;
            case PRIVATE:
                domainVip.setVipType(VirtualIpType.PRIVATE);
                break;
        }


        loadBalancerJoinVip.setVirtualIp(domainVip);
        loadBalancerJoinVipSet.add(loadBalancerJoinVip);

        return loadBalancerJoinVipSet;
    }
}