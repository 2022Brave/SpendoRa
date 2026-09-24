# SPENDO₹A — Personal Finance & Budget Tracker

> **"Discipline today. Freedom tomorrow."**

SPENDO₹A is a modern, privacy-first, offline-first personal finance application built for Android with Jetpack Compose, Material Design 3, and Room Database. Designed specifically for Indian Rupee (₹) denomination and the Indian Numbering System.

---

## 📥 How to Download the APK from GitHub

You can download the installable Android APK directly from this GitHub repository in two ways:

### Option 1: Download from GitHub Releases (Recommended)
1. Navigate to the [**Releases**](https://github.com/) section on the right sidebar of this repository.
2. Under the latest release (e.g., `v1.0`), find the **Assets** section.
3. Click on **`Spendora-v1.0-debug.apk`** to download it directly to your computer or Android phone.
4. On your Android phone, tap the downloaded APK to install (enable "Install unknown apps" if prompted).

### Option 2: Download from GitHub Actions (Latest Continuous Build)
Every commit and push automatically builds a fresh APK using GitHub Actions CI/CD:
1. Click the **[Actions](../../actions)** tab at the top of this GitHub repository.
2. Click on the topmost workflow run with a green checkmark (e.g., *Build & Release APK*).
3. Scroll down to the **Artifacts** section at the bottom of the summary page.
4. Click **`Spendora-Android-APK`** to download the ZIP file containing the APK.

---

## 🚀 Features

- **Intuitive Expense & Income Tracking**: Record and categorize daily transactions in Indian Rupees (₹).
- **Indian Numbering Format**: Native formatting supporting Lakhs and Crores (e.g. `₹1,23,456.78`).
- **Smart Category Budgets**: Set monthly limits and monitor spending thresholds with visual progress bars.
- **Deep Analytics & Insights**: Visual expense distribution, category breakdowns, and weekly/monthly trends.
- **Offline-First & Private**: Powered by local SQLite / Room Database. No cloud account or external tracking required.
- **M3 Dark & Light Theming**: High-contrast Midnight Black + Royal Purple dark theme and clean Light theme.
- **Modern Android Architecture**: 100% Kotlin, Jetpack Compose UI, MVVM architecture, Coroutines & Flow.

---

## 🛠️ Building from Source

To build the APK locally on your machine:

### Prerequisites
- JDK 21 or higher
- Android SDK (API Level 36)

### Steps
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd <repository-directory>
   ```

2. Build the Debug APK:
   ```bash
   ./gradlew assembleDebug
   ```

3. The generated APK will be available at:
   ```bash
   app/build/outputs/apk/debug/app-debug.apk
   ```

4. Install directly to a connected Android phone or emulator:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 📄 License

This project is licensed under the Apache License 2.0.
