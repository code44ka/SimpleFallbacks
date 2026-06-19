# 🚧 SimpleFallbacks — Development Branch (`dev`)

Welcome to the development branch of **SimpleFallbacks**! This branch contains the latest features, experimental code, and active bug fixes that are currently being tested for the next official release.

> ⚠️ **Disclaimer:** Code in this branch is a work in progress (WIP). It may be unstable, contain bugs, or change completely without notice. **Do not use this branch on production servers!**

---

## 🛠️ Current Development Focus
We are currently working on adding and improving the following systems:
* **Enhanced Virtual Limbo:** Optimizing performance under heavy player loads.
* **Auto-Reconnect System:** Trying to reconnect players to their original server before moving them to fallback.
* **Config Reload Command:** Adding a hot-reload feature without restarting the proxy.

## 📦 How to Test & Compile
If you want to test the absolute latest features, you can clone this branch and compile it yourself using Gradle:

1. Clone this specific branch:
   ```bash
   git clone -b dev https://github.com
   ```
2. Navigate to the project directory:
   ```bash
   cd SimpleFallbacks
   ```
3. Build the plugin:
   ```bash
   ./gradlew build
   ```
The compiled `.jar` file will be located in the `build/libs/` directory.

## 🤝 Contributing & Feedback
If you encounter any bugs while testing this branch, please open an issue on our [Issues Page](https://github.com) and make sure to specify that you are using the `dev` branch.

To return to the stable version, please switch to the [master branch](https://github.com).
