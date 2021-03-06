package org.openstack.atlas.service.domain.services.helpers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openstack.atlas.restclients.auth.IdentityAuthClientImpl;
import org.openstack.atlas.restclients.auth.fault.IdentityFault;
import org.openstack.atlas.restclients.dns.DnsClient1_0;
import org.openstack.atlas.service.domain.exceptions.RdnsException;
import org.openstack.atlas.util.b64aes.Aes;
import org.openstack.atlas.util.config.LbConfiguration;
import org.openstack.atlas.util.config.MossoConfigValues;
import org.openstack.atlas.util.debug.Debug;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import org.openstack.identity.client.access.Access;
import org.openstack.identity.client.access.Token;
import org.openstack.identity.client.user.User;

public class RdnsHelper {

    private static final int BUFFSIZE = 1024 * 16;
    private static final int PAGESIZE = 4096;
    private static final Log LOG = LogFactory.getLog(RdnsHelper.class);
    private static final String LB_SERVICE_NAME = "cloudLoadBalancers";
    private int accountId = -1;
    private String lbaasBaseUrl;
    private String rdnsPublicUrl;
    private String rdnsAdminUrl;
    private String rdnsAdminUser;
    private String rdnsPasswd;
    private String authAdminUrl;
    private String authPublicUrl;
    private String authAdminUser;
    private String authAdminKey;
    private boolean useAdminAuth;

    public RdnsHelper(int aid) {
        this.accountId = aid;
        LbConfiguration conf = new LbConfiguration();
        lbaasBaseUrl = conf.getString(MossoConfigValues.base_uri);
        rdnsPublicUrl = conf.getString(MossoConfigValues.rdns_public_url);
        rdnsAdminUrl = conf.getString(MossoConfigValues.rdns_admin_url);
        rdnsAdminUser = conf.getString(MossoConfigValues.rdns_admin_user);
        authAdminUrl = conf.getString(MossoConfigValues.auth_management_uri);
        authAdminUser = conf.getString(MossoConfigValues.basic_auth_user);
        authPublicUrl = authAdminUrl; // So wayne doesn't need to duplicate configs.
        authAdminKey = conf.getString(MossoConfigValues.basic_auth_key);
        useAdminAuth = Boolean.parseBoolean(conf.getString(MossoConfigValues.rdns_use_service_admin));

        String key = conf.getString(MossoConfigValues.rdns_crypto_key);
        String ctext = conf.getString(MossoConfigValues.rdns_admin_passwd);
        try {
            rdnsPasswd = Aes.b64decrypt_str(ctext, key);
        } catch (Exception ex) {
            String stackTrace = Debug.getEST(ex);
            String fmt = "Error decrypting rDNS admin passwd. Call to delete "
                    + "PTR record will fail: %s unless configured to use impersonation";
            rdnsPasswd = "????";
        }
    }

    // Cause typing "newRdnsHelper().delPtrRecord(aid,lid,ip) doesn't looks as "
    // silly as  of "(new RdnsHelper()).delPtrRecord(aid,lid,ip)"
    public static RdnsHelper newRdnsHelper(int aid) {
        return new RdnsHelper(aid);
    }

    public static byte[] readFile(String fileName) throws FileNotFoundException, IOException {
        byte[] bytesOut;
        byte[] buff;
        int nbytes;
        File file = new File(fileName);
        FileInputStream is = new FileInputStream(file);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        while (true) {
            buff = new byte[BUFFSIZE];
            nbytes = is.read(buff);
            if (nbytes < 0) {
                break;
            }
            os.write(buff, 0, nbytes);
        }
        bytesOut = os.toByteArray();
        is.close();
        os.close();
        return bytesOut;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(PAGESIZE);
        sb.append(String.format("accountId=%d\n", accountId));
        sb.append(String.format("lbaasBaseUrl=%s\n", lbaasBaseUrl));
        sb.append(String.format("rdnsPublicUrl=%s\n", rdnsPublicUrl));
        sb.append(String.format("rdnsAdminUrl=%s\n", rdnsAdminUrl));
        sb.append(String.format("rdnsUser=%s\n", rdnsAdminUser));
        sb.append(String.format("rDnsPasswd=%s\n", rdnsPasswd));
        sb.append(String.format("authAdminUrl=%s\n", authAdminUrl));
        sb.append(String.format("authPublicUrl=%s\n", authPublicUrl));
        sb.append(String.format("authAdminUser=%s\n", authAdminUser));
        sb.append(String.format("authAdminKey=%s\n", authAdminKey));
        sb.append(String.format("useAdminAuth=%s\n", useAdminAuth));
        return sb.toString();
    }

