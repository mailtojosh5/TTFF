# Maven & TestNG Command Reference

Quick reference for common Maven and TestNG commands for running and managing the Selenium Java project.

## 🚀 Basic Maven Commands

### Installation & Build
```bash
# Clean previous build
mvn clean

# Download dependencies
mvn dependency:resolve

# Compile source code
mvn compile

# Compile test code
mvn test-compile

# Complete build
mvn clean install

# Build with skipping tests
mvn clean install -DskipTests

# Force update dependencies
mvn clean install -U
```

---

## 🧪 Test Execution

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Class
```bash
mvn clean test -Dtest=TestRunner
```

### Run Specific Test Method
```bash
mvn clean test -Dtest=TestRunner#executeTestFlow
```

### Run Multiple Test Classes
```bash
mvn clean test -Dtest=TestRunner,BaseTest
```

### Run Tests by Pattern
```bash
mvn clean test -Dtest=*Runner*
mvn clean test -Dtest=Test*
```

---

## 🎯 Test Groups

### Run Smoke Tests Only
```bash
mvn clean test -Dgroups=smoke
```

### Run Regression Tests Only
```bash
mvn clean test -Dgroups=regression
```

### Run Multiple Groups
```bash
mvn clean test -Dgroups=smoke,regression
```

### Run Excluding a Group
```bash
mvn clean test -Dexcludedgroups=smoke
```

---

## 🌐 Browser Selection

### Run with Chrome (Default)
```bash
mvn clean test -Dbrowser=chrome
```

### Run with Firefox
```bash
mvn clean test -Dbrowser=firefox
```

### Run with Edge
```bash
mvn clean test -Dbrowser=edge
```

### Run with Headless Mode
```bash
mvn clean test -Dheadless=true
```

---

## 📊 Reports & Allure

### Generate Allure Report
```bash
mvn allure:report
```

### View Allure Report (Live)
```bash
mvn allure:serve
```

### Generate HTML Maven Report
```bash
mvn site
```

### Clean Allure Results
```bash
mvn clean
```

---

## ⚙️ Build & Plugin Commands

### Skip Tests During Build
```bash
mvn clean install -DskipTests
```

### Run with Maven 'package' Phase
```bash
mvn clean package
```

### Run with JUnit Tests Only
```bash
mvn clean test -Dtest=TestRunner
```

### Run Surefire Plugin Directly
```bash
mvn surefire:test
```

### Run Failsafe (Integration Tests)
```bash
mvn verify
```

---

## 🔍 Debugging & Logging

### Run with Debug Logging
```bash
mvn clean test -X
```

### Run with Error Only Logging
```bash
mvn clean test -q
```

### Run with Batch Mode (No Prompts)
```bash
mvn clean test -B
```

### Show Full Error Stack
```bash
mvn clean test -e
```

### Run in Offline Mode
```bash
mvn clean test -o
```

---

## 📦 Dependency Management

### View Dependency Tree
```bash
mvn dependency:tree
```

### View Dependency Tree for Specific Dependency
```bash
mvn dependency:tree -Dinclude=org.seleniumhq.selenium
```

### Check for Dependency Updates
```bash
mvn versions:display-dependency-updates
```

### Show Dependency Outdated
```bash
mvn versions:display-outdated-dependencies
```

