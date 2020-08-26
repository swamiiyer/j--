// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

/**
 * Implements register allocation using linear scan algorithm.
 */
public class NLinearRegisterAllocator extends NRegisterAllocator {
    /**
     * Constructs an NLinearRegisterAllocator object.
     *
     * @param cfg an instance of a control flow graph.
     */
    public NLinearRegisterAllocator(NControlFlowGraph cfg) {
        super(cfg);
    }

    /**
     * {@inheritDoc}
     */
    public void allocation() {
        buildIntervals();
    }
}