    public Response delPtrManRecord(int lid, String ip) throws UnsupportedEncodingException {
        DnsClient1_0 dns = new DnsClient1_0("", rdnsAdminUrl, rdnsAdminUser, rdnsPasswd, "", accountId);
        Response resp = dns.delPtrRecordMan(buildDeviceUri(accountId, lid), LB_SERVICE_NAME, ip);
        
        return resp;
    }

    public Response delPtrPubRecord(int lid, String ip) throws RdnsException {
        String tokenStr;
        if (useAdminAuth) {
            tokenStr = getLbaasToken2();
        } else {
            tokenStr = getImpersanatedUserToken();
        }

        DnsClient1_0 dns = new DnsClient1_0(rdnsPublicUrl, tokenStr, accountId);
        return dns.delPtrRecordPub(buildDeviceUri(accountId, lid), LB_SERVICE_NAME, ip);
    }

    public String buildDeviceUri(int aid, int lid) {
        return String.format("%s/%d/loadbalancers/%d", lbaasBaseUrl, aid, lid);
    }

    public String relativeUri(int aid, int lid) {
        return String.format("/%d/loadbalancers/%d", aid, lid);
    }

    public String getUserName() throws RdnsException {
        String accountIdStr = Integer.valueOf(accountId).toString();
        try {
            IdentityAuthClientImpl client = new IdentityAuthClientImpl();
            String adminToken = client.getAuthToken();
            String username = client.getPrimaryUserForTenantId(adminToken, accountIdStr).getUsername();
            return username;
        } catch (URISyntaxException ex) {
            throw logAndThrowRdnsException(ex, accountId);
        } catch (IdentityFault ex) {
            throw logAndThrowRdnsException(ex, accountId);
        } catch (MalformedURLException ex) {
            throw logAndThrowRdnsException(ex, accountId);
        } catch (JAXBException ex) {
            throw logAndThrowRdnsException(ex, accountId);
        }
    }

    public String getImpersanatedUserToken() throws RdnsException {
        String accountIdStr = Integer.valueOf(accountId).toString();
        try {
            IdentityAuthClientImpl client = new IdentityAuthClientImpl();
            String adminToken = client.getAuthToken();
            User user = client.getPrimaryUserForTenantId(adminToken, accountIdStr);
            String username = user.getUsername();
            Access impersonation = client.impersonateUser(adminToken, username);
            Token token = impersonation.getToken();
            String tokenId = token.getId();
            return tokenId;
        } catch (URISyntaxException ex) {
            throw logAndThrowRdnsException(ex, accountId);
        } catch (IdentityFault ex) {
            throw logAndThrowRdnsException(ex, accountId);
        } catch (MalformedURLException ex) {
            throw logAndThrowRdnsException(ex, accountId);
        } catch (JAXBException ex) {
            throw logAndThrowRdnsException(ex, accountId);
        }
    }

    public String getLbaasToken2() throws RdnsException {
        try {
            return (new IdentityAuthClientImpl()).getAuthToken();
        } catch (URISyntaxException e) {
            throw logAndThrowRdnsException(e, accountId);
        } catch (IdentityFault identityFault) {
            throw logAndThrowRdnsException(identityFault, accountId);
        } catch (MalformedURLException e) {
            throw logAndThrowRdnsException(e, accountId);
        }
    }

    public static RdnsException logAndThrowRdnsException(Exception ex, int aid) {
        String stackTrace = Debug.getEST(ex);
        String fmt = "Error retriving user token for account %d:%s";
        String msg = String.format(fmt, aid, stackTrace);
        return new RdnsException(msg, ex);
    }

    public String getLbaasBaseUrl() {
        return lbaasBaseUrl;
    }

    public String getRdnsPublicUrl() {
        return rdnsPublicUrl;
    }

    public String getRdnsAdminUser() {
        return rdnsAdminUser;
    }

    public String getRdnsPasswd() {
        return rdnsPasswd;
    }

    public String getRdnsAdminUrl() {
        return rdnsAdminUrl;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getAuthAdminUrl() {
        return authAdminUrl;
    }

    public String getAuthPublicUrl() {
        return authPublicUrl;
    }

    public String getAuthAdminUser() {
        return authAdminUser;
    }

    public String getAuthAdminKey() {
        return authAdminKey;
    }

    public Boolean getUseAdminAuth() {
        return useAdminAuth;
    }
}
