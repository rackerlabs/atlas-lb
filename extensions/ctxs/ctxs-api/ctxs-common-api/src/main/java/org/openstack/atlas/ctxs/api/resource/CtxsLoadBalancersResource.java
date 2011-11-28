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

import org.openstack.atlas.api.validation.validator.LoadBalancerValidator;
import org.openstack.atlas.core.api.v1.LoadBalancers;

import org.openstack.atlas.service.domain.repository.LoadBalancerRepository;
import org.openstack.atlas.service.domain.service.LoadBalancerService;
import org.openstack.atlas.service.domain.service.VirtualIpService;
import org.openstack.atlas.service.domain.exception.BadRequestException;


import org.openstack.atlas.api.v1.extensions.ctxs.LoadBalancer;
import org.openstack.atlas.api.v1.extensions.ctxs.CertificateRef;
import org.openstack.atlas.ctxs.service.domain.entity.CtxsLoadBalancer;
import org.openstack.atlas.api.resource.LoadBalancerResource;
import org.openstack.atlas.service.domain.pojo.MessageDataContainer;
import org.openstack.atlas.api.resource.provider.CommonDependencyProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.List;
import javax.xml.bind.JAXBElement;

import static javax.ws.rs.core.MediaType.*;


@Primary
@Controller
@Scope("request")
public class CtxsLoadBalancersResource extends org.openstack.atlas.api.resource.LoadBalancersResource  {
                                        
    public static Log LOG = LogFactory.getLog(CtxsLoadBalancersResource.class.getName());



/*

    @POST
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    public Response create(LoadBalancer _loadBalancer) {
        LOG.debug("creating an extended loadBalancer (ctxs extensions)");    

        ValidatorResult result = validator.validate(_loadBalancer, HttpRequestType.POST);
        
        if (!result.passedValidation()) {
            return ResponseFactory.getValidationFaultResponse(result);
        }
        try {
            CtxsLoadBalancer ctxsLoadBalancer = dozerMapper.map(_loadBalancer, org.openstack.atlas.ctxs.service.domain.entity.CtxsLoadBalancer.class);
            ctxsLoadBalancer.setAccountId(accountId);

            //This call should be moved somewhere else
            virtualIpService.addAccountRecord(accountId);

            ctxsLoadBalancer = (CtxsLoadBalancer) loadbalancerService.create(ctxsLoadBalancer);
            
            MessageDataContainer dataContainer = new MessageDataContainer();
            dataContainer.setLoadBalancer(ctxsLoadBalancer);
            
            asyncService.callAsyncLoadBalancingOperation(Operation.CREATE_LOADBALANCER, dataContainer);
            return Response.status(Response.Status.ACCEPTED).entity(dozerMapper.map(ctxsLoadBalancer, LoadBalancer.class)).build();
        } catch (Exception e) {
            return ResponseFactory.getErrorResponse(e, null, null);
        }
    }

*/
    
    
       
