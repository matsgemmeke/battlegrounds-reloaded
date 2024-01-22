package com.github.matsgemmeke.battlegrounds.configuration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

public class ItemConfiguration extends AbstractPluginConfiguration {

    public ItemConfiguration(@NotNull File file, @Nullable InputStream resource) {
        super(file, resource, true);
    }

    public Set<String> getIdList() {
        return this.getRoot().getRoutesAsStrings(false);
    }
}
