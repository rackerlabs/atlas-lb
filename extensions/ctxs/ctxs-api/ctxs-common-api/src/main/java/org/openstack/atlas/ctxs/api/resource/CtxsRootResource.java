package org.openstack.atlas.ctxs.api.resource;

import org.springframework.beans.factory.annotation.Autowired;

import org.openstack.atlas.api.resource.RootResource;

import javax.ws.rs.Path;

@Path("{accountId: [-+]?[0-9][0-9]*}")
public class CtxsRootResource extends RootResource {
    @Autowired
    private CertificatesResource certificatesResource;
    

    @Path("certificates")
    public CertificatesResource retrieveCertificatesResource() {
        return certificatesResource;
    }  
}
