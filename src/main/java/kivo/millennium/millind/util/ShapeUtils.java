package kivo.millennium.millind.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;


public class ShapeUtils {
    public static VoxelShape rotateUPShape(VoxelShape shape, Direction target){
        VoxelShape rotatedShape = Shapes.empty();

        for (AABB box : shape.toAabbs()) {
            double x1 = box.minX;
            double y1 = box.minY;
            double z1 = box.minZ;
            double x2 = box.maxX;
            double y2 = box.maxY;
            double z2 = box.maxZ;

            switch (target) {
                case NORTH: {
                    double tempZ1 = z1;
                    double tempZ2 = z2;
                    z1 = 1 - y2;
                    z2 = 1 - y1;
                    y1 = tempZ1;
                    y2 = tempZ2;
                    break;
                }
                case SOUTH: {
                    double tempZ1 = z1;
                    double tempZ2 = z2;
                    z1 = y1;
                    z2 = y2;
                    y1 = 1 - tempZ2;
                    y2 = 1 - tempZ1;
                    break;
                }
                case WEST: {
                    double tempX1 = x1;
                    double tempX2 = x2;
                    x1 = 1 - y2;
                    x2 = 1 - y1;
                    y1 = tempX1;
                    y2 = tempX2;
                    break;
                }
                case EAST: {
                    double tempX1 = x1;
                    double tempX2 = x2;
                    x1 = y1;
                    x2 = y2;
                    y1 = 1 - tempX2;
                    y2 = 1 - tempX1;
                    break;
                }
                case DOWN: {
                    double temp = y1;
                    y1 = 1 - y2;
                    y2 = 1 - temp;
                    break;
                }
                case UP:{
                    break;
                }
                default:
                    break;
            }

            rotatedShape = Shapes.or(rotatedShape, Shapes.box(x1, y1, z1, x2, y2, z2));
        }
        return rotatedShape;
    }


    public static VoxelShape rotateNORTHShape(VoxelShape shape, Direction target){
        VoxelShape rotatedShape = Shapes.empty();

        for (AABB box : shape.toAabbs()) {
            double x1 = box.minX;
            double y1 = box.minY;
            double z1 = box.minZ;
            double x2 = box.maxX;
            double y2 = box.maxY;
            double z2 = box.maxZ;

            switch (target) {
                case NORTH: {
                    break;
                }
                case SOUTH: {
                    double temp = x1;
                    x1 = 1 - y2;
                    x2 = 1 - temp;
                    break;
                }
                case WEST: {
                    double tempX1 = x1;
                    double tempX2 = x2;
                    x1 = 1 - y2;
                    x2 = 1 - y1;
                    y1 = tempX1;
                    y2 = tempX2;
                    break;
                }
                case EAST: {
                    double tempX1 = x1;
                    double tempX2 = x2;
                    x1 = y1;
                    x2 = y2;
                    y1 = 1 - tempX2;
                    y2 = 1 - tempX1;
                    break;
                }
                default:
                    break;
            }

            rotatedShape = Shapes.or(rotatedShape, Shapes.box(x1, y1, z1, x2, y2, z2));
        }
        return rotatedShape;
    }

    public static int getXRotation(Direction direction) {
        return switch (direction) {
            case DOWN -> 180;
            case UP -> 0;
            default -> 90;
        };
    }

    public static int getYRotation(Direction direction) {
        return switch (direction) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }

    public static void rotateByFacing(PoseStack pPoseStack, Direction facing){
        switch (facing) {
            case DOWN:
                pPoseStack.mulPose(Axis.XP.rotationDegrees(180));
                break;
            case UP:
                pPoseStack.mulPose(Axis.XP.rotationDegrees(0));
                break;
            case NORTH:
                pPoseStack.mulPose(Axis.XP.rotationDegrees(270));
                break;
            case SOUTH:
                pPoseStack.mulPose(Axis.XP.rotationDegrees(90));
                break;
            case WEST:
                pPoseStack.mulPose(Axis.ZP.rotationDegrees(90));
                break;
            case EAST:
                pPoseStack.mulPose(Axis.ZP.rotationDegrees(270));
                break;
        }
    }
}