package tk.bookyclient.bookyclient.utils;
// Created by booky10 in bookyClient (18:06 12.04.21)

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class ClientResourcePack implements IResourcePack {

    private static final Set<String> DOMAINS = ImmutableSet.of(Constants.MOD_ID);
    private static final ClientResourcePack INSTANCE = new ClientResourcePack();

    @Override
    public InputStream getInputStream(ResourceLocation location) {
        return getClass().getResourceAsStream(getAssetPath(location));
    }

    @Override
    public boolean resourceExists(ResourceLocation location) {
        return getClass().getResource(getAssetPath(location)) != null;
    }

    @Override
    public Set<String> getResourceDomains() {
        return DOMAINS;
    }

    @Override
    public <T extends IMetadataSection> T getPackMetadata(IMetadataSerializer metadataSerializer, String metadataSectionName) throws IOException {
        return null;
    }

    @Override
    public BufferedImage getPackImage() {
        return null;
    }

    @Override
    public String getPackName() {
        return Constants.MOD_NAME;
    }

    private String getAssetPath(ResourceLocation location) {
        return "/assets/" + location.getResourceDomain() + "/" + location.getResourcePath();
    }

    public static ClientResourcePack getInstance() {
        return INSTANCE;
    }
}