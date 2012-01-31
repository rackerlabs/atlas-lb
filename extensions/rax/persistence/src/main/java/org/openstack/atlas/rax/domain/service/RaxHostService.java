package org.openstack.atlas.rax.domain.service;

import org.openstack.atlas.rax.domain.entity.RaxHost;
import org.springframework.stereotype.Service;

@Service
public interface RaxHostService {
    void create(RaxHost host);
}
