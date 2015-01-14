import mcpi.minecraft as minecraft
import mcpi.block as block
import mcpi.vec3 as vec3
import sys

def asCardinalDirection(yaw):
    while yaw < 0:
        yaw = yaw + 360
    while yaw > 360:
        yaw = yaw - 360

    if (yaw < 45) or (yaw > 315):
        return 0 # South
    elif yaw < 135:
        return 1 # West
    elif yaw < 225:
        return 2 # North
    else:
        return 3 # East

mc = minecraft.Minecraft.create()
if len(sys.argv) != 4:
    mc.postToChat('Usage: /py clear <number> <number> <number>')
    exit(0)
try:
    x = int(sys.argv[1])
    y = int(sys.argv[2])
    z = int(sys.argv[3])
except ValueError:
    mc.postToChat('Usage: /py clear <number> <number> <number>')
    exit(0)

ppos = mc.player.getTilePos()

# Where is the player looking (to the closest cardinal direction)
direction = asCardinalDirection(mc.player.getRotation())
print "direction", direction

# and update x/z suitably.
if direction == 0:
    sx,ex,sz,ez = -x/2,x/2,0,z
elif direction == 1:
    sx,ex,sz,ez = 0,-z,x/2,-x/2
elif direction == 2:
    sx,ex,sz,ez = -x/2,x/2,0,-z
else:
    sx,ex,sz,ez = 0,z,x/2,-x/2

mc.setBlocks(ppos.x+sx, ppos.y,ppos.z+sz,
             ppos.x+ex, ppos.y+y, ppos.z+ez, block.AIR.id)
