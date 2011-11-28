package org.openstack.atlas.ctxs.api.resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openstack.atlas.api.response.ResponseFactory;
import org.openstack.atlas.api.validation.context.HttpRequestType;
import org.openstack.atlas.api.validation.result.ValidatorResult;
import org.openstack.atlas.service.domain.operation.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;


import org.openstack.atlas.service.domain.repository.LoadBalancerRepository;
import org.openstack.atlas.core.api.v1.LoadBalancers;
import org.openstack.atlas.core.api.v1.LoadBalancer;

import org.openstack.atlas.service.domain.pojo.MessageDataContainer;
import org.openstack.atlas.api.resource.provider.CommonDependencyProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.*;


@Primary
@Controller
@Scope("request")
public class CertificatesResource extends CommonDependencyProvider  {
                                        
    public static Log LOG = LogFactory.getLog(CertificatesResource.class.getName());
    private HttpHeaders requestHeaders;
    protected Integer accountId;

    @Autowired
    protected LoadBalancerRepository loadBalancerRepository;
    

    @GET
    @Produces({APPLICATION_XML, APPLICATION_JSON, APPLICATION_ATOM_XML})
    public Response list() {
    
        LOG.debug("entered list /certificates");
        
        LoadBalancers _loadbalancers = new LoadBalancers();
        
        List<org.openstack.atlas.service.domain.entity.LoadBalancer> loadbalancers = loadBalancerRepository.getByAccountId(accountId);
        
        for (org.openstack.atlas.service.domain.entity.LoadBalancer loadBalancer : loadbalancers) {
            _loadbalancers.getLoadBalancers().add(dozerMapper.map(loadBalancer, org.openstack.atlas.core.api.v1.LoadBalancer.class, "SIMPLE_LB"));
        }
        
        return Response.status(Response.Status.OK).entity(_loadbalancers).build();
    }

    public void setRequestHeaders(HttpHeaders requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }    
}
