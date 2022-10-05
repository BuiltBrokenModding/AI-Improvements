# AI-Improvements-
General adjustment to MC's AIs to improve performance and functionality 

Downloads: https://www.curseforge.com/minecraft/mc-mods/ai-improvements

# Improvements

* Tan math cache - Large improvement in 1.7-1.9, Small improvement 1.10+
* Disable AI tasks - Large improvements on any version for performance at the cost of mechanics 


# Change Log

# 0.5.2

## User Impact
* Performance: Removed listener for LivingSpawnEvent. It is not needed, and added unnecessary overhead

# 0.5.1

## User Impact
* Fixed: Compatibility with Forge 41.0.94+

# 0.5.0

## User Impact
* Added: Ability to define which entities are affected by the remove look goal, remove look random, replace look controller configs
    * Comes with a reset of those config values
* Fixed: Crash involving the filter system

## Dev Impact
* Added: Noop filter node
* Added: Filtered remove modifier to handle remove calls that are backed by a filtered config value

# 0.4.0

## User Impact
* Added: Support for disabling floating, panicking, breeding, tempting, follow parent, and stroll AI from cows, chickens, pigs, and sheep
* Added: Support for disabling eat block AI from sheep

# 0.3.0

## User Impact
* Fixed: Config pathing doing 'entity.entity.entity.entity.entity'
* Added: Support for disabling squid random swim and flee AI

## Dev Impact
* Implemented: New system for handling filtering and editing AI modification tasks. Avoids the need for a mess of IF-ELSE statements.
* Implemented: Sorted system for remove calls, Edits most often applied will float to the top of the list to reduce CPU time used for editing mobs.
* Added: Filter system - allows dev to filter edits to a specific mob-type or mob with settings
* Added: Generic remove edit - allows removing based on instance check and config check
* Added: Filter layer - allows registering a collection of edit tasks with sub layers
* Added: Modification layer - allows handling filter layers, combat AI edits, and goal edits as a set
* Added: Filter layer for all entities
* Added: Filter level for mobs
* Added: Filter level for fish
* Added: Filter level for squid
* Ported: look helper as a modification edit
* Ported: fish edits as generic edits


# 0.2.2
* Fixed: Crash due to invalid access transformer

# 0.2.1 - Init MC 1.14.4 release, 8/1/2019 9pm Est
* Update to 1.14.4
* Added: fish swim remove option
* Added: fish panic remove option
* Added: fish avoid player remove option

# Before 
* Added: Look Helper override
* Added: Tan math cache, used by Look helper to improve speed
