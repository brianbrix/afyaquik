package com.afyaquik.users.security;

import com.afyaquik.users.dto.security.ApiPermissionDto;
import com.afyaquik.users.service.ApiPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class ApiAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private static final Set<String> ADMIN = Set.of("ADMIN", "SUPERADMIN");
    private static final Set<String> STAFF = Set.of("ADMIN", "SUPERADMIN", "RECEPTIONIST", "DOCTOR", "NURSE", "PHARMACIST", "CASHIER", "LAB_TECHNICIAN", "REPORTS");
    private static final Set<String> CARE = Set.of("ADMIN", "SUPERADMIN", "RECEPTIONIST", "DOCTOR", "NURSE", "PHARMACIST");
    private static final Set<String> CLINICAL = Set.of("ADMIN", "SUPERADMIN", "DOCTOR", "NURSE");
    private static final Set<String> DOCTOR = Set.of("DOCTOR");
    private static final Set<String> PHARMACY = Set.of("ADMIN", "SUPERADMIN", "PHARMACIST");
    private static final Set<String> PRESCRIBERS = Set.of("DOCTOR");
    private static final Set<String> BILLING = Set.of("ADMIN", "SUPERADMIN", "CASHIER", "RECEPTIONIST");
    private static final Set<String> RECEPTION = Set.of("ADMIN", "SUPERADMIN", "RECEPTIONIST");
    private static final List<Rule> RULES = List.of(
            new Rule("/api/search", STAFF, STAFF),
            new Rule("/api/delete", ADMIN, ADMIN),
            new Rule("/api/notifications/**", STAFF, STAFF),
            new Rule("/api/users/byrole", CARE, ADMIN),
            new Rule("/api/users/**", ADMIN, ADMIN),
            new Rule("/api/roles/byName/*", STAFF, Set.of("SUPERADMIN")),
            new Rule("/api/roles/**", ADMIN, Set.of("SUPERADMIN")),
            new Rule("/api/admin/permissions/**", Set.of("SUPERADMIN"), Set.of("SUPERADMIN")),
            new Rule("/api/stations/**", CARE, ADMIN),
            new Rule("/api/departments/**", CARE, ADMIN),
            new Rule("/api/settings/**", ADMIN, ADMIN),
            new Rule("/api/password-reset/**", ADMIN, ADMIN),
            new Rule("/api/patients/**", CARE, RECEPTION),
            new Rule("/api/patients/*/delete", ADMIN, ADMIN),
            new Rule("/api/patient/visits/**", CARE, CARE),
            new Rule("/api/inpatient/**", Set.of("ADMIN", "SUPERADMIN", "RECEPTIONIST", "DOCTOR", "NURSE"), CLINICAL),
            new Rule("/api/inpatient/beds/**", CLINICAL, ADMIN),
            new Rule("/api/patient-communications/**", Set.of("ADMIN", "SUPERADMIN", "RECEPTIONIST", "DOCTOR", "NURSE"), Set.of("ADMIN", "SUPERADMIN", "RECEPTIONIST", "DOCTOR", "NURSE")),
            new Rule("/api/patient/triage/items/**", CLINICAL, ADMIN),
            new Rule("/api/patient/triage/**", CLINICAL, CLINICAL),
            new Rule("/api/appointments/**", Set.of("ADMIN", "SUPERADMIN", "RECEPTIONIST", "DOCTOR", "NURSE"), RECEPTION),
            new Rule("/api/observations/**", DOCTOR, DOCTOR),
            new Rule("/api/observation/items/**", CLINICAL, ADMIN),
            new Rule("/api/plan/items/**", Set.of("ADMIN", "SUPERADMIN", "DOCTOR", "PHARMACIST"), ADMIN),
            new Rule("/api/plan/**", Set.of("DOCTOR", "PHARMACIST"), DOCTOR),
            new Rule("/api/drugs/inventory/**", Set.of("ADMIN", "SUPERADMIN", "PHARMACIST", "DOCTOR"), PHARMACY),
            new Rule("/api/drugs/**", Set.of("ADMIN", "SUPERADMIN", "PHARMACIST", "DOCTOR"), PHARMACY),
            new Rule("/api/patient-drugs/*/dispense", PHARMACY, PHARMACY),
            new Rule("/api/patient-drugs/**", Set.of("ADMIN", "SUPERADMIN", "PHARMACIST", "DOCTOR"), PRESCRIBERS),
            new Rule("/api/billing/items/**", BILLING, ADMIN),
            new Rule("/api/billing/**", BILLING, BILLING),
            new Rule("/api/currencies/**", BILLING, ADMIN),
            new Rule("/api/reports/**", Set.of("REPORTS", "ADMIN", "SUPERADMIN", "DOCTOR", "NURSE"), Set.of())
    );

    private final ApiPermissionService permissionService;
    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        Authentication current = authentication.get();
        if (current == null || !current.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }
        String path = context.getRequest().getRequestURI().substring(context.getRequest().getContextPath().length());
        boolean readOnly = Set.of("GET", "HEAD").contains(context.getRequest().getMethod());
        var comparator = matcher.getPatternComparator(path);
        Rule baseline = RULES.stream()
                .filter(rule -> matcher.match(rule.pattern(), path))
                .min((first, second) -> comparator.compare(first.pattern(), second.pattern()))
                .orElse(null);
        if (baseline == null || !hasRole(current, readOnly ? baseline.readRoles() : baseline.writeRoles())) {
            return new AuthorizationDecision(false);
        }
        ApiPermissionDto configured = permissionService.getEnabledPermissions().stream()
                .filter(permission -> permission.getUrlPattern() != null && matcher.match(permission.getUrlPattern(), path))
                .min((first, second) -> comparator.compare(first.getUrlPattern(), second.getUrlPattern()))
                .orElse(null);
        return new AuthorizationDecision(configured == null ||
                (configured.getRoleNames() != null && hasRole(current, configured.getRoleNames())));
    }

    private boolean hasRole(Authentication authentication, Set<String> roles) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> roles.stream().anyMatch(role -> authority.getAuthority().equals("ROLE_" + role)));
    }

    private record Rule(String pattern, Set<String> readRoles, Set<String> writeRoles) {}
}