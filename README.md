# ShitterList Fabric Mod

A Fabric mod for Minecraft 1.21.10 that automatically removes specified players from your party when they join via party finder on Hypixel Skyblock. 

Made by hydro!

## Features

- Automatically detects when players join your party via party finder
- Automatically removes players on your shitterlist using `/party remove <username>`
- Persistent configuration saved to `config/shitterlist.json`
- In-game commands to manage your shitterlist

## Commands

- `/shitterlist add <username>` - Add a player to the shitterlist
- `/shitterlist remove <username>` - Remove a player from the shitterlist  
- `/shitterlist list` - Show all players on the shitterlist
- `/shitterlist clear` - Clear the entire shitterlist
- `/shitterlist` - Show help message

## How It Works

The mod listens for party join messages from Hypixel's party finder system:
- `Party Finder > (?<username>[\\w]+) joined the dungeon group!`

When a player on your shitterlist joins, the mod automatically executes `/party kick <username>` and sends you a confirmation message.

## Configuration

The shitterlist is automatically saved to `config/shitterlist.json` in your Minecraft directory. You can edit this file manually if needed.

## Requirements

- Minecraft 1.21.10
- Fabric Loader 0.15.11+
- Fabric API

## License

MIT License
