package org.openstack.atlas.ctxs.service.domain.entity;

import org.openstack.atlas.service.domain.entity.*;

import javax.persistence.*;
import java.io.Serializable;

@javax.persistence.Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="vendor",
    discriminatorType=DiscriminatorType.STRING
)
@DiscriminatorValue("CTXS")
@Table(name = "certificateref")
public class CertificateRef extends org.openstack.atlas.service.domain.entity.Entity implements Serializable {
    private final static long serialVersionUID = 512512324L;

    @ManyToOne
    @JoinColumn(name = "loadbalancer_id")
    private CtxsLoadBalancer loadbalancer;

    @Column(name = "idref", nullable = false)
    private Integer idref;

    public CtxsLoadBalancer getLoadbalancer() {
        return loadbalancer;
    }

    public void setLoadbalancer(CtxsLoadBalancer loadbalancer) {
        this.loadbalancer = loadbalancer;
    }

    public Integer getIdref() {
        return idref;
    }

    public void setIdref(Integer idref) {
        this.idref = idref;
    }

    @Override
    public String toString() {
        return "CertificateRef {" +
               "    idref=" + idref.toString() +
                '}';
    }
}
