package collaborate.login_event_listener;

import org.keycloak.models.ClientModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestUserModel implements UserModel {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> roles;

    public TestUserModel(String id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = new HashSet<>();
    }

    public TestUserModel(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = new HashSet<>();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public void setUsername(String s) {

    }

    @Override
    public Long getCreatedTimestamp() {
        return null;
    }

    @Override
    public void setCreatedTimestamp(Long aLong) {

    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEnabled(boolean b) {

    }

    @Override
    public void setSingleAttribute(String s, String s1) {

    }

    @Override
    public void setAttribute(String s, List<String> list) {

    }

    @Override
    public void removeAttribute(String s) {

    }

    @Override
    public String getFirstAttribute(String s) {
        return null;
    }

    @Override
    public List<String> getAttribute(String s) {
        return null;
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        return null;
    }

    @Override
    public Set<String> getRequiredActions() {
        return null;
    }

    @Override
    public void addRequiredAction(String s) {

    }

    @Override
    public void removeRequiredAction(String s) {

    }

    @Override
    public void addRequiredAction(RequiredAction requiredAction) {

    }

    @Override
    public void removeRequiredAction(RequiredAction requiredAction) {

    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public void setFirstName(String s) {
        this.firstName = s;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public void setLastName(String s) {
        this.lastName = s;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public void setEmail(String s) {
        this.email = s;
    }

    @Override
    public boolean isEmailVerified() {
        return false;
    }

    @Override
    public void setEmailVerified(boolean b) {

    }

    @Override
    public Set<GroupModel> getGroups() {
        return null;
    }

    @Override
    public void joinGroup(GroupModel groupModel) {

    }

    @Override
    public void leaveGroup(GroupModel groupModel) {

    }

    @Override
    public boolean isMemberOf(GroupModel groupModel) {
        return false;
    }

    @Override
    public String getFederationLink() {
        return null;
    }

    @Override
    public void setFederationLink(String s) {

    }

    @Override
    public String getServiceAccountClientLink() {
        return null;
    }

    @Override
    public void setServiceAccountClientLink(String s) {

    }

    @Override
    public Set<RoleModel> getRealmRoleMappings() {
        return null;
    }

    @Override
    public Set<RoleModel> getClientRoleMappings(ClientModel clientModel) {
        return null;
    }

    @Override
    public boolean hasRole(RoleModel roleModel) {
        return this.roles.contains(roleModel.getName());
    }

    public void addRole(String role) {
        this.roles.add(role);
    }

    @Override
    public void grantRole(RoleModel roleModel) {

    }

    @Override
    public Set<RoleModel> getRoleMappings() {
        return null;
    }

    @Override
    public void deleteRoleMapping(RoleModel roleModel) {

    }
}
