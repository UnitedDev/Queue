# Portal

A multi-proxy player queue.

## How It Works
* Player joins a queue through a hub.
* Hub sends a message to the Independent module to add that player to that queue.
* Independent module constantly checks if a queue can send a player. If a player can be sent, a message is broadcasted to all Bukkit instances that the next player in the queue (based on rank priority) should be sent to the server.
* Any Bukkit instance that contains the player will use plugin messages to send the player.

## Commands
| Command syntax               | Description                         | Permission       |
| ---------------------------- | ----------------------------------- | ---------------- |
| /queueclear \<queue\>          | Clear the list of a queue           | portal.clear     |
| /queuetoggle \<queue\>         | Toggle (pause) a queue              | portal.toggle    |
| /forcesend \<player\> \<server\> | Force sends a player to a server    | portal.forcesend |
| /datadump                    | Displays all server data and queues | portal.datadump  |

## Priority
Queue priority can be assigned through permissions (config.yml) or by using your own implementation.

To implement your own priority system, extend `PriorityProvider` and set the instance provider using `Portal.getInstance().setPriorityProvider(provider)`.

## Bypass
Players that have the `portal.bypass` permission will immediately be sent to a server instead of joining a queue.

## Original Developer
Joeleoli