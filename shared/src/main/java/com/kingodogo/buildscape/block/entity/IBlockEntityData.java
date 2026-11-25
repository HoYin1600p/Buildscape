package com.kingodogo.buildscape.block.entity;

import java.util.Optional;
public interface IBlockEntityData extends IBlockEntityReadData, IBlockEntityWriteData {
    @Override
    IBlockEntityData createChild();

    @Override
    default void putSubData(String key, IBlockEntityWriteData data) {
        if (data instanceof IBlockEntityData d) {
            putSubData(key, d);
        } else if (data != null) {
            IBlockEntityData child = createChild();
            data.copyTo(child);
            putSubData(key, child);
        }
    }

    void putSubData(String key, IBlockEntityData data);

    @Override
    Optional<IBlockEntityData> getSubData(String key);
}
