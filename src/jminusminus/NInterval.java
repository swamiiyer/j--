// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Represents a lifetime interval, recording the interval of LIR code for which the corresponding
 * virtual register contains a useful value.
 */
class NInterval implements Comparable<NInterval> {
    // Control flow graph instance.
    private NControlFlowGraph cfg;

    /**
     * The virtual register id corresponding to the index in the array list of NIntervals used by
     * register allocation
     */
    public int vRegId;

    /**
     * All live ranges for this virtual register
     */
    public ArrayList<NRange> ranges;

    /**
     * All use positions (in LIR) and their types for this virtual register
     */
    public TreeMap<Integer, InstructionType> usePositions;

    /**
     * The NPhyicalRegister assigned to this interval. If an interval ends up needing more than
     * one physical register it is split.
     */
    public NPhysicalRegister pRegister;

    /**
     * Whether or not to spill.
     */
    public boolean spill;

    /**
     * From offset.
     */
    public OffsetFrom offsetFrom;

    /**
     * Offset.
     */
    public int offset;

    /**
     * Parent of this interval.
     */
    public NInterval parent;

    /**
     * Children of this interval.
     */
    public ArrayList<NInterval> children;

    /**
     * Constructs an NInterval object.
     *
     * @param vRegId program counter.
     * @param cfg    the control flow graph.
     */
    public NInterval(int vRegId, NControlFlowGraph cfg) {
        this.vRegId = vRegId;
        this.cfg = cfg;
        ranges = new ArrayList<NRange>();
        usePositions = new TreeMap<Integer, InstructionType>();
        spill = false;
        pRegister = null;
        offset = -1;
        parent = null;
        children = new ArrayList<NInterval>();
    }

    /**
     * This second constructor is used in instantiating children of a split interval.
     *
     * @param virtualRegID program counter.
     * @param cfg          The control flow graph.
     * @param childRanges  The instruction ranges for this child.
     * @param parent       The parent interval.
     */
    public NInterval(int virtualRegID, NControlFlowGraph cfg, ArrayList<NRange> childRanges,
                     NInterval parent) {
        this.cfg = cfg;
        this.ranges = childRanges;
        this.usePositions = new TreeMap<Integer, InstructionType>();
        this.vRegId = virtualRegID;
        this.parent = parent;
        this.children = new ArrayList<NInterval>();
        spill = false;
        offset = -1;
    }

    /**
     * Adds a new range to the existing ranges. If the range overlaps then the old start position
     * will be given the new start range position.
     *
     * @param newNRange the NRange to add
     */
    public void addOrExtendNRange(NRange newNRange) {
        if (!ranges.isEmpty()) {
            if (newNRange.stop + 5 == ranges.get(0).start ||
                    newNRange.rangeOverlaps(ranges.get(0))) {
                ranges.get(0).start = newNRange.start;
            } else {
                ranges.add(0, newNRange);
            }
        } else {
            ranges.add(newNRange);
        }
    }

    /**
     * Returns the very first position where an intersection with another interval occurs.
     *
     * @param otherInterval the interval to compare against for intersection.
     * @return the position where the intersection begins.
     */
    public int nextIntersection(NInterval otherInterval) {
        int a = -1, b = -2;
        for (NRange r : this.ranges) {
            if (otherInterval.isLiveAt(r.start)) {
                a = r.start;
                break;
            }
        }
        for (NRange r : otherInterval.ranges) {
            if (this.isLiveAt(r.start)) {
                b = r.start;
                break;
            }
        }
        if (a >= 0 && b >= 0) {
            return a <= b ? a : b;
        } else {
            return a > b ? a : b;
        }
    }

