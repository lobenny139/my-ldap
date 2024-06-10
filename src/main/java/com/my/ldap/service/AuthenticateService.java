package com.my.ldap.service;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

@Getter
@Setter
@Component
@Service
public class AuthenticateService implements IAuthenticateService {

    private static Logger logger = LoggerFactory.getLogger(AuthenticateService.class);

    private static final String ATTRIBUTE_FOR_USER = "sAMAccountName";

    @Value("${ldapAuthDomain}")
    private String domain;

    @Value("${ldapServerUrl}")
    private String adServer;

    @Value("${ldapAuthSearchBase}")
    private String searchBase;

    public boolean authenticateUser(String userId, String password) {

//        String domain = "taikoomotors.com.tw";
//        String adServer = "192.168.1.242";
//        String searchBase = "dc=taikoomotors,dc=com,dc=tw";

        logger.info("LDAP Info:"+ getDomain() + "," + getAdServer() + "," + getSearchBase());

        String searchFilter = "(&(objectClass=user)(" + ATTRIBUTE_FOR_USER + "=" + userId + "))";

        // Create the search controls
        SearchControls searchCtls = new SearchControls();

        String[] returnedAtts = { "displayName", "description", "mail", "department" };

        searchCtls.setReturningAttributes(returnedAtts);

        // Specify the search scope
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        Hashtable<String, String> environment = new Hashtable<String, String>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL, "ldap://" + getAdServer() + ":389");
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.SECURITY_PRINCIPAL, userId + "@" + getDomain());
        environment.put(Context.SECURITY_CREDENTIALS, password);
        LdapContext ldapContext = null;
        Attributes attributes = null;
        try {
            ldapContext = new InitialLdapContext(environment, null);
            // Search for objects in the GC using the filter
            @SuppressWarnings("rawtypes")
            NamingEnumeration answer = ldapContext.search(getSearchBase(), searchFilter, searchCtls);
            while (answer.hasMoreElements()) {
                SearchResult searchResult = (SearchResult) answer.next();
                attributes = searchResult.getAttributes();
                logger.info("Found LDAP user:" + attributes.toString());
                if (attributes != null && attributes.toString().trim().length() > 0) {
                    return true;
                }
            }
        }  catch (javax.naming.AuthenticationException e) {
            //密碼錯誤
            logger.error( "AuthError" );
        }
        catch (javax.naming.CommunicationException e) {
            //連線錯誤
            logger.error( "ConnectionError" );
        }catch(Exception e){
            logger.error( "ConnectionError" );
        }
        return false;
    }

}