### Download All Sources
```bash
mvn dependency:sources
      
### Analyze Dependencies
```bash
mvn dependency:analyze
```

---

## 🔄 Parallel Execution

### Run Tests in Parallel (Methods)
```bash
mvn clean test -DthreadCount=3
```

### Run Tests Parallel by Class
```bash
mvn clean test -Dgroups=smoke -DthreadCount=4
```

### View Parallel Execution Info
```bash
mvn clean test -X | grep -i parallel
```

---

## 🐛 Troubleshooting

### Force Update Maven Cache
```bash
mvn clean install -U --no-offline
```

### Rebuild Entire Project
```bash
mvn clean build
```

### Regenerate Sources
```bash
mvn clean process-sources compile
```

### View All Plugins
```bash
mvn plugins:list -Dpl
```

### Check Java Version Used
```bash
mvn compiler:testCompile -X | grep java.version
```

---

## 📝 Complex Commands

### Run Specific Test with Browser and Logging
```bash
mvn clean test -Dtest=TestRunner -Dbrowser=firefox -X
```

### Run Smoke Tests Headless with Custom Timeout
```bash
mvn clean test -Dgroups=smoke -Dbrowser=chrome -Dheadless=true
```

### Run Tests Parallel with Report Generation
```bash
mvn clean test -DthreadCount=3 allure:report
```

### Run Tests and Generate Report Without Opening
```bash
mvn clean test allure:report
```

### Run with Custom JVM Arguments
```bash
mvn clean test -DargLine="-Xmx512m -XX:MaxPermSize=256m"
```

---

## 🎯 TestNG Specific

### Run TestNG Suite File
```bash
mvn clean test -Dsuite=testng.xml
```

### Run Specific Method
```bash
mvn test -Dtest=TestRunner#smokeTest
```

### Run with TestNG Annotations
```bash
mvn clean test -Dgroups=smoke
```

---

## 📋 Useful Combinations

### Development Testing (Quick Feedback)
```bash
mvn clean test -Dgroups=smoke -q
```

### Continuous Integration (Full Suite)
```bash
mvn clean test allure:report -B
```

### Local Debugging
```bash
mvn clean test -X -Dtest=TestRunner
```

### Overnight Run (All Tests, Detailed Output)
```bash
mvn clean test allure:report -e
```

### Fresh Build & Test
```bash
mvn clean install -U && mvn clean test allure:serve
```

---

## 💡 Pro Tips

### Create Shell Alias (Linux/Mac)
```bash
alias mvn-smoke='mvn clean test -Dgroups=smoke'
alias mvn-report='mvn allure:serve'
alias mvn-full='mvn clean test allure:report'
```

### Create Batch File (Windows)
```batch
@echo off
mvn clean test -Dgroups=smoke -DthreadCount=3
```

### Run Multiple Commands
```bash
mvn clean && mvn install && mvn test && mvn allure:report
```

### Background Execution (Linux/Mac)
```bash
nohup mvn clean test &
tail -f nohup.out
```

---

## 🔗 File Locations

| Output/File | Location |
|---|---|
| Test Results | `target/surefire-reports/` |
| Allure Results | `target/allure-results/` |
| Allure Report | `target/allure-report/` |
| Maven Logs | Console output |
| Screenshots | `screenshots/` |
| Test Logs | `logs/` |
| Coverage Report | `target/site/jacoco/` |

---

## ⏱️ Command Execution Times

| Command | Typical Duration |
|---|---|
| `mvn clean` | 2-5 seconds |
| `mvn compile` | 5-10 seconds |
| `mvn clean install` | 30-60 seconds |
| `mvn clean test` (1 test) | 15-30 seconds |
| `mvn allure:report` | 5-10 seconds |
| `mvn allure:serve` | 3-5 seconds (plus browser load) |

---

## 🚨 Common Issues & Solutions

### "Compilation Error"
```bash
mvn clean compile
# Check error messages for syntax issues
```

### "Tests Skipped"
```bash
mvn clean test -Dgroups=smoke
# Verify group names match @Test(groups="...")
```

### "Out of Memory"
```bash
MAVEN_OPTS="-Xmx1024m" mvn clean test
```

### "WebDriver Not Found"
```bash
mvn clean test -X
# Check WebDriverManager download logs
```

### "Excel File Not Found"
```bash
# Verify file path in config.properties
# Check working directory: pwd
```

---

## 📞 Getting Help

### Show Maven Help
```bash
mvn help:help
```

### Show Plugin Help
```bash
mvn surefire:help
mvn allure:help
```

### List All Available Goals
```bash
mvn help:describe -Dgoal=help
```

---

Last Updated: May 15, 2026
Status: Complete ✅
