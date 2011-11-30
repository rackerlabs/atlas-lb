package org.openstack.atlas.rax.api.mapper.dozer.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openstack.atlas.api.v1.extensions.rax.AccessList;
import org.openstack.atlas.api.v1.extensions.rax.NetworkItem;
import org.openstack.atlas.core.api.v1.IpVersion;
import org.openstack.atlas.api.v1.extensions.rax.NetworkItemType;
import org.openstack.atlas.api.v1.extensions.rax.LoadBalancer;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import java.util.List;

public class ExtensionObjectMapperTest {
    private LoadBalancer unMarshalledLb;

    public ExtensionObjectMapperTest() throws JAXBException {
        String lbRequestFile = "/rax-lb.xml";
        JAXBContext jaxbContext = JAXBContext.newInstance(org.openstack.atlas.api.v1.extensions.rax.ObjectFactory.class.getPackage().getName());
        this.unMarshalledLb = (LoadBalancer) jaxbContext.createUnmarshaller().unmarshal(getClass().getResourceAsStream(lbRequestFile));
    }

    @Test
    public void shouldUnmarshallAccessListFromAniesList() throws JAXBException, ParserConfigurationException, IOException, SAXException {
        List<NetworkItem> accessList = unMarshalledLb.getAccessList();


        Assert.assertNotNull(accessList);
        Assert.assertEquals("1.1.1.1", accessList.get(0).getAddress());
        Assert.assertEquals(IpVersion.IPV4, accessList.get(0).getIpVersion());
        Assert.assertEquals(NetworkItemType.DENY, accessList.get(0).getType());
    }

    @Test
    public void shouldUnmarshallCrazyNameFromOtherAttributes() {
        String crazyName = ExtensionObjectMapper.getOtherAttribute(unMarshalledLb.getOtherAttributes(), "crazyName");

        Assert.assertEquals("foo", crazyName);
    }
}