    private Response createCtxsLoadBalancer(LoadBalancer _loadBalancer) {
        LOG.debug("creating an extended loadBalancer (ctxs extensions)");    

        ValidatorResult result = validator.validate(_loadBalancer, HttpRequestType.POST);
        
        if (!result.passedValidation()) {
            return ResponseFactory.getValidationFaultResponse(result);
        }
        try {
            CtxsLoadBalancer ctxsLoadBalancer = dozerMapper.map(_loadBalancer, org.openstack.atlas.ctxs.service.domain.entity.CtxsLoadBalancer.class);
            ctxsLoadBalancer.setAccountId(accountId);

            //This call should be moved somewhere else
            virtualIpService.addAccountRecord(accountId);

            ctxsLoadBalancer = (CtxsLoadBalancer) loadbalancerService.create(ctxsLoadBalancer);
            
            MessageDataContainer dataContainer = new MessageDataContainer();
            dataContainer.setLoadBalancer(ctxsLoadBalancer);
            
            asyncService.callAsyncLoadBalancingOperation(Operation.CREATE_LOADBALANCER, dataContainer);
            return Response.status(Response.Status.ACCEPTED).entity(dozerMapper.map(ctxsLoadBalancer, LoadBalancer.class)).build();
        } catch (Exception e) {
            return ResponseFactory.getErrorResponse(e, null, null);
        }
    }

  
    
    
    @POST
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    public Response create(JAXBElement<org.openstack.atlas.api.v1.extensions.ctxs.LoadBalancer> payload)
    {
    
        LOG.debug(String.format("JAXBElement's name is: %s", payload.getName()));
        LOG.debug(String.format("JAXBElement's namespace URI is: %s", payload.getName().getNamespaceURI()));        
        LOG.debug(String.format("JAXBElement's declared type is: %s", payload.getDeclaredType().getName()));
        LOG.debug(String.format("JAXBElement's scope: %s", payload.getScope().getName()));   
        LOG.debug(String.format("is JAXBElement a global scope ?: %b", payload.isGlobalScope()));                     
        LOG.debug(String.format("is JAXBElement type substituted ?: %b", payload.isTypeSubstituted()));                     
                    
        Object lbObj = payload.getValue();
        String namespace = payload.getName().getNamespaceURI();
        
        if (namespace.equals("http://docs.openstack.org/atlas/api/v1.1") || (namespace.equals("")) || (namespace == null))
        {
             // we assume this is a core loadbalancer we are passed in.
            org.openstack.atlas.core.api.v1.LoadBalancer _loadBalancer = (org.openstack.atlas.core.api.v1.LoadBalancer) lbObj;
            
            LOG.debug(String.format("loadBalancer name is: %s", _loadBalancer.getName()));  
            LOG.debug(String.format("loadBalancer protocol is: %s", _loadBalancer.getProtocol()));  
            LOG.debug(String.format("loadBalancer algorithm is: %s", _loadBalancer.getAlgorithm()));  
            LOG.debug(String.format("loadBalancer port is: %d", _loadBalancer.getPort()));                          
            List<org.openstack.atlas.core.api.v1.Node> nodes = _loadBalancer.getNodes();
            LOG.debug(String.format("loadBalancer number of nodes is: %d", nodes.size()));  
                        
            for (org.openstack.atlas.core.api.v1.Node node : nodes)
            {
                LOG.debug(String.format("node condition is: %b", node.isEnabled()));  
            }
                  
            List<CertificateRef> certificates = ((LoadBalancer)lbObj).getCertificates();
            
                              
            if ((certificates != null) && (certificates.size() > 0))                 
                return ResponseFactory.getErrorResponse(new BadRequestException("invalid element of loadBalancer"), null, null);
                
            // We call create() of core
            return create(_loadBalancer);
        } 
        
        if (payload.getName().getNamespaceURI().equals("http://docs.openstack.org/atlas/api/v1.1/extensions/ctxs"))
        {
            // We expect an extension loadbalancer passed in.
            LoadBalancer _loadBalancer = (LoadBalancer) lbObj;
            
            LOG.debug(String.format("loadBalancer name is: %s", _loadBalancer.getName()));  
            LOG.debug(String.format("loadBalancer protocol is: %s", _loadBalancer.getProtocol()));   
            LOG.debug(String.format("loadBalancer algorithm is: %s", _loadBalancer.getAlgorithm()));  
            LOG.debug(String.format("loadBalancer port is: %d", _loadBalancer.getPort()));                                                      
            List<org.openstack.atlas.core.api.v1.Node> nodes = _loadBalancer.getNodes();
            LOG.debug(String.format("loadBalancer number of nodes is: %d", nodes.size()));  
                        
            for (org.openstack.atlas.core.api.v1.Node node : nodes)
            {
                LOG.debug(String.format("node condition is: %b", node.isEnabled()));  
            }
                              
                                                      
            return createCtxsLoadBalancer(_loadBalancer);        
        }
        
        return ResponseFactory.getErrorResponse(new Exception("XML namespace of body element unknown"));
    }
    

    
}
