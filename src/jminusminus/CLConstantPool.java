// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Representation of a class' constant pool.
 */
class CLConstantPool {
    // Index of the next item into the constant pool.
    private int cpIndex;

    // List of constant pool items.
    private ArrayList<CLCPInfo> cpItems;

    /**
     * Constructs an empty constant pool.
     */
    public CLConstantPool() {
        cpIndex = 1;
        cpItems = new ArrayList<CLCPInfo>();
    }

    /**
     * Returns the size of the constant pool.
     *
     * @return the size of the constant pool.
     */
    public int size() {
        return cpItems.size();
    }

    /**
     * Returns the index of the specified item in the constant pool or -1.
     *
     * @param cpInfo item to find.
     * @return the index of the specified item in the constant pool or -1.
     */
    public int find(CLCPInfo cpInfo) {
        int index = cpItems.indexOf(cpInfo);
        return (index != -1) ? cpItems.get(index).cpIndex : index;
    }

    /**
     * Returns the constant pool item at the specified index or null.
     *
     * @param i constant pool index.
     * @return the constant pool item at the specified index or null.
     */
    public CLCPInfo cpItem(int i) {
        if (((i - 1) < 0) || ((i - 1) >= cpItems.size())) {
            return null;
        }
        return cpItems.get(i - 1);
    }

    /**
     * Adds the specified (non-null) item to the constant pool and returns its index.
     *
     * @param cpInfo the item to add.
     * @return constant pool index of the item.
     */
    public int addCPItem(CLCPInfo cpInfo) {
        cpInfo.cpIndex = cpIndex++;
        cpItems.add(cpInfo);

        // long and double, with their lower and higher words, are treated by JVM as two items in
        // the constant pool. We have a single representation for each, so we add a null as
        // a placeholder in the second slot.
        if ((cpInfo instanceof CLConstantLongInfo) || (cpInfo instanceof CLConstantDoubleInfo)) {
            cpIndex++;
            cpItems.add(null);
        }
        return cpInfo.cpIndex;
    }

    /**
     * Writes the contents of the constant pool to the specified output stream.
     *
     * @param out output stream.
     * @throws IOException if an error occurs while writing.
     */
    public void write(CLOutputStream out) throws IOException {
        for (CLCPInfo cpInfo : cpItems) {
            if (cpInfo != null) {
                cpInfo.write(out);
            }
        }
    }

    /**
     * Returns the constant pool index of a singleton instance of CLConstantClassInfo.
     *
     * @param className class or interface name in internal form.
     * @return constant pool index.
     */
    public int constantClassInfo(String className) {
        CLCPInfo c = new CLConstantClassInfo(constantUtf8Info(className));
        return findOrAdd(c);
    }

    /**
     * Returns the constant pool index of a singleton instance of CLConstantFieldRefInfo.
     *
     * @param className class or interface name in internal form.
     * @param name      name of the field.
     * @param type      descriptor of the field.
     * @return constant pool index.
     */
    public int constantFieldRefInfo(String className, String name, String type) {
        CLCPInfo c = new CLConstantFieldRefInfo(constantClassInfo(className),
                constantNameAndTypeInfo(name, type));
        return findOrAdd(c);
    }

    /**
     * Returns the constant pool index of a singleton instance of CLConstantMethodRefInfo.
     *
     * @param className class or interface name in internal form.
     * @param name      name of the method.
     * @param type      descriptor of the method.
     * @return constant pool index.
     */
    public int constantMethodRefInfo(String className, String name, String type) {
        CLCPInfo c = new CLConstantMethodRefInfo(constantClassInfo(className),
                constantNameAndTypeInfo(name, type));
        return findOrAdd(c);
    }

    /**
     * Returns the constant pool index of a singleton instance of CLConstantInterfaceMethodRefInfo.
     *
     * @param className class or interface name in internal form.
     * @param name      name of the method.
     * @param type      descriptor of the method.
     * @return constant pool index.
     */
    public int constantInterfaceMethodRefInfo(String className, String name, String type) {
        CLCPInfo c = new CLConstantInterfaceMethodRefInfo(
                constantClassInfo(className), constantNameAndTypeInfo(name, type));
        return findOrAdd(c);
    }

    /**
     * Returns the constant pool index of a singleton instance of CLConstantStringInfo.
     *
     * @param s the constant string value.
     * @return constant pool index.
     */
    public int constantStringInfo(String s) {
        CLCPInfo c = new CLConstantStringInfo(constantUtf8Info(s));
        return findOrAdd(c);
    }

    /**
     * Returns the constant pool index of a singleton instance of CLConstantIntegerInfo.
     *
     * @param i the constant int value.
     * @return constant pool index.
     */
    public int constantIntegerInfo(int i) {
        CLCPInfo c = new CLConstantIntegerInfo(i);
        return findOrAdd(c);
    }

    /**
     * Returns the constant pool index of a singleton instance of CLConstantFloatInfo.
     *
     * @param f the constant floating-point value.
     * @return constant pool index.
     */
    public int constantFloatInfo(float f) {
        CLCPInfo c = new CLConstantFloatInfo(f);
        return findOrAdd(c);
    }

    /**
     * Returns the constant pool index of a singleton instance of CLConstantLongInfo.
     *
     * @param l the constant long value.
     * @return constant pool index.
     */
    public int constantLongInfo(long l) {
        CLCPInfo c = new CLConstantLongInfo(l);
        return findOrAdd(c);
    }

    /**
     * Returns the constant pool index of a singleton instance of CLConstantDoubleInfo.
     *
     * @param d the constant double value.
     * @return constant pool index.
     */
    public int constantDoubleInfo(double d) {
        CLCPInfo c = new CLConstantDoubleInfo(d);
        return findOrAdd(c);
    }

    /**
     * Returns the constant pool index of a singleton instance of CLConstantNameAndTypeInfo.
     *
     * @param name field or method name.
     * @param type field or method type descriptor.
     * @return constant pool index.
     */
    public int constantNameAndTypeInfo(String name, String type) {
        CLCPInfo c = new CLConstantNameAndTypeInfo(constantUtf8Info(name), constantUtf8Info(type));
        return findOrAdd(c);
    }

    /**
     * Returns the constant pool index of a singleton instance of CLConstantUtf8Info.
     *
     * @param s the constant string value.
     * @return constant pool index.
     */
    public int constantUtf8Info(String s) {
        CLCPInfo c = new CLConstantUtf8Info(s.getBytes());
        return findOrAdd(c);
    }

    // Returns the index of the specified item in the constant pool. If the item does not exist,
    // adds the item to the pool and return its (new) index.
    private int findOrAdd(CLCPInfo cpInfo) {
        int index = find(cpInfo);
        if (index == -1) {
            index = addCPItem(cpInfo);
        }
        return index;
    }
}
