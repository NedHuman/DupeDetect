# DupeDetect
A minecraft bukkit plugin which aids in the detection of duplication glithces
## How it works
The plugin assigns a random unique 64-bit ID to each shulker box (can be configured to contain other items). If the plugin finds multiple shulker boxes with the same ID, it means the item was duplicated and alerts online players with the dupedetect.notify permission, and optionally may send a discord webhook. 
This plugin does not magically solve all duping, it only helps with the detection of it. The idea is that most players will use shulker boxes to duplicate their goods.

Note: Don't use this plugin if your server involves mechanics which duplicate items, it will create false-positives.

## Features
* Assigns a unique ID to certain items
* Staff are notified in chat about duplicate items found
* A GUI to see the most recent dupe alerts
* An API for developers

## Permissions:
- dupedetect.notify - be notified about duplicate items found
- dupedetect.bypass - bypass the duplicate item checker
- dupedetect.getcopy - receive a copy of a duped item
- dupedetect.menu - access the dupedetect menu

## Spigot Page
https://www.spigotmc.org/resources/dupedetect.120053/
