package io.factorialsystems.keycloakremote.provider;

import io.factorialsystems.keycloakremote.mapper.UserMapper;
import io.factorialsystems.keycloakremote.model.User;
import lombok.RequiredArgsConstructor;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.UserCredentialStore;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.user.UserLookupProvider;

@RequiredArgsConstructor
public class RemoteUserStorageProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {

    private final KeycloakSession keycloakSession;
    private final ComponentModel componentModel;
    private final UserMapper mapper;

    @Override
    public boolean supportsCredentialType(String s) {
        return PasswordCredentialModel.TYPE.equals(s);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String s) {
        if (!supportsCredentialType(s)) return false;

        return !getCredentialStore().getStoredCredentialsByType(realmModel, userModel, s).isEmpty();
    }

    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput credentialInput) {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public UserModel getUserById(String s, RealmModel realmModel) {
        return null;
    }

    @Override
    public UserModel getUserByUsername(String s, RealmModel realmModel) {
        UserModel userModel = null;

        User user = mapper.selectUserByEmail(s);

        if (user != null) {
            userModel = createUserModel(s, realmModel);
        }

        return userModel;

    }

    @Override
    public UserModel getUserByEmail(String s, RealmModel realmModel) {
        return null;
    }

    private UserCredentialStore getCredentialStore() {
        return keycloakSession.userCredentialManager();
    }

    private UserModel createUserModel(String userName, RealmModel realm) {
        return new AbstractUserAdapter(keycloakSession, realm, componentModel) {
            @Override
            public String getUsername() {
                return userName;
            }
        };
    }
}
