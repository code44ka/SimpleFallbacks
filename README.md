#  SimpleFallbacks
A lightweight and efficient `Velocity` proxy plugin designed to keep players connected when backend servers go offline. Powered by `LimboAPI`, it seamlessly moves players to a `virtual Limbo world` or a `fallback server` instead of kicking them from the network.

# ✨ FeaturesVirtual
**Limbo Worlds:** Hosts players in a lightweight, virtual `Limbo space` using `LimboAPI` if no servers are available.  
**Smart Fallback Routing:** Automatically transfers players from a disconnected or crashing server to an active lobby/hub server.  
**Customizable Messages:** Configure exactly what players see during transitions or server failures.  
**Performance Focused:** Built specifically for `Velocity`, ensuring minimal resource usage and maximum stability.  

# 🚀 Requirements
- Proxy: [Velocity](https://papermc.io/downloads/velocity)  
- Dependency: [LimboAPI](https://github.com/Elytrium/LimboAPI/releases)  

# 🛠️ Installation
1. Download the latest `.jar` file from the Releases page.  
2. Drop the file into your Velocity `plugins/` directory.  
3. Ensure **LimboAPI** is also installed in the same folder.  
4. Restart your Velocity proxy.  
5. Configure the `config.yml` file in `plugins/simplefallbacks/` to match your server setup.


> ### 🚧 Work In Progress
> This plugin is actively under development. Expect frequent updates, improvements, and new features!
