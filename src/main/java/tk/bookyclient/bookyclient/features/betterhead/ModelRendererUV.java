package tk.bookyclient.bookyclient.features.betterhead;
// Created by booky10 in bookyClient (16:52 12.09.20)

import com.google.common.collect.Lists;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TextureOffset;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ModelRendererUV extends ModelRenderer {

    public float textureWidth, textureHeight;
    public int textureOffsetX, textureOffsetY, displayList;
    public float rotationPointX, rotationPointY, rotationPointZ, rotateAngleX, rotateAngleY, rotateAngleZ;
    public boolean compiled, mirror;

    public final String boxName;
    public final List<ModelBox> cubeList;
    public List<ModelRenderer> childModels;
    public final ModelBase baseModel;

    public ModelRendererUV(ModelBase model, String boxName) {
        super(model, boxName);
        this.boxName = boxName;

        textureWidth = 64.0f;
        textureHeight = 32.0f;
        showModel = true;
        cubeList = Lists.newArrayList();
        baseModel = model;

        model.boxList.add(this);
        setTextureSize(model.textureWidth, model.textureHeight);
    }

    public ModelRendererUV(ModelBase model) {
        this(model, null);
    }

    public ModelRendererUV(ModelBase model, int textureOffsetX, int textureOffsetY) {
        this(model);
        setTextureOffset(textureOffsetX, textureOffsetY);
    }

    @Override
    public void addChild(ModelRenderer renderer) {
        if (childModels == null) childModels = Lists.newArrayList(renderer);
        else childModels.add(renderer);
    }

    @Override
    public ModelRenderer setTextureOffset(int x, int y) {
        textureOffsetX = x;
        textureOffsetY = y;
        return this;
    }

    @Override
    public ModelRenderer addBox(String partName, float offX, float offY, float offZ, int width, int height, int depth) {
        partName = boxName + "." + partName;
        TextureOffset textureoffset = baseModel.getTextureOffset(partName);
        setTextureOffset(textureoffset.textureOffsetX, textureoffset.textureOffsetY);
        cubeList.add(new ModelBoxUV(this, textureOffsetX, textureOffsetY, offX, offY, offZ, width, height, depth, 0.0f).setBoxName(partName));
        return this;
    }

    @Override
    public ModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth) {
        cubeList.add(new ModelBoxUV(this, textureOffsetX, textureOffsetY, offX, offY, offZ, width, height, depth, 0.0f));
        return this;
    }

    @Override
    public ModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth, boolean mirror) {
        cubeList.add(new ModelBoxUV(this, textureOffsetX, textureOffsetY, offX, offY, offZ, width, height, depth, 0.0f));
        return this;
    }

    @Override
    public void addBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor) {
        cubeList.add(new ModelBoxUV(this, textureOffsetX, textureOffsetY, offX, offY, offZ, width, height, depth, scaleFactor));
    }

    @Override
    public void setRotationPoint(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn) {
        rotationPointX = rotationPointXIn;
        rotationPointY = rotationPointYIn;
        rotationPointZ = rotationPointZIn;
    }

    public void renderBetterHat(float scale) {
        if (isHidden || !showModel) return;
        if (!compiled) compileDisplayList(scale);

        GlStateManager.pushMatrix();
        GlStateManager.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);

        if (rotateAngleY != 0.0f) GlStateManager.rotate(rotateAngleY * 57.295776f, 0.0f, 1.0f, 0.0f);
        if (rotateAngleX != 0.0f) GlStateManager.rotate(rotateAngleX * 57.295776f, 1.0f, 0.0f, 0.0f);
        if (rotateAngleZ != 0.0f) GlStateManager.rotate(rotateAngleZ * 57.295776f, 0.0f, 0.0f, 1.0f);

        GlStateManager.callList(displayList);
        GlStateManager.popMatrix();
    }

    public void compileDisplayList(float scale) {
        GL11.glNewList(displayList = GLAllocation.generateDisplayLists(1), 4864);

        for (ModelBox modelBox : cubeList) {
            ((ModelBoxUV) modelBox).render(Tessellator.getInstance(), scale);
        }

        GL11.glEndList();
        compiled = true;
    }

    public void applyRotation(ModelRenderer source) {
        rotateAngleX = source.rotateAngleX;
        rotateAngleY = source.rotateAngleY;
        rotateAngleZ = source.rotateAngleZ;
        rotationPointX = source.rotationPointX;
        rotationPointY = source.rotationPointY;
        rotationPointZ = source.rotationPointZ;
    }

    @Override
    public ModelRenderer setTextureSize(int textureWidthIn, int textureHeightIn) {
        textureWidth = textureWidthIn;
        textureHeight = textureHeightIn;
        return this;
    }
}