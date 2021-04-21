package collaborate.api.comparator;

import collaborate.api.domain.Scope;

import java.util.Comparator;

/**
 * Sorting management for scopes.
 */
public class ScopeComparator {

    public static class StatusSorter implements Comparator<Scope> {
        @Override
        public int compare(Scope o1, Scope o2) {
            return o1.getStatus().compareTo(o2.getStatus());
        }
    }

    public static  class OrganisationSorter implements Comparator<Scope> {
        @Override
        public int compare(Scope o1, Scope o2) {
            return o1.getOrganizationId().compareTo(o2.getOrganizationId());
        }
    }

    public static class NameSorter implements Comparator<Scope> {
        @Override
        public int compare(Scope o1, Scope o2) {
            return o1.getScope().compareTo(o2.getScope());
        }
    }
}

