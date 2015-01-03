#Minecraft Pi API mod

This is a [Minecraft Forge](http://www.minecraftforge.net/wiki/Installation/Universal) mod that reimplements (a subset of) the Minecraft Pi edition API.

The Minecraft Pi Edition also contains a [simple to use API](http://www.stuffaboutcode.com/p/minecraft.html). My plugin recreates this simple API in your desktop Minecraft, so you can now program your Desktop edition Minecraft easily using the Pi API.

##Screenshots

![Massive structures](../master/downloads/massive.jpg?raw=true)

Outside the skyscraper and shuttle, built using Martin O'Hanlon's [massive 3d structures project](http://www.stuffaboutcode.com/2013/03/minecraft-pi-edition-create-massive-3d.html).

![Sokoban](../master/downloads/sokoban.jpg?raw-true)

Running the sokoban demo from the standard [API library](http://pi.minecraft.net/)

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

To get the Pi Edition client library - download [the free Minecraft Pi
Edition](https://s3.amazonaws.com/assets.minecraft.net/pi/minecraft-pi-0.1.1.tar.gz)
and you should find java and python clients, as well as a few demos
under the `mcpi/api` directory.

Not all the commands are implemented, and I've probably botched up the
right way to do many things -- this was my first foray into writing a
forge plugin. But I hope you find it useful and fun nevertheless.

Functionally, this mod is also like the [RaspberryJuice
plugin](https://github.com/zhuowei/RaspberryJuice). However,
RasperryJuice runs on a Bukkit server, whereas this mod runs under any
client running Minecraft Forge.
