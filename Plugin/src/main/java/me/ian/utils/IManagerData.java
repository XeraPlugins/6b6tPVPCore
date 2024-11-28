package me.ian.utils;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

public interface IManagerData<T> {

    void create(T t);

    void delete(T t);

    NBTTagCompound toCompound(T t);

    T fromCompound(NBTTagCompound compound);

}
