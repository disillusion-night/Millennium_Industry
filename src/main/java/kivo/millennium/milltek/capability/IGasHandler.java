package kivo.millennium.milltek.capability;
/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */


import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.fluids.*;
import org.jetbrains.annotations.NotNull;

@AutoRegisterCapability
public interface IGasHandler
{
    enum FluidAction {
        EXECUTE, SIMULATE;

        public boolean execute() {
            return this == EXECUTE;
        }

        public boolean simulate() {
            return this == SIMULATE;
        }
    }

    /**
     * Returns the number of fluid storage units ("tanks") available
     *
     * @return The number of tanks available
     */
    int getTanks();

    /**
     * Returns the FluidStack in a given tank.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This FluidStack <em>MUST NOT</em> be modified. This method is not for
     * altering internal contents. Any implementers who are able to detect modification via this method
     * should throw an exception. It is ENTIRELY reasonable and likely that the stack returned here will be a copy.
     * </p>
     *
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED FLUIDSTACK</em></strong>
     * </p>
     *
     * @param tank Tank to query.
     * @return FluidStack in a given tank. FluidStack.EMPTY if the tank is empty.
     */
    @NotNull
    FluidStack getGasInTank(int tank);

    /**
     * Retrieves the maximum fluid amount for a given tank.
     *
     * @param tank Tank to query.
     * @return     The maximum fluid amount held by the tank.
     */
    int getTankCapacity(int tank);


    boolean isGasValid(int tank, @NotNull FluidStack stack);

    int fill(FluidStack resource, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction action);

    FluidStack drain(FluidStack resource, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction action);

    FluidStack drain(int maxDrain, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction action);

}
