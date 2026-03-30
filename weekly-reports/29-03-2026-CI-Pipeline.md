# CI/CD Pipeline Implementation - 29/03/2026

## 📋 Table of Contents
1. [CI/CD Pipeline Creation](#1-cicd-pipeline-creation)
2. [Additional Step: PR Comment Bot](#2-additional-step-pr-comment-bot)

---

## 1. CI/CD Pipeline Creation

*(Documentation pending)*

---

## 2. Additional Step: PR Comment Bot

### Overview
A new step was implemented in the CI/CD pipeline that automates the publication of status reports in Pull Requests through a GitHub bot.

### Functionality
The `🤖 Bot - Comment PR Result` step performs the following operations:

#### a) Previous Comments Management
- Retrieves the list of existing comments in the PR
- Searches for and deletes previous bot comments containing "🤖 CI Report"
- Prevents accumulation of duplicate comments

#### b) Report Generation
The bot creates a markdown-formatted comment that includes:
- **Status Table**: Displays the result (✅/❌) of:
  - 🔨 Build
  - 🧪 Unit Tests
  - 🔍 Sonar Analysis (with direct link to analysis)
- **Context Information**:
  - Commit SHA
  - Working branch
  - User who executed the pipeline

#### c) Conditional Execution
- Only executes when the event is `pull_request`
- Uses `actions/github-script@v7` to interact with GitHub API
- Requires read/write permissions on issues

### Required Configuration
For proper operation, the following secrets must be configured in the repository:
- `GITHUB_TOKEN`: Authentication token (included by default in GitHub Actions)
- `SONAR_TOKEN`: Token for SonarCloud authentication (generate from SonarCloud account settings)

### SonarCloud Project Configuration
The project `csdt-eci_HospitalManagementRefactor` must be created and linked in SonarCloud:
1. Project exists at: https://sonarcloud.io/organizations/csdt-eci
2. GitHub repository is linked for automatic PR analysis
3. Main branch: `master`

### GitHub Secrets Setup
Add the following secret to your GitHub repository (Settings → Secrets and variables → Actions):
- **Name**: `SONAR_TOKEN`
- **Value**: Your SonarCloud authentication token

### Benefits
- ✅ Immediate feedback to developers in the PR
- ✅ Prevents noise from accumulated comments
- ✅ Direct access to code analysis reports
- ✅ Centralized pipeline status information in PR context

### Technical Implementation
```yaml
- name: Analyze with SonarCloud
  uses: SonarSource/sonarqube-scan-action@v5.0.0
  env:
    GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
    SONAR_HOST_URL: https://sonarcloud.io
  with:
    args: >
      -Dsonar.projectKey=csdt-eci_HospitalManagementRefactor
      -Dsonar.organization=csdt-eci
      ${{ github.event_name == 'pull_request' &&
        format('-Dsonar.pullrequest.key={0} -Dsonar.pullrequest.branch={1} -Dsonar.pullrequest.base={2}',
          github.event.pull_request.number,
          github.head_ref,
          github.base_ref)
        || format('-Dsonar.branch.name={0}', github.ref_name) }}
```

### Root Cause Analysis & Resolution

**Problem**: SonarCloud failed with "Could not find a default branch" error during PR analysis.

**Root Cause**: When analyzing a pull request, `github.ref_name` returns an internal PR reference (e.g., `8/merge`) instead of the actual branch name. SonarCloud couldn't map this to any known branch.

**Solution**: Implement conditional parameter passing:
- **For Pull Requests**: Use `sonar.pullrequest.*` properties with correct metadata
  - `pullrequest.key`: PR number
  - `pullrequest.branch`: Source branch (via `github.head_ref`)
  - `pullrequest.base`: Target branch (via `github.base_ref`)
- **For Push Events**: Use `sonar.branch.name` with actual branch reference

This ensures SonarCloud correctly identifies the context and associates the analysis with the proper branch configuration.

### Troubleshooting (Resolved)
✅ **"Could not find a default branch" error** - Fixed by using correct PR parameters

---

**Implementation Date**: March 29, 2026  
**Status**: ✅ Completed  
**Last Updated**: March 30, 2026 - Fixed PR analysis with proper pullrequest parameters


