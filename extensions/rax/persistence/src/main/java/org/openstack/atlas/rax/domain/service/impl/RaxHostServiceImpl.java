package org.openstack.atlas.rax.domain.service.impl;

import org.openstack.atlas.rax.domain.entity.RaxHost;
import org.openstack.atlas.rax.domain.repository.RaxHostRepository;
import org.openstack.atlas.rax.domain.service.RaxHostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RaxHostServiceImpl implements RaxHostService {

    @Autowired
    private RaxHostRepository raxHostRepository;

    @Override
    public void create(RaxHost host) {

    }
}
