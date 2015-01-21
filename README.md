#Minecraft Pi API mod

This is a [Minecraft Forge](http://www.minecraftforge.net/wiki/Installation/Universal) mod that reimplements (a subset of) the Minecraft Pi edition API.

The Minecraft Pi Edition also contains a [simple to use API](http://www.stuffaboutcode.com/p/minecraft.html). My plugin recreates this simple API in your desktop Minecraft, so you can now program your Desktop edition Minecraft easily using the Pi API.

##Screenshots

Outside the skyscraper and shuttle, built using Martin O'Hanlon's [massive 3d structures project](http://www.stuffaboutcode.com/2013/03/minecraft-pi-edition-create-massive-3d.html).

![Massive structures](../master/downloads/massive.jpg?raw=true)

Running the sokoban demo from the standard [API library](http://pi.minecraft.net/)

![Sokoban](../master/downloads/sokoban.jpg?raw=true)

I've added an in-game command `/python` which calls an `mcpi` python script you place in a special directory. [This example](mcpimods/python/clear.py) clears a cubiod of blocks in front of you.

![Clear a cuboid of blocks](../master/downloads/clear_mod.gif?raw=true)

##Installing

This plugin works with the 1.7.10 version of Minecraft.

If you haven't installed Minecraft Forge, take a look at the
[installation instructions for
Forge](http://www.minecraftforge.net/wiki/Installation/Universal).

Once you've successfully installed Minecraft Forge, [download
the jar file](../master/downloads/McpiApiMod-1.7.10-1.0.jar) to
your `minecraft/mods` folder.

If you've already programmed the Pi edition with one of the client
libraries - the plugin starts up on the same port (4711) so your
code should work unchanged.

If you're new to programming the Pi API - Martin O'Hanlon has a nice
[introductory series of
articles](http://www.stuffaboutcode.com/p/minecraft.html) that should
get you started.

I've included the python version of the Pi Edition client library and a few demos under the [`mcpimods/python`](mcpimods/python) directory. It adds support for some additional commands supported by the mod - but the mod should also work against the standard Pi edition client library.

To get the standard Pi Edition client library - download [the free Minecraft Pi
Edition](https://s3.amazonaws.com/assets.minecraft.net/pi/minecraft-pi-0.1.1.tar.gz)
and you should find java and python clients, as well as a few demos
under its `mcpi/api` directory.

Not all the commands from the standard client are implemented, and
I've probably botched up the right way to do many things -- this was
my first foray into writing a forge plugin. But I hope you find it
useful and fun nevertheless.

Functionally, this mod is similar to the [RaspberryJuice
plugin](https://github.com/zhuowei/RaspberryJuice). However,
RasperryJuice runs on a Bukkit server, whereas this mod runs under any
client running Minecraft Forge.

##Enhancements

The forge mod tries to be compatible with the extended commands
supported by the [RaspberryJuice
plugin](https://github.com/zhuowei/RaspberryJuice).

I've also added an in-game command `/python` which calls scripts you
place under a directory called `mcpimods/python` in your root
Minecraft installation.

The `mcpimods/` directory should be next to your Forge `mod/`
directory. For example, if (on a Mac) your Forge mod directory looks
like `Library/Application Support/minecraft/mods/`, you would place
your python scripts under `Library/Application
Support/minecraft/mcpimods/python`

From within the game, you simply call the name of your script (without
the `.py` extension) and it will launch the python script as an
external process.

This is best suited for running short commands rather than something
that runs in an infinite loop; but of course I hope you experiment and
find interesting things.

I've [bundled the python mcpi library](mcpimods/python) which has a
small demo client that [clears a cuboid of
blocks](mcpimods/python/clear.py). You should also be able to copy the
[`mcpimods`](mcpimods) directory to the root of your minecraft
installation and start using the `/python` in-game command from the
mod.

I've added some experimental commands:

`world.setTileEntityHex(x,y,z,hex)`, which takes an `x,y,z` position
relative to the spawn point, and an ASCII hex string representing a
tile entity in [NBT format](http://wiki.vg/NBT). The main purpose
was to have some (weak) support for importing schematic files, and you
can see [an experimental script](mcpimods/python/load.py) that tries to
load schematic files. It doesn't always do the right thing, but it's good enough to have some fun creating structures from [schematic files](http://minecraft.gamepedia.com/Schematic_file_format)

Reading an [autofarm schematic](http://www.minecraft-schematics.com/schematic/934/) via the in-game `/python` command.
![Reading a schematic](../master/downloads/load_mod.gif?raw=true)


`events.block.wait.hit()` waits until a block is hit, and returns the
`x,y,z` position of the block. This can be useful to avoid busy wait
loops from your code.

`events.entity.wait.movedTile()` waits until a player has moved
their tile position, and reports a `,` separated string like
`entityid,x,y,z`. The `x,y,z` position is the current tile position of
the player with `entityid`. This too is handy to avoid busy wait loops
in your code. Check out [this example](mcpimods/python/follow.py) for
a hello world type script for how to use this command.

`events.setting(key,value)` to configure events. Currently, just one
key is available -- `restrict_to_sword` and it takes `0` or `1` as its
boolean value. `events.setting(restrict_to_sword,0)` will remove the
(default) check that reports block hit events only when the player is
also holding a sword.
