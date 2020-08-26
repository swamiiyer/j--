// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

/**
 * Implements register allocation using graph coloring algorithm.
 */
public class NGraphRegisterAllocator extends NRegisterAllocator {
    /**
     * Constructs an NGraphRegisterAllocator object.
     *
     * @param cfg an instance of a control flow graph.
     */
    public NGraphRegisterAllocator(NControlFlowGraph cfg) {
        super(cfg);
    }

    /**
     * {@inheritDoc}
     */
    public void allocation() {
        buildIntervals();
    }
}
