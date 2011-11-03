WINKY
========

### Minecraft Server Wrapper for Realtime Git World Backups ###

CURRENTLY UNSTABLE - NOT RECOMMENDED FOR LIVE MINECRAFT SERVERS

Why save the entire world when you can just save the changes.

Commands

If no [world] is specified with the commands then the level-name in the server.properties is used.

git-commit [world] - Commits a world to the repository.

git-log [world] - List all commits for the given world. 

git-reset [world] [id] - Will shutdown the server, reset to a previous commit, and then start the server.

  