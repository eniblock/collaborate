package xdev.keycloak.api.resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.jpa.entities.*;
import xdev.keycloak.api.representation.RoleRepresentation;
import xdev.keycloak.api.representation.UserRepresentation;
import xdev.keycloak.api.representation.UserSearchCriteria;
import xdev.keycloak.api.representation.UserSearchResponse;

public class UserResource extends AbstractAdminResource<AdminAuth> {

    private static final Logger LOG = Logger.getLogger(UserResource.class);

    @Context
    private KeycloakSession session;

    private final EntityManager em;

    public UserResource(RealmModel realm, EntityManager em) {
        super(realm);
        this.em = em;
    }

    @GET
    @NoCache
    @Path("users/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserRepresentation get(final @PathParam("id") String id) {
        UserEntity user = find(id);
        List<RoleEntity> roles = findRoles(user.getId());
        return toRepresentation(user, roles);
    }

    @GET
    @NoCache
    @Path("users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserSearchResponse getAll(@BeanParam UserSearchCriteria criteria) {
        List<UserEntity> users = findUsers(criteria);
        List<UserRepresentation> usersRep = new ArrayList<>();
        Long count = countUsers(criteria);
        if (CollectionUtils.isNotEmpty(users)) {
            users.forEach(userEntity -> {
                List<RoleEntity> roles = findRoles(userEntity.getId());
                usersRep.add(toRepresentation(userEntity, roles));
            });
        }

        return new UserSearchResponse(usersRep, count);
    }

    private UserRepresentation toRepresentation(UserEntity entity, List<RoleEntity> roles) {

        final UserRepresentation rep = new UserRepresentation();

        rep.setId(UUID.fromString(entity.getId()));

        rep.setFirstName(entity.getFirstName());
        rep.setLastName(entity.getLastName());
        rep.setEmail(entity.getEmail());
        rep.setCreatedTimestamp(entity.getCreatedTimestamp());
        rep.setUsername(entity.getUsername());
        rep.setEnabled(entity.isEnabled());

        if (CollectionUtils.isNotEmpty(roles)) {
            Set<RoleRepresentation> rolesRep = new HashSet<>();
            roles.forEach(roleEntity -> {
                RoleRepresentation roleRep = new RoleRepresentation();
                roleRep.setId(UUID.fromString(roleEntity.getId()));
                roleRep.setName(roleEntity.getName());
                roleRep.setDescription(roleEntity.getDescription());
                rolesRep.add(roleRep);
            });

            rep.setRoles(rolesRep);
        }

        return rep;
    }

    private UserEntity find(String id) {
        try {
            UserEntity user = em.createQuery("SELECT u FROM UserEntity u WHERE u.realmId = :realmId AND u.id = :id", UserEntity.class)
                    .setParameter("realmId", realm.getId())
                    .setParameter("id", id)
                    .getSingleResult();
            return user;
        } catch (NoResultException e) {
            throw new NotFoundException("User not found");
        }
    }

    private List<UserEntity> findUsers(UserSearchCriteria criteria) {

        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<UserEntity> criteriaQuery = builder.createQuery(UserEntity.class);
            // Main entity is UserEntity
            Root<UserEntity> userRoot = criteriaQuery.from(UserEntity.class);
            List<Predicate> predicates = getPredicates(userRoot, criteriaQuery, builder, criteria);

            // We finalize the criteriaQuery specification by linking all information together
            criteriaQuery.select(userRoot).distinct(true).where(predicates.toArray(new Predicate[0]));

            // TODO Add sorting

            // Query creation, pagination and execution
            TypedQuery<UserEntity> query = em.createQuery(criteriaQuery);
            if (criteria.isPaged()) {
                query.setFirstResult(criteria.getOffset().intValue()).setMaxResults(criteria.getPageSize());
            }
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    private Long countUsers(UserSearchCriteria criteria) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);

        // Main entity is UserEntity
        Root<UserEntity> userRoot = criteriaQuery.from(UserEntity.class);
        List<Predicate> predicates = getPredicates(userRoot, criteriaQuery, builder, criteria);

        // We finalize the criteriaQuery specification by linking all information together
        criteriaQuery.select(builder.countDistinct(userRoot)).where(predicates.toArray(new Predicate[0]));

        // Query creation and execution
        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    private List<Predicate> getPredicates(Root<UserEntity> userRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder, UserSearchCriteria criteria) {

        List<Predicate> predicates = new ArrayList<>();
        // We MUST filter on the realm to be sure we don't retrieve other users from other realms
        predicates.add(builder.equal(userRoot.get("realmId"), realm.getId()));

        // Filtering on email (lowercase to ignore case)
        if (StringUtils.isNotBlank(criteria.getEmail())) {
            predicates.add(builder.equal(builder.lower(userRoot.get("email")), criteria.getEmail().toLowerCase()));
        }

        // Filter on "search" as keycloak do in its default APIs
        if (StringUtils.isNotBlank(criteria.getSearch())) {
            String searchPattern = "%" + criteria.getSearch().toLowerCase() + "%";
            predicates.add(builder.or(
                    builder.like(builder.lower(userRoot.get("firstName")), searchPattern),
                    builder.like(builder.lower(userRoot.get("lastName")), searchPattern),
                    builder.like(builder.lower(userRoot.get("email")), searchPattern))
            );
        }

        // Filter on role names
        if (CollectionUtils.isNotEmpty(criteria.getRoles()) || criteria.getAccountId() != null) {
            // We must join manually UserRoleMappingEntity and RoleEntity. It creates a cross join unfortunately
            Root<UserRoleMappingEntity> mappingRoot = criteriaQuery.from(UserRoleMappingEntity.class);
            Root<RoleEntity> roleRoot = criteriaQuery.from(RoleEntity.class);
            predicates.add(builder.equal(mappingRoot.get("user").get("id"), userRoot.get("id")));
            predicates.add(builder.equal(mappingRoot.get("roleId"), roleRoot.get("id")));
            if (CollectionUtils.isNotEmpty(criteria.getRoles())) {
                predicates.add(roleRoot.get("name").in(criteria.getRoles()));
            }
            if (criteria.getAccountId() != null) {
                predicates.add(builder.like(roleRoot.get("name"), "%\\_" + criteria.getAccountId(), '\\'));
            }
        }

        return predicates;
    }

    private List<RoleEntity> findRoles(String userId) {

        try {
            List<RoleEntity> result = em.createQuery("SELECT r FROM RoleEntity r, UserRoleMappingEntity urm, RoleAttributeEntity attr WHERE r.id = urm.roleId AND urm.user.realmId = :realmId AND urm.user.id = :userId " +
                    "AND r.id = attr.role.id AND attr.name = :attributeName AND attr.value = :attributeValue", RoleEntity.class)
                    .setParameter("realmId", realm.getId())
                    .setParameter("userId", userId)
                    .setParameter("attributeName", System.getenv("ROLE_ATTRIBUTE_KEY"))
                    .setParameter("attributeValue", System.getenv("ROLE_ATTRIBUTE_VALUE"))
                    .getResultList();

            return result;
        } catch (NoResultException e) {
            return null;
        }

    }
}
