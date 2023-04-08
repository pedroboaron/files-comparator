package com.boti.filescomparator.service;

import com.boti.filescomparator.domain.IAccount;
import com.boti.filescomparator.domain.IAuthenticationResult;
import com.boti.filescomparator.domain.PublicClientApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CloudService {

    public String login() throws IOException {
    setUpSampleData();

    PublicClientApplication pca = PublicClientApplication.builder(clientId)
            .authority(authority)
            .build();

    Set<IAccount> accountsInCache = pca.getAccounts().join();
    IAccount account = getAccountByUsername(accountsInCache, username);

    //Attempt to acquire token when user's account is not in the application's token cache
    IAuthenticationResult result = acquireTokenIntegratedWindowsAuth(pca, scope, account, username);
        System.out.println("Account username: " + result.account().username());
        System.out.println("Access token:     " + result.accessToken());
        System.out.println("Id token:         " + result.idToken());
        System.out.println();

    //Get list of accounts from the application's token cache, and search them for the configured username
    //getAccounts() will be empty on this first call, as accounts are added to the cache when acquiring a token
    accountsInCache = pca.getAccounts().join();
    account = getAccountByUsername(accountsInCache, username);

    //Attempt to acquire token again, now that the user's account and a token are in the application's token cache
    result = acquireTokenIntegratedWindowsAuth(pca, scope, account, username);
        System.out.println("Account username: " + result.account().username());
        System.out.println("Access token:     " + result.accessToken());
        System.out.println("Id token:         " + result.idToken());

        return result.toString();
}

    private static IAuthenticationResult acquireTokenIntegratedWindowsAuth( PublicClientApplication pca,
                                                                            Set<String> scope,
                                                                            IAccount account,
                                                                            String username) throws Exception {

        IAuthenticationResult result;
        try {
            SilentParameters silentParameters =
                    SilentParameters
                            .builder(scope)
                            .account(account)
                            .build();
            // Try to acquire token silently. This will fail on the first acquireTokenIntegratedWindowsAuth() call
            // because the token cache does not have any data for the user you are trying to acquire a token for
            result = pca.acquireTokenSilently(silentParameters).join();
            System.out.println("==acquireTokenSilently call succeeded");
        } catch (Exception ex) {
            if (ex.getCause() instanceof MsalException) {
                System.out.println("==acquireTokenSilently call failed: " + ex.getCause());
                IntegratedWindowsAuthenticationParameters parameters =
                        IntegratedWindowsAuthenticationParameters
                                .builder(scope, username)
                                .build();

                // Try to acquire a token using Integrated Windows Authentication (IWA). You will need to generate a Kerberos ticket.
                // If successful, you should see the token and account information printed out to console
                result = pca.acquireToken(parameters).join();
                System.out.println("==Integrated Windows Authentication flow succeeded");
            } else {
                // Handle other exceptions accordingly
                throw ex;
            }
        }
        return result;
    }

    /**
     * Helper function to return an account from a given set of accounts based on the given username,
     * or return null if no accounts in the set match
     */
    private static IAccount getAccountByUsername(Set<IAccount> accounts, String username) {
        if (accounts.isEmpty()) {
            System.out.println("==No accounts in cache");
        } else {
            System.out.println("==Accounts in cache: " + accounts.size());
            for (IAccount account : accounts) {
                if (account.username().equals(username)) {
                    return account;
                }
            }
        }
        return null;
    }

    /**
     * Helper function unique to this sample setting. In a real application these wouldn't be so hardcoded, for example
     * values such as username would come from the user, and different users may require different scopes
     */
    private static void setUpSampleData() throws IOException {
        // Load properties file and set properties used throughout the sample
        Properties properties = new Properties();
        properties.load(UsernamePasswordFlow.class.getResourceAsStream("application.properties"));
        authority = properties.getProperty("AUTHORITY");
        scope = Collections.singleton(properties.getProperty("SCOPE"));
        clientId = properties.getProperty("CLIENT_ID");
        username = properties.getProperty("USER_NAME");
    }
}
