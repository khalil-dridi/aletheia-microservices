package tn.platform.user.user.repository;

import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;
import tn.platform.user.user.entity.Role;
import tn.platform.user.user.entity.User;

public final class UserSpecifications {

    private UserSpecifications() {
    }

    public static Specification<User> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    public static Specification<User> hasRole(Role role) {
        return (root, query, cb) ->
                role == null ? cb.conjunction() : cb.equal(root.get("role"), role);
    }

    public static Specification<User> matchesSearch(String q) {
        if (q == null || q.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        String pattern = "%" + q.trim().toLowerCase() + "%";
        return (root, query, cb) -> {
            Expression<String> prenomCoalesced = cb.coalesce(root.get("prenom"), "");
            return cb.or(
                    cb.like(cb.lower(root.get("nom")), pattern),
                    cb.like(cb.lower(prenomCoalesced), pattern),
                    cb.like(cb.lower(root.get("email")), pattern)
            );
        };
    }
}
