package com.kingodogo.buildscape.block.entity;
public interface IDataSerializable {
    void readData(IBlockEntityReadData data);

    void writeData(IBlockEntityWriteData data);

    default void readData(IBlockEntityData data) {
        readData((IBlockEntityReadData) data);
    }

    default void writeData(IBlockEntityData data) {
        writeData((IBlockEntityWriteData) data);
    }
}
