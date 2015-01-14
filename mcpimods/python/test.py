import sys
import nbt.nbt as nbt
import cStringIO
import binascii

nbtfile = nbt.NBTFile(sys.argv[1], 'rb')

print nbtfile.pretty_tree()

w = nbtfile['Width'].value
h = nbtfile['Height'].value
l = nbtfile['Length'].value
blocks = nbtfile['Blocks'].value
data = nbtfile['Data'].value
for y in range(0, h):
    for z in range(0, l):
        for x in range(0, w):
            idx = (y*l + z)*w +x
            blockid = blocks[idx]
            print "{},{},{}, {} {}".format(x,y,z,blockid,data[idx])

# tileentities = nbtfile['TileEntities']
# testentity = tileentities[0]
# mynbt = nbt.NBTFile()
# mynbt.name = 'data'
# print testentity.pretty_tree()
# mynbt.tags = testentity.tags
# print mynbt.pretty_tree()
# out = cStringIO.StringIO()
# mynbt.write_file(fileobj=out)
# print binascii.b2a_hex(bytearray(out.getvalue()))
