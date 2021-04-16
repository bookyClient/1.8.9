package tk.bookyclient.bookyclient.features.betterhead;
// Created by booky10 in bookyClient (16:52 12.09.20)

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;

public class ModelBoxUV extends ModelBox {

    public final Boolean mirror;
    public final PositionTextureVertex[] vertexPositions;
    public QuadData[] quadDataList;
    public TexturedQuad[] quadList;

    public static final Integer[][] vectorIndex = new Integer[][]{{5, 1, 2, 6}, {0, 4, 7, 3}, {5, 4, 0, 1}, {2, 3, 7, 6}, {1, 0, 3, 2}, {4, 5, 6, 7}};
    public final Float textureWidth, textureHeight, minX, minY, minZ, maxX, maxY, maxZ;

    public static ModelBoxUV addBox(ModelRendererUV renderer, float x, float y, float z, int w, int h, int d, float add) {
        ModelBoxUV box = new ModelBoxUV(renderer, renderer.textureOffsetX, renderer.textureOffsetY, x, y, z, w, h, d, add);
        renderer.cubeList.add(box);
        return box;
    }

    public ModelBoxUV(ModelRendererUV modelRenderer, int textureX, int textureY, float x, float y, float z, int xOffset, int yOffset, int zOffset, float add) {
        super(modelRenderer, textureX, textureY, x, y, z, xOffset, yOffset, zOffset, add);

        textureWidth = modelRenderer.textureWidth;
        textureHeight = modelRenderer.textureHeight;

        minX = x;
        minY = y;
        minZ = z;
        maxX = x + xOffset;
        maxY = y + yOffset;
        maxZ = z + zOffset;

        mirror = modelRenderer.mirror;
        vertexPositions = new PositionTextureVertex[8];
        quadDataList = new QuadData[6];
        quadList = null;

        float x2 = x + xOffset;
        float y2 = y + yOffset;
        float z2 = z + zOffset;

        x -= add;
        y -= add;
        z -= add;
        x2 += add;
        y2 += add;
        z2 += add;

        if (mirror) {
            float f7 = x2;
            x2 = x;
            x = f7;
        }

        PositionTextureVertex vertex0 = new PositionTextureVertex(x, y, z, 0.0f, 0.0f);
        PositionTextureVertex vertex2 = new PositionTextureVertex(x2, y, z, 0.0f, 8.0f);
        PositionTextureVertex vertex3 = new PositionTextureVertex(x2, y2, z, 8.0f, 8.0f);
        PositionTextureVertex vertex4 = new PositionTextureVertex(x, y2, z, 8.0f, 0.0f);
        PositionTextureVertex vertex5 = new PositionTextureVertex(x, y, z2, 0.0f, 0.0f);
        PositionTextureVertex vertex6 = new PositionTextureVertex(x2, y, z2, 0.0f, 8.0f);
        PositionTextureVertex vertex7 = new PositionTextureVertex(x2, y2, z2, 8.0f, 8.0f);
        PositionTextureVertex vertex8 = new PositionTextureVertex(x, y2, z2, 8.0f, 0.0f);

        vertexPositions[0] = vertex0;
        vertexPositions[1] = vertex2;
        vertexPositions[2] = vertex3;
        vertexPositions[3] = vertex4;
        vertexPositions[4] = vertex5;
        vertexPositions[5] = vertex6;
        vertexPositions[6] = vertex7;
        vertexPositions[7] = vertex8;

        for (int i = 0; i < quadDataList.length; ++i) {
            quadDataList[i] = new QuadData(textureX, textureY, xOffset, yOffset, zOffset, i);
        }
    }

    public ModelBoxUV setSideUV(int side, int textureX, int textureY) {
        QuadData data = quadDataList[side];

        int deltaX = Math.abs(data.uvPos[2] - data.uvPos[0]);
        int deltaY = Math.abs(data.uvPos[3] - data.uvPos[1]);

        data.uvPos[0] = textureX;
        data.uvPos[1] = textureY;
        data.uvPos[2] = textureX + deltaX;
        data.uvPos[3] = textureY + deltaY;

        if (side == 3) {
            data.uvPos[1] = textureY + deltaY;
            data.uvPos[3] = textureY;
        }
        return this;
    }

    public ModelBoxUV setAllUV(int textureX, int textureY) {
        for (int i = 0; i < quadDataList.length; ++i)
            setSideUV(i, textureX, textureY);
        return this;
    }

    public ModelBoxUV initQuads() {
        quadList = new TexturedQuad[6];
        for (int i = 0; i < quadList.length; ++i) {
            QuadData data = quadDataList[i];
            quadList[i] = new TexturedQuad(getVertexes(i), data.uvPos[0], data.uvPos[1], data.uvPos[2], data.uvPos[3], textureWidth, textureHeight);
        }

        if (mirror) {
            for (TexturedQuad texturedQuad : quadList) {
                texturedQuad.flipFace();
            }
        }

        quadDataList = null;
        return this;
    }

    public PositionTextureVertex[] getVertexes(int side) {
        int index1 = ModelBoxUV.vectorIndex[side][0];
        int index2 = ModelBoxUV.vectorIndex[side][1];
        int index3 = ModelBoxUV.vectorIndex[side][2];
        int index4 = ModelBoxUV.vectorIndex[side][3];

        return new PositionTextureVertex[]{vertexPositions[index1], vertexPositions[index2], vertexPositions[index3], vertexPositions[index4]};
    }

    public void render(Tessellator par1Tessellator, float partialTicks) {
        if (quadList == null) initQuads();
        for (TexturedQuad texturedQuad : quadList) texturedQuad.draw(par1Tessellator.getWorldRenderer(), partialTicks);
    }

    public static class QuadData {

        public final Integer[] uvPos;

        public QuadData(int textureX, int textureY, int w, int h, int d, int side) {
            uvPos = new Integer[4];
            switch (side) {
                case 0:
                    uvPos[0] = textureX + d + w;
                    uvPos[1] = textureY + d;
                    uvPos[2] = textureX + d + w + d;
                    uvPos[3] = textureY + d + h;
                    break;
                case 1:
                    uvPos[0] = textureX;
                    uvPos[1] = textureY + d;
                    uvPos[2] = textureX + d;
                    uvPos[3] = textureY + d + h;
                    break;
                case 2:
                    uvPos[0] = textureX + d;
                    uvPos[1] = textureY;
                    uvPos[2] = textureX + d + w;
                    uvPos[3] = textureY + d;
                    break;
                case 3:
                    uvPos[0] = textureX + d + w;
                    uvPos[1] = textureY + d;
                    uvPos[2] = textureX + d + w + w;
                    uvPos[3] = textureY;
                    break;
                case 4:
                    uvPos[0] = textureX + d;
                    uvPos[1] = textureY + d;
                    uvPos[2] = textureX + d + w;
                    uvPos[3] = textureY + d + h;
                    break;
                case 5:
                    uvPos[0] = textureX + d + w + d;
                    uvPos[1] = textureY + d;
                    uvPos[2] = textureX + d + w + d + w;
                    uvPos[3] = textureY + d + h;
                    break;
                default:
                    break;
            }
        }
    }
}