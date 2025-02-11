package de.labystudio.game.world.chunk;

import de.labystudio.game.render.Tessellator;
import de.labystudio.game.util.EnumWorldBlockLayer;
import de.labystudio.game.world.World;
import de.labystudio.game.world.WorldRenderer;
import de.labystudio.game.world.block.Block;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public class ChunkSection {
    public static final int SIZE = 16;

    public final World world;

    private final byte[] blockData = new byte[SIZE * SIZE * SIZE];
    private final byte[] blockLight = new byte[SIZE * SIZE * SIZE];

    public int x;
    public int y;
    public int z;

    private final int lists;
    private boolean queuedForRebuild = true;

    public ChunkSection(World world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.lists = GL11.glGenLists(EnumWorldBlockLayer.values().length);

        // Fill chunk with light using Arrays.fill
        Arrays.fill(this.blockLight, (byte) 15);
    }
    public void render(EnumWorldBlockLayer renderLayer) {
        // Call list with render layer
        GL11.glCallList(this.lists + renderLayer.ordinal());
    }

    public void rebuild(WorldRenderer renderer) {
        this.queuedForRebuild = false;

        // Rebuild all render layers
        for (EnumWorldBlockLayer layer : EnumWorldBlockLayer.values()) {
            rebuild(renderer, layer);
        }
    }

    public void queueForRebuild() {
        this.queuedForRebuild = true;
    }

    public boolean isQueuedForRebuild() {
        return queuedForRebuild;
    }

    private void rebuild(WorldRenderer renderer, EnumWorldBlockLayer renderLayer) {
        // Create GPU memory list storage
        int listIndex = this.lists + renderLayer.ordinal();
        GL11.glNewList(listIndex, GL11.GL_COMPILE);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, renderer.textureId);

        // Start rendering
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(7);

        int baseX = this.x * SIZE;
        int baseY = this.y * SIZE;
        int baseZ = this.z * SIZE;

        // Render blocks
        for (int x = 0; x < SIZE; x++) {
            int absoluteX = baseX + x;

            for (int y = 0; y < SIZE; y++) {
                int absoluteY = baseY + y;

                for (int z = 0; z < SIZE; z++) {
                    int absoluteZ = baseZ + z;
                    byte typeId = getBlockAt(x, y, z);

                    if (typeId != 0) {
                        Block block = Block.getById(typeId);
                        if (block != null && ((renderLayer == EnumWorldBlockLayer.CUTOUT) == block.isTransparent())) {
                            block.render(renderer, this.world, absoluteX, absoluteY, absoluteZ);
                        }
                    }
                }
            }
        }

        // Stop rendering
        tessellator.draw();

        // End storage
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEndList();
    }

    public boolean isEmpty() {
        for (byte block : blockData) {
            if (block != 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isSolidBlockAt(int x, int y, int z) {
        return getBlockAt(x, y, z) != 0;
    }

    public byte getBlockAt(int x, int y, int z) {
        int index = y << 8 | z << 4 | x;
        return blockData[index];
    }

    public void setLightAt(int x, int y, int z, int lightLevel) {
        int index = y << 8 | z << 4 | x;
        this.blockLight[index] = (byte) lightLevel;
    }

    public void setBlockAt(int x, int y, int z, int type) {
        int index = y << 8 | z << 4 | x;
        this.blockData[index] = (byte) type;
    }

    public int getLightAt(int x, int y, int z) {
        int index = y << 8 | z << 4 | x;
        return blockLight[index];
    }
}
