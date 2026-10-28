# Project Plan

A call blocking app targeting specific country codes, with a selectable country code list, and support for specific prefix or suffix call blocking for local calls.

## Project Brief

# Project Brief: Smart Call Blocker

## Features
1. **Country Code Call Blocking**: Select and manage target country codes to automatically reject incoming international calls originating from specified countries.
2. **Local Prefix & Suffix Pattern Matching**: Define custom blocking rules for local numbers based on specific starting prefixes or ending suffixes.
3. **Real-Time Call Screening Service**: Leverage Android's native call screening framework to evaluate incoming calls in real-time and silently reject blocked numbers.
4. **Blocked Call History & Activity Log**: View a history log of recent blocked incoming calls, including timestamps, caller phone numbers, and triggered blocking rules.

## High-Level Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Navigation & Adaptive Strategy**: Jetpack Navigation 3 (state-driven) and Compose Material Adaptive (`androidx.compose.material3.adaptive`)
- **Asynchronous & Reactive Programming**: Kotlin Coroutines & Flow / StateFlow
- **System Integration**: Android Telecom Framework via `CallScreeningService` API

## Implementation Steps
**Total Duration:** 28h 35m 59s

### Task_1_DataAndCallScreeningEngine: Implement data models, persistence repository for blocking rules and call log, and CallScreeningService to filter incoming calls based on country code, prefix, and suffix rules.
- **Status:** COMPLETED
- **Updates:** Completed Room Database models (CountryCodeRule, PatternRule, BlockedCallLog), DAOs, Repository, CountryCodeHelper with 50+ world country codes, CallScreeningEngine pattern matcher, SmartCallScreeningService in AndroidManifest, and verified unit tests.
- **Acceptance Criteria:**
  - Data models and persistence created for Country Code rules, Prefix/Suffix rules, and Blocked Call History
  - CallScreeningService evaluates incoming numbers against active rules, logs blocked calls, and disallows blocked calls
  - AndroidManifest.xml configured with CallScreeningService and telecom permissions
- **Duration:** 13m 41s

### Task_2_RulesManagementUI: Build Jetpack Compose UI screens to manage country code blocking rules and local prefix/suffix pattern matching rules.
- **Status:** COMPLETED
- **Updates:** Implemented CountryCodeRulesTab with Searchable CountryPickerDialog, PatternRulesTab with filter chips and AddPatternRuleDialog, RulesViewModel with reactive state, and verified UI with unit tests and previews.
- **Acceptance Criteria:**
  - UI allows adding, toggling, and deleting country code blocking rules
  - UI allows adding, toggling, and deleting prefix and suffix pattern rules
  - StateFlow reactive updates synced with local repository
- **Duration:** 5m 49s

### Task_3_CallHistoryAndNavigation: Build Blocked Call History UI screen and set up Navigation 3 and Adaptive layout to connect rules management and history screens.
- **Status:** COMPLETED
- **Updates:** Implemented CallHistoryScreen with relative formatting and clear confirmation dialog, NavigationBar/NavigationRail adaptive navigation, CallScreeningStatusBanner with RoleManager integration, and wired ViewModel state in MainActivity. Verified with unit tests and assembleDebug.
- **Acceptance Criteria:**
  - Call history screen displays timestamped blocked call records with triggered rule info
  - Navigation 3 connects Rules Management and Call History screens seamlessly
  - Service status and screening permission request triggers integrated into UI
- **Duration:** 28h 5m 6s

### Task_4_RunAndVerify: Run and verify the complete application. Instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** Successfully verified app build and test suite. All 25 unit tests passed with 0 failures, assembleDebug build succeeded, and code functionality verified across CallScreeningEngine, RulesViewModel, CallHistoryViewModel, CountryCodeHelper, and Compose UI screens.
- **Acceptance Criteria:**
  - make sure all existing tests pass
  - build pass
  - app does not crash
  - All call blocking features, history log, and navigation function correctly without errors
- **Duration:** 11m 23s

