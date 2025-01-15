# Changelog

---

All notable changes to this project will be documented in this file.

According to [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) , the `Unreleased` section serves the following purposes:

-   People can see what changes they might expect in upcoming releases.
-   At release time, you can move the `Unreleased` section changes into a new release version section.

## Types of changes

---

-   `Added` for new features.
-   `Changed` for changes in existing functionality.
-   `Removed` for now removed features.
-   `Fixed` for any bug fixes.
-   `Security` in case of vulnerabilities.
-   `Deprecated` for soon-to-be removed features.

## Unreleased

-   [#129](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/129) - PIDMR-206 Add validator Property to Provider Table.


## 2.3.0 - 2024-12-19

---
### Added

-   [#86](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/86) - PIDMR-175 Implement GET to retrieve change role request by id as an admin role.
-   [#99](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/99) - PIDMR-194 Decode pid URL Parameter to Handle Ampersand (&) Character.

### Changed

-   [#108](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/108) - PIDMR-198 Moving to new PIDMR backend.


## 2.2.0 - 2024-06-10

---

### Added

-   [#59](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/59)   - PIDMR-146 Add "endpoint" field to Provider entity with mode-specific endpoints.
-   [#60](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/60)   - PIDMR-154 Add Property to Provider Entity to Track DOI Reliance.
-   [#61](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/61)   - PIDMR-160 Implement Communication with Keycloak Admin for User Role Assignment.
-   [#62](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/62)   - PIDMR-159 Create an Endpoint to Promote User to Any Role.
-   [#63](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/63)   - PIDMR-161 Implement Admin Endpoint for Promotion Request Approval.
-   [#64](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/64)   - PIDMR-157 Create New Authentication Flow in Keycloak.
-   [#65](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/65)   - PIDMR-164 Create a new endpoint to get all the promotion requests submitted through our API.
-   [#66](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/66)   - PIDMR-158 Implement API Endpoint for Assigning User Roles.
-   [#70](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/70)   - PIDMR-169 EMAIL after registration.
-   [#81](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/56)   - PIDMR-176 Create Endpoint to Retrieve All Role Change Requests by a User.
-   [#82](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/82)   - PIDMR-177 Remove Assigned Role on Rejected Role Change Request by Admin.
-   [#106](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/106) - PIDMR-196 More info to emails send to admins.

## 2.1.0 - 2024-04-29

---

### Changed

-   [#56](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/56) - PIDMR-152 Refactor Batch Resolution Process.


## 2.0.0 - 2024-03-07

---

### Added

-   [#45](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/45) - PIDMR-117 Batch request for resolution.
-   [#46](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/46) - PIDMR-118 Batch request for identify.
-   [#47](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/47) - PIDMR-98 Connect pidmr with GRNET postgresql.
-   [#48](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/47) - PIDMR-140 Trim PID Before Resolution.

### Changed

-   [#46](https://github.com/FC4E-WP5/fc4eosc-PIDMR-api/pull/46) - PIDMR-136 Pending pid entries seem to be used in identification.
