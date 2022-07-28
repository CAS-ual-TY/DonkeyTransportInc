package de.cas_ual_ty.donkey;

import net.minecraft.nbt.ByteTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IHypnotized extends INBTSerializable<ByteTag>
{
    boolean isHypnotized();
    
    void hypnotize();
    
    void unhypnotize();
}
