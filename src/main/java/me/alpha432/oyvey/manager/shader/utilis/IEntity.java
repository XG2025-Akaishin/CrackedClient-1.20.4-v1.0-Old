package me.alpha432.oyvey.manager.shader.utilis;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
//import thunder.hack.modules.render.Trails;

import java.util.List;

public interface IEntity {
    //List<Trails.Trail> thunderHack_Recode$getTrails();

    List<Vec3d> getPrevPositions();

    //BlockPos thunderHack_Recode$getVelocityBP();
}