    /**
     * Returns the next use position of this interval after the first range start of the foreign
     * interval. If there is no such use, then the first use position is returned to preserve
     * data flow (in case of loops).
     *
     * @param currInterval the interval with starting point after which we want to find the next
     *                     usage of this one.
     * @return the next use position.
     */
    public int nextUsageOverlapping(NInterval currInterval) {
        int psi = currInterval.firstNRangeStart();
        if (usePositions.ceilingKey(psi) != null) {
            return usePositions.ceilingKey(psi);
        } else if (!usePositions.isEmpty()) {
            return usePositions.firstKey();
        } else {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Returns the first use position in this interval.
     *
     * @return the first use position in this interval.
     */
    public int firstUsage() {
        return usePositions.firstKey();
    }

    /**
     * Sets the start value of the very first range. Note: There will always be at least one
     * range before this method is used by the NRegisterAllocator.buildIntervals() method.
     *
     * @param newStart the value to which the first range's start will be set.
     */
    public void newFirstRangeStart(int newStart) {
        if (!ranges.isEmpty()) {
            ranges.get(0).start = newStart;
        }
    }

    /**
     * Registers a use (read or write).
     *
     * @param index the site of the use.
     * @param type  the instruction type.
     */
    public void addUsePosition(Integer index, InstructionType type) {
        usePositions.put(index, type);
    }

    /**
     * Returns true if this virtual register is alive at a given index, and false otherwise.
     *
     * @param atIndex the index at which to see if this register is live.
     * @return true if this virtual register is alive at a given index, and false otherwise.
     */
    public boolean isLiveAt(int atIndex) {
        for (NRange r : ranges) {
            if (atIndex >= r.start && atIndex < r.stop) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the range in this interval in which the LIR instruction with the given id is live,
     * or null.
     *
     * @param id the LIR instruction id.
     * @return the range in this interval in which the LIR instruction with the given id is live,
     * or null.
     */
    private NRange liveRangeAt(int id) {
        for (NRange r : ranges) {
            if (id >= r.start && id <= r.stop) {
                return r;
            }
        }
        return null;
    }

    /**
     * Writes the interval information to STDOUT.
     *
     * @param p for pretty printing with indentation.
     */
    public void writeToStdOut(PrettyPrinter p) {
        if (cfg.registers.get(vRegId) != null) {
            String s = cfg.registers.get(vRegId).name() + ": ";
            for (NRange r : ranges) {
                s += r.toString() + " ";
            }
            if (pRegister != null) {
                s += "-> " + pRegister.name();
            } else {
                s += "-> None";
            }
            if (spill) {
                if (offsetFrom == OffsetFrom.FP) {
                    s += " [frame:" + offset + "]";
                } else {
                    s += " [stack:" + offset + "]";
                }
            }
            p.printf("%s\n", s);
            for (NInterval child : this.children) {
                child.writeToStdOut(p);
            }
        } else if (this.isChild()) {
            String s = "\tv" + this.vRegId + ": ";
            for (NRange r : ranges) {
                s += r.toString() + " ";
            }
            if (pRegister != null) {
                s += "-> " + pRegister.name();
            } else {
                s += "-> None";
            }
            if (offsetFrom == OffsetFrom.FP) {
                s += " [frame:" + offset + "]";
            } else {
                s += " [stack:" + offset + "]";
            }
            p.printf("%s\n", s);
            for (NInterval child : this.children) {
                child.writeToStdOut(p);
            }
        }
    }

    /**
     * Returns the start position for the first range.
     *
     * @return the start position for the first range.
     */
    public int firstNRangeStart() {
        return ranges.isEmpty() ? -1 : ranges.get(0).start;
    }

    /**
     * Returns the stop position for the last range.
     *
     * @return the stop position for the last range.
     */
    public int lastNRangeStop() {
        return ranges.isEmpty() ? -1 : ranges.get(ranges.size() - 1).stop;
    }

    /**
     * Returns a comparison of this interval with other.
     *
     * @param other interval to compare to.
     * @return a comparison of this interval with other.
     */
    public int compareTo(NInterval other) {
        return this.firstNRangeStart() - other.firstNRangeStart();
    }

    /**
     * Returns true if this interval is the same as other, and false otherwise.
     *
     * @param other the interval we are comparing with.
     * @return true if this interval is the same as other, and false otherwise.
     */
    public boolean equals(NInterval other) {
        return (this.vRegId == other.vRegId);
    }

    /**
     * Splits the current interval at the given index. Responsible for splitting a range if the
     * index falls on one, moving remaining ranges over to child, and moving appropriate
     * usePositions over to the child.
     *
     * @param idx the index at which this interval is to be split
     * @return the child interval which is to be sorted onto unhandled. If there was no child
     * created in the case of a pruning this interval is returned.
     */
    public NInterval splitAt(int idx) {
        ArrayList<NRange> childsRanges = new ArrayList<NRange>();
        if (this.isLiveAt(idx)) { // means split falls on a range
            // Assumptions: if a range is LIVE on an index, then there exist usePositions at or
            // before the index within this same range.
            NRange liveRange = this.liveRangeAt(idx);
            int splitTo = idx;
            splitTo = usePositions.ceilingKey(idx);
            childsRanges.add((liveRange.splitRange(splitTo, idx - 5)));
        }

        // The following two for loops take care of any untouched ranges which start after the
        // split position and must be moved to the child interval.
        for (NRange r : ranges) {
            if (r.start > idx) {
                childsRanges.add(r);
            }
        }
        for (NRange r : childsRanges) {
            ranges.remove(r);
        }

        NInterval child = new NInterval(cfg.maxIntervals++, cfg, childsRanges, getParent());
        cfg.registers.add(null); // expand size of cfg.registers to avoid NPE when printing

        // Transfer remaining use positions.
        while (this.usePositions.ceilingKey(idx) != null) {
            child.usePositions.put(usePositions.ceilingKey(idx),
                    usePositions.remove(usePositions.ceilingKey(idx)));
        }
        getParent().children.add(child);

        return child;
    }

    /**
     * Returns the parent interval.
     *
     * @return the parent interval.
     */
    private NInterval getParent() {
        return parent != null ? parent : this;
    }

    /**
     * Returns the child interval at the given instruction index.
     *
     * @param idx the instruction index.
     * @return the child interval at the given instruction index.
     */
    public NInterval childAt(int idx) {
        for (NInterval child : children) {
            if (child.isLiveAt(idx)) {
                return child;
            }
        }
        return this;
    }

    /**
     * Returns the child of this interval which is live or ends before the given basic block's end.
     *
     * @param b the basic block.
     * @return the child of this interval which ends at or nearest (before) this basic block's
     * end (last lir instruction index).
     */
    public NInterval childAtOrEndingBefore(NBasicBlock b) {
        int idx = b.getLastLIRInstId();
        for (NInterval child : children) {
            if (child.isLiveAt(idx)) {
                return child;
            }
        }
        NInterval tmp = this;
        int highestEndingAllowed = b.getFirstLIRInstId();
        for (NInterval child : children) {
            // Get the child which ends before idx but also ends closest to idx.
            if (child.lastNRangeStop() < idx && child.lastNRangeStop() > highestEndingAllowed) {
                tmp = child;
                highestEndingAllowed = tmp.lastNRangeStop();
            }
        }
        return tmp;
    }

    /**
     * Returns the child of this interval which is live or starts after the given basic block's
     * start.
     *
     * @param b the basic block.
     * @return the child of this interval which starts at or nearest (after) this basic block's
     * start (fist lir instruction index).
     */
    public NInterval childAtOrStartingAfter(NBasicBlock b) {
        int idx = b.getFirstLIRInstId();
        for (NInterval child : children) {
            if (child.isLiveAt(idx)) {
                return child;
            }
        }
        NInterval tmp = this;
        int lowestStartAllowed = b.getLastLIRInstId(); // block's end
        for (NInterval child : children) {
            if (child.firstNRangeStart() > idx && child.firstNRangeStart() < lowestStartAllowed) {
                tmp = child;
                lowestStartAllowed = tmp.firstNRangeStart();
            }
        }
        return tmp;
    }

    /**
     * Returns the basic block in which this interval's start position falls.
     *
     * @return the basic block in which this interval's start position falls.
     */
    public int startsAtBlock() {
        for (NBasicBlock b : this.cfg.basicBlocks) {
            if (this.firstNRangeStart() >= b.getFirstLIRInstId()
                    && this.firstNRangeStart() <= b.getLastLIRInstId())
                return b.id;
        }
        return -1; // this will never happen
    }

    /**
     * Returns the basic block in which this interval's end position falls.
     *
     * @return the basic block in which this interval's end position falls.
     */
    public int endsAtBlock() {
        for (NBasicBlock b : this.cfg.basicBlocks) {
            if (this.lastNRangeStop() >= b.getFirstLIRInstId()
                    && this.lastNRangeStop() <= b.getLastLIRInstId()) {
                return b.id;
            }
        }
        return -1; // this will never happen
    }

    /**
     * Assigns an offset to this interval (if one hasn't been already assigned). Assigns that
     * same offset to any (newly created) children.
     */
    public void spill() {
        this.spill = true;
        if (this.offset == -1) {
            this.offset = cfg.offset++;
            this.offsetFrom = OffsetFrom.SP;
        }
        for (NInterval child : children) {
            if (child.offset == -1) {
                child.offset = this.offset;
                child.offsetFrom = this.offsetFrom;
            }
        }
    }

    /**
     * Returns true if this is a child interval, and false otherwise.
     *
     * @return true if this is a child interval, and false otherwise.
     */
    public boolean isChild() {
        return parent != null;
    }

    /**
     * Returns true if this is a parent interval, and false otherwise.
     *
     * @return true if this is a parent interval, and false otherwise.
     */
    public boolean isParent() {
        return !this.children.isEmpty();
    }
}

/**
 * The types of stack pointers.
 */
enum OffsetFrom {
    FP, SP
};

/**
 * The types of possible uses.
 */
enum InstructionType {
    read, write
};

/**
 * Representation of a liveness range for an interval.
 */
class NRange implements Comparable<NRange> {
    /**
     * The range's start position.
     */
    public int start;

    /**
     * The range's stop position.
     */
    public int stop;

    /**
     * Constructs a liveness range extending from start to stop (positions in the code).
     *
     * @param start start position.
     * @param stop  stop position.
     */
    public NRange(int start, int stop) {
        this.start = start;
        this.stop = stop;
    }

    /**
     * Truncates the current range to newStop and returns the remainder as a new range.
     *
     * @param newStart the new start position.
     * @param newStop  the split position.
     * @return the remainder of this range starting at newStart and ending at its old stop
     * position.
     */
    public NRange splitRange(int newStart, int newStop) {
        NRange newRange = new NRange(newStart, stop);
        this.stop = newStop;
        return newRange;
    }

    /**
     * Returns true if this range overlaps with other, and false otherwise.
     *
     * @param other the other range.
     * @return true if this range overlaps with other, and false otherwise.
     */
    public boolean rangeOverlaps(NRange other) {
        return other.start <= this.stop && this.start <= other.stop;
    }

    /**
     * Returns a comparison of this range and other by their start positions.
     *
     * @param other the other range.
     * @return a comparison of this range and other by their start positions.
     */
    public int compareTo(NRange other) {
        return this.start - other.start;
    }

    /**
     * Returns a string representation of this range.
     *
     * @return a string representation of this range.
     */
    public String toString() {
        return "[" + start + ", " + stop + "]";
    }
}
