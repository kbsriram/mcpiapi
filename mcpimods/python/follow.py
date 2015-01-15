import mcpi.minecraft as minecraft
import mcpi.block as block

# Quick sample to test the blocking player movement

mc = minecraft.Minecraft.create()

while True:
    data = mc.conn.sendReceive('events.entity.wait.movedTile').split(",")
    mc.postToChat("Player {} at {},{},{}".format(
        data[0], data[1], data[2], data[3]))
