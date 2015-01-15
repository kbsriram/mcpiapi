import mcpi.minecraft as minecraft
import mcpi.block as block
import mcpi.vec3 as vec3
import sys
import nbt.nbt as nbt
import cStringIO
import binascii

# Block ids that I want to place after all the
# static blocks are in place.
dynamicBlocks = set([
    8, #flowing_water
    9, #water
    10, #flowing_lava
    12, #sand
    25, #noteblock
    27, #golder_rail
    28, #detector_rail
    29, #sticky_piston
    33, #piston
    34, #piston_head
    36, #piston_extension
    50, #torch
    41, #fire
    55, #redstone_wire
    62, #standing_sign
    64, #wooden_door
    65, #ladder
    66, #rail
    68, #wall_sign
    69, #lever
    70, #stone_pressure_plate
    71, #iron_door
    72, #wooden_pressure_plate
    75, #unlit_redstone_torch
    76, #redstone_torch
    77, #stone_button
    93, #unpowered_repeater
    94, #powered_repeater
    96, #trapdoor
    101, #iron_bars
    107, #fence_gate
    131, #tripwire_hook
    132, #tripwire
    143, #wooden_button
    147, #light_weighted_pressure_plate
    148, #heavy_weighted_pressure_plate
    149, #unpowered_comparator
    150, #powered_comparator
    152, #redstone_block
    157, #activator_rail
    171 #carpet
])

def nbt2hex(item):
    file = nbt.NBTFile()
    file.name = 'data'
    file.tags = item.tags
    out = cStringIO.StringIO()
    file.write_file(fileobj=out)
    return binascii.b2a_hex(bytearray(out.getvalue()))


class Optimizer():
    def __init__(self, mc):
        self.mc = mc
        self.sx = None
        self.sy = None
        self.sz = None
        self.obid = None
        self.odatum = None
        self.ex = None
        self.state = False

    def push(self, nx, ny, nz, nbid, ndatum):
        if nbid != self.obid or ny != self.sy or nz != self.sz \
           or ndatum != self.odatum:
            self.flush()
            self.sx = nx
            self.sy = ny
            self.sz = nz
            self.obid = nbid
            self.odatum = ndatum
            self.state = True
        # Always set end-x to cur value.
        self.ex = nx

    def flush(self):
        if self.state:
            self.mc.setBlocks(self.sx,self.sy,self.sz,self.ex,self.sy,self.sz,
                              self.obid,self.odatum,2)
            self.sx = None
            self.sy = None
            self.sz = None
            self.obid = None
            self.odatum = None
            self.ex = None
            self.state = False

mc = minecraft.Minecraft.create()
if len(sys.argv) != 2:
    mc.postToChat('Usage: /py load <schematic>')
    exit(0)

nbtfile = nbt.NBTFile(sys.argv[1], 'rb')
w = nbtfile['Width'].value
h = nbtfile['Height'].value
l = nbtfile['Length'].value

tileentities = nbtfile['TileEntities']
blocks = nbtfile['Blocks'].value
data = nbtfile['Data'].value
if len(blocks) != w*h*l:
    print "Not matching."
    exit(0)

pos = mc.player.getTilePos()
px = pos.x + 1
pz = pos.z + 1
py = mc.getHeight(px, pz)

mc.postToChat('Need to build ' + str(l) + ' levels');

mc.setBlocks(px, py, pz, px+w, py+h, pz+l, block.AIR.id)

opt = Optimizer(mc)

for y in range(0, h):
    mc.postToChat('Level '+str(y))
    for z in range(0, l):
        for x in range(0, w):
            idx = (y*l + z)*w +x
            blockid = blocks[idx]
            if (blockid == 0):
                opt.flush()
                continue
            if (blockid in dynamicBlocks): # skip these the first time.
                opt.flush()
                continue
            datum = data[idx]
            #mc.setBlock(px+x,py+y,pz+z,blockid,datum)
            opt.push(px+x,py+y,pz+z,blockid,datum)

for y in range(0, h):
    mc.postToChat('Level '+str(y))
    for z in range(0, l):
        for x in range(0, w):
            idx = (y*l + z)*w +x
            blockid = blocks[idx]
            if (blockid == 0):
                opt.flush()
                continue
            if (not (blockid in dynamicBlocks)):
                opt.flush()
                continue
            idx = (y*l + z)*w +x
            datum = data[idx]
            #mc.setBlock(px+x,py+y,pz+z,blockid,datum)
            opt.push(px+x,py+y,pz+z,blockid,datum)

opt.flush()

ntileentities = len(tileentities)
if ntileentities > 0:
    mc.postToChat('Updating '+str(ntileentities)+' blocks')

for tileentity in tileentities:
    nx = px + tileentity['x'].value
    ny = py + tileentity['y'].value
    nz = pz + tileentity['z'].value
    tileentity['x'].value = nx
    tileentity['y'].value = ny
    tileentity['z'].value = nz
    hex = nbt2hex(tileentity)
    mc.conn.send('world.setTileEntityHex', nx, ny, nz, hex)

mc.postToChat('All done')
